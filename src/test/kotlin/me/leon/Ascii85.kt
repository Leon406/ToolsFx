package me.leon

import java.nio.charset.StandardCharsets

object Ascii85 {
    const val SIZE_OF_ENCODING_BLOCKS = 32
    const val SIZE_OF_DECODING_BLOCKS = 40
    const val BITS_AMOUNT_IN_BYTE = 8
    const val NULL_BYTE = "00000000"
    fun toAscii85(data: ByteArray): String {
        val binaryRepresentation =
            convertBinary(data.toString(), SIZE_OF_ENCODING_BLOCKS / BITS_AMOUNT_IN_BYTE)
        println("to: " + binaryRepresentation.str)
        return calculateEncodeBlocksValues(binaryRepresentation.str, binaryRepresentation.padAmount)
    }

    fun fromAscii85(data: String): ByteArray {
        var data = data
        data = data.replace("\\s".toRegex(), "")
        data = removeTagsForDecoding(data)
        val binaryRepresentation =
            convertBinary(data, SIZE_OF_DECODING_BLOCKS / BITS_AMOUNT_IN_BYTE)
        println("fr: " + binaryRepresentation.str)
        return calculateDecodeBlocksValues(binaryRepresentation.str, binaryRepresentation.padAmount)
    }

    private fun removeTagsForDecoding(str: String): String {
        val sb = StringBuilder(str)
        sb.delete(0, 2)
        sb.setLength(sb.length - 2)
        return sb.toString()
    }

    private fun convertBinary(str: String, blockSectorAmount: Int): StringAndPadAmount {
        val binary = StringBuilder()
        val data = str.toByteArray()
        for (b in data) {
            var `val` = b.toInt()
            for (i in 0..7) {
                binary.append(if (`val` and 128 == 0) 0 else 1)
                `val` = `val` shl 1
            }
        }
        val padAmount =
            if (data.size % blockSectorAmount == 0) 0
            else blockSectorAmount - data.size % blockSectorAmount
        binary.append(NULL_BYTE.repeat(padAmount))
        return StringAndPadAmount(binary.toString(), padAmount)
    }

    private fun calculateEncodeBlocksValues(str: String, padAmount: Int): String {
        val sb = StringBuilder()
        sb.append("<~") // starting symbols
        val numberOfBlocks = str.length / SIZE_OF_ENCODING_BLOCKS
        for (i in 0 until numberOfBlocks) {
            val startIndex = i * SIZE_OF_ENCODING_BLOCKS
            val endIndex = startIndex + 32
            var number = str.substring(startIndex, endIndex).toInt(2)
            for (powIndex in 4 downTo 0) {
                val multiplyFactor = Math.pow(85.0, powIndex.toDouble()).toInt()
                val ascii85Char = number / multiplyFactor
                sb.append((ascii85Char + 33).toChar())
                number -= ascii85Char * multiplyFactor
            }
        }
        sb.setLength(sb.length - padAmount)
        sb.append("~>") // ending symbols
        return sb.toString()
    }

    private fun calculateDecodeBlocksValues(str: String, padAmount: Int): ByteArray {
        val resultSb = StringBuilder()
        val numberOfBlocks = str.length / SIZE_OF_DECODING_BLOCKS
        for (i in 0 until numberOfBlocks) {
            var startIndex = i * SIZE_OF_DECODING_BLOCKS
            var endIndex = startIndex + 8
            var blockTotal = 0
            for (powIndex in 4 downTo 0) {
                val number = str.substring(startIndex, endIndex).toInt(2)
                val multiplyFactor = Math.pow(85.0, powIndex.toDouble()).toInt()
                val ascii85Char = number * multiplyFactor
                blockTotal += ascii85Char * multiplyFactor
                startIndex += 8
                endIndex += 8
            }
            resultSb.append(blockTotal)
        }
        resultSb.setLength(resultSb.length - padAmount)
        return resultSb.toString().toByteArray()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val str = "开发工具集合 by leon406@52pojie.cn"
        val encodedStr = toAscii85(str.toByteArray())
        println("encoded : $encodedStr")
        val decodedBytes = fromAscii85(encodedStr)
        val decodedString = String(decodedBytes, StandardCharsets.ISO_8859_1)
        println("decoded: $decodedString")
    }

    internal class StringAndPadAmount(var str: String, var padAmount: Int)
}
