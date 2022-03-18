package lianzhang

import java.util.Locale

/** https://zh.wikipedia.org/wiki/%E5%B8%8C%E5%B0%94%E5%AF%86%E7%A0%81 */
class Hill {
    private fun decrypt(ciphertext: String): String {
        val key2 = getReverseMatrix(key)
        println("加密密钥逆矩阵为：")
        showMatrix(key2)
        return decrypt(key2, ciphertext)
    }

    private fun decrypt(key2: Array<IntArray>, ciphertext: String): String {
        var temp1: Int
        var temp2: Int
        var temp3: Int
        val plain = StringBuilder()
        var i = 0
        while (i < ciphertext.length) {
            temp1 =
                key2[0][0] * (ciphertext[i] - 'A') +
                    key2[0][1] * (ciphertext[i + 1] - 'A') +
                    key2[0][2] * (ciphertext[i + 2] - 'A')
            temp2 =
                key2[1][0] * (ciphertext[i] - 'A') +
                    key2[1][1] * (ciphertext[i + 1] - 'A') +
                    key2[1][2] * (ciphertext[i + 2] - 'A')
            temp3 =
                key2[2][0] * (ciphertext[i] - 'A') +
                    key2[2][1] * (ciphertext[i + 1] - 'A') +
                    key2[2][2] * (ciphertext[i + 2] - 'A')
            plain.append(('A'.code + temp1 % 26).toChar())
            plain.append(('A'.code + temp2 % 26).toChar())
            plain.append(('A'.code + temp3 % 26).toChar())
            i += 3
        }
        return plain.toString()
    }

    private fun showMatrix(key2: Array<IntArray>) {
        for (i in key2.indices) {
            for (j in key2[0].indices) {
                print(key2[i][j].toString() + " ")
            }
            println()
        }
    }

    private fun getReverseMatrix(key2: Array<IntArray>): Array<IntArray> {
        val key = Array(key2.size) { IntArray(key2[0].size) }
        for (num in 0..2) for (i in 0..25) for (j in 0..25) for (k in 0..25) {
            if ((i * 17 + j * 21 + k * 2) % 26 == temp[num % 3] &&
                    (i * 17 + j * 18 + k * 2) % 26 == temp[(num + 2) % 3] &&
                    (i * 5 + j * 21 + k * 19) % 26 == temp[(num + 1) % 3]
            ) {
                key[num][0] = i
                key[num][1] = j
                key[num][2] = k
            }
        }
        return key
    }

    private fun getEncrypttext(plainttext: String): String {
        var temp1 = 0
        var temp2: Int
        var temp3: Int
        val cipertext = StringBuilder()
        var i = 0
        while (i < plainttext.length) {
            temp1 =
                key[0][0] * (plainttext[i] - 'A') +
                    key[0][1] * (plainttext[i + 1] - 'A') +
                    key[0][2] * (plainttext[i + 2] - 'A')
            temp2 =
                key[1][0] * (plainttext[i] - 'A') +
                    key[1][1] * (plainttext[i + 1] - 'A') +
                    key[1][2] * (plainttext[i + 2] - 'A')
            temp3 =
                key[2][0] * (plainttext[i] - 'A') +
                    key[2][1] * (plainttext[i + 1] - 'A') +
                    key[2][2] * (plainttext[i + 2] - 'A')
            cipertext.append(('A'.code + temp1 % 26).toChar())
            cipertext.append(('A'.code + temp2 % 26).toChar())
            cipertext.append(('A'.code + temp3 % 26).toChar())
            i += 3
        }
        return cipertext.toString()
    }

    companion object {
        var key = arrayOf(intArrayOf(17, 17, 5), intArrayOf(21, 18, 21), intArrayOf(2, 2, 19))
        var temp = intArrayOf(1, 0, 0)

        @JvmStatic
        fun main(args: Array<String>) {
            val hill = Hill()
            val plainttext = "paymoremoney"
            val ciphertext = hill.getEncrypttext(plainttext.uppercase(Locale.getDefault()))
            println(ciphertext)
            println()
            println("下面将上面的密文重新通过加密密钥来解密")
            val plainttext2 = hill.decrypt(ciphertext)
            println("解密后的明文为：")
            println(plainttext2.lowercase(Locale.getDefault()))
        }
    }
}
