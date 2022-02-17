package me.leon

import kotlin.math.pow

typealias call = () -> Unit

/** @link https://github.com/pieroxy/lz-string */
object LzString {
    private const val keyStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/="
    private const val keyStrUri =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+-$"

    private data class Data(var value: Char = '0', var position: Int = 0, var index: Int = 1)

    private inline fun String.charLess256(yes: call, no: call) {
        if (this[0].toInt() < 256) {
            yes.invoke()
        } else {
            no.invoke()
        }
    }

    private fun Int.power() = 1 shl this
    private fun _compress(
        source: String,
        bitsPerChar: Int,
        getCharFromInt: (code: Int) -> Char
    ): String {
        var context_c: String
        var value: Int
        var context_w = ""
        var context_wc: String
        val context_dictionary = mutableMapOf<String, Int>()
        val context_dictionaryToCreate = mutableMapOf<String, Boolean>()
        var context_enlargeIn = 2.0 // Compensate for the first entry which should not count
        var context_dictSize = 3
        val context_data = mutableListOf<Char>()
        var context_numBits = 2
        var context_data_val = 0
        var context_data_position = 0
        fun minusEnlargeIn() {
            context_enlargeIn--
            if (context_enlargeIn == 0.0) {
                context_enlargeIn = 2.0.pow(context_numBits.toDouble())
                context_numBits++
            }
        }

        fun checkPosition() {
            if (context_data_position == bitsPerChar - 1) {
                context_data_position = 0
                context_data.add(getCharFromInt(context_data_val))
                context_data_val = 0
            } else {
                context_data_position++
            }
        }

        fun processContextWordInCreateDictionary() {
            context_w.charLess256({
                repeat(context_numBits) {
                    context_data_val = context_data_val shl 1
                    checkPosition()
                }
                value = context_w[0].toInt()
                repeat(8) {
                    context_data_val = context_data_val shl 1 or (value and 1)
                    checkPosition()
                    value = value shr 1
                }
            }) {
                value = 1
                repeat(context_numBits) {
                    context_data_val = context_data_val shl 1 or value
                    checkPosition()
                    value = 0
                }
                value = context_w[0].toInt()
                repeat(16) {
                    context_data_val = context_data_val shl 1 or (value and 1)
                    checkPosition()
                    value = value shr 1
                }
            }
            minusEnlargeIn()
            context_dictionaryToCreate.remove(context_w)
        }

        fun processContextWord() {
            if (context_dictionaryToCreate.containsKey(context_w)) {
                processContextWordInCreateDictionary()
            } else {
                value = context_dictionary[context_w]!! // not be empty?
                repeat(context_numBits) {
                    context_data_val = context_data_val shl 1 or (value and 1)
                    checkPosition()
                    value = value shr 1
                }
            }
            minusEnlargeIn()
        }
        source.forEach {
            context_c = it.toString()
            // char in dictionary
            context_dictionary[context_c]
                ?: kotlin.run {
                    context_dictionary[context_c] = context_dictSize++
                    context_dictionaryToCreate[context_c] = true
                }
            context_wc = context_w + context_c
            if (context_dictionary.contains(context_wc)) {
                context_w = context_wc
            } else {
                processContextWord()
                // Add wc to the dictionary.
                context_dictionary[context_wc] = context_dictSize++
                context_w = context_c
            }
        }
        // Output the code for w.
        if (context_w.isNotBlank()) {
            processContextWord()
        }
        // Mark the end of the stream
        value = 2
        repeat(context_numBits) {
            context_data_val = context_data_val shl 1 or (value and 1)
            if (context_data_position == bitsPerChar - 1) {
                context_data_position = 0
                context_data.add(getCharFromInt(context_data_val))
                context_data_val = 0
            } else {
                context_data_position++
            }
            value = value shr 1
        }
        // Flush the last char
        while (true) {
            context_data_val = context_data_val shl 1
            if (context_data_position == bitsPerChar - 1) {
                context_data.add(getCharFromInt(context_data_val))
                break
            } else context_data_position++
        }
        return context_data.joinToString("")
    }

    private val Int.string
        get() = this.toChar().toString()
    private fun _decompress(
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
        var next = 0
        fun doPower(initBits: Int, initPower: Int, initMaxPowerFactor: Int, mode: Int = 0) {
            bits = initBits
            maxpower = initMaxPowerFactor.power()
            power = initPower
            while (power != maxpower) {
                resb = data.value.toInt() and data.position
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

    fun compress(source: String) = _compress(source, 16) { it.toChar() }
    fun decompress(compressed: String) =
        if (compressed.isBlank()) null else _decompress(compressed.length, 32768) { compressed[it] }

    fun decompressFromEncodedURIComponent(input: String) =
        when {
            input.isBlank() -> ""
            else -> _decompress(input.length, 32) { keyStrUri.indexOf(input[it]).toChar() }
        }

    fun compressToEncodedURIComponent(input: String) = _compress(input, 6) { keyStrUri[it] }

    fun compressToBase64(input: String): String {
        val res = _compress(input, 6) { keyStr[it] }
        return when (res.length % 4) { // To produce valid Base64
            0 -> res
            1 -> "$res==="
            2 -> "$res=="
            3 -> "$res="
            else -> throw IllegalStateException("Illegal base64url string!")
        }
    }

    fun decompressFromBase64(input: String) =
        when {
            input.isBlank() -> null
            else -> _decompress(input.length, 32) { keyStr.indexOf(input[it]).toChar() }
        }

    fun decompressFromUTF16(input: String) =
        when {
            input.isBlank() -> null
            else -> _decompress(input.length, 16384) { (input[it].toInt() - 32).toChar() }
        }

    fun compressToUTF16(input: String) = _compress(input, 15) { (it + 32).toChar() } + " "
}
