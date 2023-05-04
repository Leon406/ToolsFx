package me.leon.toolsfx.plugin.compress

import kotlin.math.pow

typealias call = () -> Unit

/** @link https://github.com/pieroxy/lz-string */
object LzString {
    private const val keyStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/="
    private const val keyStrUri =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+-$"

    private val Int.string
        get() = this.toChar().toString()

    private fun Int.power() = 1 shl this

    private data class Data(var value: Char = '0', var position: Int = 0, var index: Int = 1)

    private inline fun String.charLess256(yes: call, no: call) {
        if (this[0].code < 256) {
            yes.invoke()
        } else {
            no.invoke()
        }
    }

    private fun compress(
        source: String,
        bitsPerChar: Int,
        getCharFromInt: (code: Int) -> Char
    ): String {
        var contextC: String
        var value: Int
        var contextW = ""
        var contextWc: String
        val contextDictionary = mutableMapOf<String, Int>()
        val contextDictionaryToCreate = mutableMapOf<String, Boolean>()
        var contextEnlargeIn = 2.0 // Compensate for the first entry which should not count
        var contextDictSize = 3
        val contextData = mutableListOf<Char>()
        var contextNumbits = 2
        var contextDataVal = 0
        var contextDataPosition = 0
        fun minusEnlargeIn() {
            contextEnlargeIn--
            if (contextEnlargeIn == 0.0) {
                contextEnlargeIn = 2.0.pow(contextNumbits.toDouble())
                contextNumbits++
            }
        }

        fun checkPosition() {
            if (contextDataPosition == bitsPerChar - 1) {
                contextDataPosition = 0
                contextData.add(getCharFromInt(contextDataVal))
                contextDataVal = 0
            } else {
                contextDataPosition++
            }
        }

        fun processContextWordInCreateDictionary() {
            contextW.charLess256({
                repeat(contextNumbits) {
                    contextDataVal = contextDataVal shl 1
                    checkPosition()
                }
                value = contextW[0].code
                repeat(8) {
                    contextDataVal = contextDataVal shl 1 or (value and 1)
                    checkPosition()
                    value = value shr 1
                }
            }) {
                value = 1
                repeat(contextNumbits) {
                    contextDataVal = contextDataVal shl 1 or value
                    checkPosition()
                    value = 0
                }
                value = contextW[0].code
                repeat(16) {
                    contextDataVal = contextDataVal shl 1 or (value and 1)
                    checkPosition()
                    value = value shr 1
                }
            }
            minusEnlargeIn()
            contextDictionaryToCreate.remove(contextW)
        }

        fun processContextWord() {
            if (contextDictionaryToCreate.containsKey(contextW)) {
                processContextWordInCreateDictionary()
            } else {
                value = contextDictionary[contextW]!! // not be empty?
                repeat(contextNumbits) {
                    contextDataVal = contextDataVal shl 1 or (value and 1)
                    checkPosition()
                    value = value shr 1
                }
            }
            minusEnlargeIn()
        }
        source.forEach {
            contextC = it.toString()
            // char in dictionary
            contextDictionary[contextC]
                ?: kotlin.run {
                    contextDictionary[contextC] = contextDictSize++
                    contextDictionaryToCreate[contextC] = true
                }
            contextWc = contextW + contextC
            if (contextDictionary.contains(contextWc)) {
                contextW = contextWc
            } else {
                processContextWord()
                // Add wc to the dictionary.
                contextDictionary[contextWc] = contextDictSize++
                contextW = contextC
            }
        }
        // Output the code for w.
        if (contextW.isNotBlank()) {
            processContextWord()
        }
        // Mark the end of the stream
        value = 2
        repeat(contextNumbits) {
            contextDataVal = contextDataVal shl 1 or (value and 1)
            if (contextDataPosition == bitsPerChar - 1) {
                contextDataPosition = 0
                contextData.add(getCharFromInt(contextDataVal))
                contextDataVal = 0
            } else {
                contextDataPosition++
            }
            value = value shr 1
        }
        // Flush the last char
        while (true) {
            contextDataVal = contextDataVal shl 1
            if (contextDataPosition == bitsPerChar - 1) {
                contextData.add(getCharFromInt(contextDataVal))
                break
            } else {
                contextDataPosition++
            }
        }
        return contextData.joinToString("")
    }

    @Suppress("ReturnCount")
    private fun decompress(
        length: Int,
        resetValue: Int,
        getNextValue: (idx: Int) -> Char
    ): String? {
        val builder = StringBuilder()
        val dictionary = mutableListOf(0.string, 1.string, 2.string)
        var bits = 0
        var maxpower: Int
        var power: Int
        val data = Data(getNextValue(0), resetValue, 1)
        var resb: Int
        var c = ""
        var w: String
        var entry: String
        var numBits = 3
        var enlargeIn = 4
        var dictSize = 4
        var next: Int
        fun doPower(initBits: Int, initPower: Int, initMaxPowerFactor: Int, mode: Int = 0) {
            bits = initBits
            maxpower = initMaxPowerFactor.power()
            power = initPower
            while (power != maxpower) {
                resb = data.value.code and data.position
                data.position = data.position shr 1
                if (data.position == 0) {
                    data.position = resetValue
                    data.value = getNextValue(data.index++)
                }
                bits = bits or (if (resb > 0) 1 else 0) * power
                power = power shl 1
            }
            when (mode) {
                0 -> Unit
                1 -> c = bits.string
                2 -> {
                    dictionary.add(dictSize++, bits.string)
                    next = (dictSize - 1)
                    enlargeIn--
                }
            }
        }

        fun checkEnlargeIn() {
            if (enlargeIn == 0) {
                enlargeIn = numBits.power()
                numBits++
            }
        }
        doPower(bits, 1, 2)
        next = bits
        when (next) {
            0 -> doPower(0, 1, 8, 1)
            1 -> doPower(0, 1, 16, 1)
            2 -> return ""
        }
        dictionary.add(3, c)
        w = c
        builder.append(w)
        while (true) {
            if (data.index > length) {
                return ""
            }
            doPower(0, 1, numBits)
            next = bits
            when (next) {
                0 -> doPower(0, 1, 8, 2)
                1 -> doPower(0, 1, 16, 2)
                2 -> return builder.toString()
            }
            checkEnlargeIn()
            entry =
                when {
                    dictionary.size > next -> dictionary[next]
                    next == dictSize -> w + w[0]
                    else -> return null
                }
            builder.append(entry)
            // Add w+entry[0] to the dictionary.
            dictionary.add(dictSize++, w + entry[0])
            enlargeIn--
            w = entry
            checkEnlargeIn()
        }
    }

    fun compress(source: String) = compress(source, 16) { it.toChar() }

    fun decompress(compressed: String) =
        if (compressed.isBlank()) null else decompress(compressed.length, 32_768) { compressed[it] }

    fun decompressFromEncodedURIComponent(input: String) =
        when {
            input.isBlank() -> ""
            else -> decompress(input.length, 32) { keyStrUri.indexOf(input[it]).toChar() }
        }

    fun compressToEncodedURIComponent(input: String) = compress(input, 6) { keyStrUri[it] }

    fun compressToBase64(input: String): String {
        val res = compress(input, 6) { keyStr[it] }
        return when (res.length % 4) { // To produce valid Base64
            0 -> res
            1 -> "$res==="
            2 -> "$res=="
            3 -> "$res="
            else -> error("Illegal base64url string!")
        }
    }

    fun decompressFromBase64(input: String) =
        when {
            input.isBlank() -> null
            else -> decompress(input.length, 32) { keyStr.indexOf(input[it]).toChar() }
        }

    fun decompressFromUTF16(input: String) =
        when {
            input.isBlank() -> null
            else -> decompress(input.length, 16_384) { (input[it].code - 32).toChar() }
        }

    fun compressToUTF16(input: String) = compress(input, 15) { (it + 32).toChar() } + " "
}
