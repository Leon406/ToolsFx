import java.nio.charset.StandardCharsets
import me.leon.encode.base.BASE64_DICT
import me.leon.encode.base.base64

fun main() {
    // http://cdn.esjson.com/esjson/js/encryencode/rabbit.js?v=202003311412
    //        test.test(new Rabbit());
    // 小于128位,不足 补 o
    val key = "6666666666666666".toByteArray(StandardCharsets.UTF_8)
    val data = "I Love You 521".toByteArray(StandardCharsets.UTF_8)
    val paddingData =
        ByteArray(16)
            .mapIndexed { index, byte -> if (index < data.size) data[index] else byte }
            .toByteArray()

    val rab = Rabbit()
    val ecrypt2 = rab.encryptMessage("I Love You 521", "6666666666666666", null, false)
    println(("Salted__" + String(ecrypt2)).base64())
    println(rab.decryptMessage(ecrypt2, "6666666666666666", "6666666666666666", false))
    println(paddingData.contentToString())
    println(paddingData.size)
    val iv: ByteArray? = null
    val rabbit = Rabbit2()
    //            test.test(rabbit)
    rabbit.reset()
    rabbit.setupKey(key)
    if (iv != null) rabbit.setupIV(iv)
    val crypt = rabbit.crypt(paddingData)
    println(crypt.base64(BASE64_DICT))
    rabbit.reset()
    rabbit.setupKey(key)
    if (iv != null) rabbit.setupIV(iv)
    rabbit.crypt(crypt)
    println(String(crypt))
}
