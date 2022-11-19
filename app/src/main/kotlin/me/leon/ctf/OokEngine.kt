package me.leon.ctf

import java.io.*

/**
 * The [OokEngine] is an implementation for the brainfuck derivative Ook!.
 *
 * @author Fabian M.
 */
class OokEngine
@JvmOverloads
constructor(
    cells: Int = 64,
    out: OutputStream? = ByteArrayOutputStream(),
    inputStream: InputStream? = System.`in`
) : TrollScriptEngine(cells, out, inputStream) {
    /** The default length of a token. */
    override var defaultTokenLength = 9

    object Token : BrainFuckToken {
        override val start = ""
        override val next = "Ook. Ook?"
        override val pre = "Ook? Ook."
        override val plus = "Ook. Ook."
        override val minus = "Ook! Ook!"
        override val output = "Ook! Ook."
        override val input = "Ook. Ook!"
        override val bracketLeft = "Ook! Ook?"
        override val bracketRight = "Ook? Ook!"
        override val end = ""
    }

    /** Interprets the given string. */
    @Throws(Exception::class)
    override fun interpret(str: String): String {
        (outWriter as ByteArrayOutputStream).reset()

        // List with tokens.defaultTokenLenght
        val tokens: MutableList<String> = ArrayList()
        // It fine that all Ook! tokens are 9 characters long :)
        // So we aren't going to loop through all characters..
        while (charPointer < str.length) {
            if (str[charPointer].toString().isBlank()) {
                charPointer++
                continue
            }
            val token =
                if (charPointer + defaultTokenLength <= str.length) {
                    str.substring(charPointer, charPointer + defaultTokenLength)
                } else {
                    str.substring(charPointer, charPointer + (str.length - charPointer))
                }
            var b = false
            if (isValidToken(token, Token)) {
                tokens.add(token)
                charPointer += defaultTokenLength
                b = true
            }
            // If the token was invalid, b is false.
            if (!b) {
                if (charPointer + defaultTokenLength > str.length) {
                    charPointer += str.length - charPointer
                } else {
                    charPointer++
                }
            }
        }

        loopAllTokens(tokens)
        // Clear all data.
        initiate(data!!.size)
        return outWriter.toString()
    }

    private fun loopAllTokens(tokens: MutableList<String>) {
        // Loop through all tokens.
        var tokenPointer = 0
        while (tokenPointer < tokens.size) {
            when (tokens[tokenPointer]) {
                Token.next ->
                    dataPointer = (if (dataPointer == data!!.size - 1) 0 else dataPointer + 1)
                Token.pre ->
                    dataPointer = (if (dataPointer == 0) data!!.size - 1 else dataPointer - 1)
                Token.plus -> data!![dataPointer]++
                Token.minus -> data!![dataPointer]--
                Token.output -> outWriter.write(data!![dataPointer].toInt())
                Token.input -> data!![dataPointer] = consoleReader.read().toByte()
                Token.bracketLeft ->
                    if (data!![dataPointer].toInt() == 0) {
                        var level = 1
                        while (level > 0) {
                            tokenPointer++
                            if (tokens[tokenPointer] == Token.bracketLeft) {
                                level++
                            } else if (tokens[tokenPointer] == Token.bracketRight) {
                                level--
                            }
                        }
                    }
                Token.bracketRight ->
                    if (data!![dataPointer].toInt() != 0) {
                        var level = 1
                        while (level > 0) {
                            tokenPointer--
                            if (tokens[tokenPointer] == Token.bracketLeft) {
                                level--
                            } else if (tokens[tokenPointer] == Token.bracketRight) {
                                level++
                            }
                        }
                    }
            }
            tokenPointer++
        }
    }
}
