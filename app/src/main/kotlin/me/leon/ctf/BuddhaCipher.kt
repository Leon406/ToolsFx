package me.leon.ctf

import javax.crypto.Cipher
import me.leon.ext.crypto.makeCipher

private const val CIPHER = "AES/CBC/PKCS5Padding"
private const val KEY = "XDXDtudou@KeyFansClub^_^Encode!!"
private const val IV = "Potato@Key@_@=_="
private const val BYTE_MAP =
    "滅苦婆娑耶陀跋多漫都殿悉夜爍帝吉利阿無南那怛喝羯勝摩伽謹波者穆僧室藝尼瑟地彌菩提蘇醯盧呼舍佛參沙伊隸麼遮闍度" +
        "蒙孕薩夷迦他姪豆特逝朋輸楞栗寫數曳諦羅曰咒即密若般故不實真訶切一除能等是上明大神知三藐耨得依諸世槃涅竟究想夢倒顛離遠怖恐有礙心所以亦智道。集盡死老至"

private const val BYTE128MAP = "奢梵呐俱哆怯諳罰侄缽皤"

fun String.buddhaSays(): String {
    return makeCipher(CIPHER, KEY.toByteArray(), IV.toByteArray(), Cipher.ENCRYPT_MODE)
        .doFinal(toByteArray(Charsets.UTF_16LE))
        .run {
            fold(StringBuilder("佛曰：")) { acc, b ->
                    acc.apply {
                        if (b >= 0) append(BYTE_MAP[b.toInt()])
                        else append(BYTE128MAP.random()).append(BYTE_MAP[b.toInt() and 0xFF - 128])
                    }
                }
                .toString()
        }
}

fun String.buddhaExplain(): String {
    var flag = false
    val encryptedBytes =
        replace("佛曰：", "")
            .fold(mutableListOf<Byte>()) { acc, c ->
                acc.apply {
                    flag =
                        if (c in BYTE_MAP) {
                            acc.add((BYTE_MAP.indexOf(c) + if (flag) 128 else 0).toByte())
                            false
                        } else {
                            true
                        }
                }
            }
            .toByteArray()

    return makeCipher(CIPHER, KEY.toByteArray(), IV.toByteArray(), Cipher.DECRYPT_MODE)
        .doFinal(encryptedBytes)
        .toString(Charsets.UTF_16LE)
}
