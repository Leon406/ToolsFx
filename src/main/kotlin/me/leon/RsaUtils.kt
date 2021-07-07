package me.leon

import me.leon.base.base64
import me.leon.base.base64Decode
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
import org.bouncycastle.util.io.pem.PemObject
import org.bouncycastle.util.io.pem.PemWriter
import java.io.*
import java.math.BigInteger
import java.security.*
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.InvalidKeySpecException
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.RSAPublicKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher


object RsaUtils {
    private const val RSA = "RSA/ECB/PKCS1Padding"
    private const val RSA_KEY_FACTORY = "RSA"
    /**
     * 随机生成RSA密钥对
     *
     * @param keyLength 密钥长度，范围：512～2048<br></br>
     * 一般1024
     * @return
     */
    /**
     * 随机生成RSA密钥对(默认密钥长度为1024)
     *
     * @return
     */
    @JvmOverloads
    fun generateRSAKeyPair(keyLength: Int = 1024): KeyPair? {
        return try {
            val kpg = KeyPairGenerator.getInstance(RSA_KEY_FACTORY)
            kpg.initialize(keyLength)
            kpg.genKeyPair()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 用公钥加密 <br></br>
     * 每次加密的字节数，不能超过密钥的长度值减去11
     *
     * @param data      需加密数据的byte数据
     * @param publicKey 公钥
     * @return 加密后的byte型数据
     */
    fun encryptData(data: ByteArray?, publicKey: PublicKey?): ByteArray {
        return try {
            val cipher = Cipher.getInstance(RSA)
            // 编码前设定编码方式及密钥
            cipher.init(Cipher.ENCRYPT_MODE, publicKey)
            // 传入编码数据并返回编码结果
            cipher.doFinal(data)
        } catch (e: Exception) {
            e.printStackTrace()
            byteArrayOf()
        }
    }

    /**
     * 用公钥加密 <br></br>
     * 每次加密的字节数，不能超过密钥的长度值减去11
     *
     * @param data      需加密数据的byte数据
     * @param publicKey 公钥
     * @return 加密后的String型数据
     */
    fun encryptDataStr(data: ByteArray, publicKey: PublicKey): String =
        encryptData(data, publicKey).base64()


    /**
     * 用私钥解密
     *
     * @param encryptedData 经过encryptedData()加密返回的byte数据
     * @param privateKey    私钥
     * @return
     */
    fun decryptData(encryptedData: ByteArray, privateKey: PrivateKey?): ByteArray {
        return try {
            val cipher = Cipher.getInstance(RSA)
            cipher.init(Cipher.DECRYPT_MODE, privateKey)
            cipher.doFinal(encryptedData)
        } catch (e: Exception) {
            byteArrayOf()
        }
    }

    /**
     * 用私钥解密
     *
     * @param encryptedData 经过encryptedData()加密返回的byte数据
     * @param privateKey    私钥
     * @return
     */
    fun decryptDataStr(encryptedData: ByteArray, privateKey: PrivateKey?): String =
        String(decryptData(encryptedData, privateKey))


    /**
     * 通过公钥byte[](publicKey.getEncoded())将公钥还原，适用于RSA算法
     *
     * @param keyBytes
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    fun getPublicKey(keyBytes: ByteArray?): PublicKey {
        val keySpec = X509EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance(RSA_KEY_FACTORY)
        return keyFactory.generatePublic(keySpec)
    }

    /**
     * 通过私钥byte[]将公钥还原，适用于RSA算法
     *
     * @param keyBytes
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    fun getPrivateKey(keyBytes: ByteArray?): PrivateKey {
        val keySpec = PKCS8EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance(RSA_KEY_FACTORY)
        return keyFactory.generatePrivate(keySpec)
    }

    /**
     * 使用N、e值还原公钥
     *
     * @param modulus
     * @param publicExponent
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    fun getPublicKey(modulus: String?, publicExponent: String?): PublicKey {
        val bigIntModulus = BigInteger(modulus)
        val bigIntPrivateExponent = BigInteger(publicExponent)
        val keySpec = RSAPublicKeySpec(bigIntModulus, bigIntPrivateExponent)
        val keyFactory = KeyFactory.getInstance(RSA_KEY_FACTORY)
        return keyFactory.generatePublic(keySpec)
    }

    /**
     * 使用N、d值还原私钥
     *
     * @param modulus
     * @param privateExponent
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    fun getPrivateKey(modulus: String?, privateExponent: String?): PrivateKey {
        val bigIntModulus = BigInteger(modulus)
        val bigIntPrivateExponent = BigInteger(privateExponent)
        val keySpec = RSAPublicKeySpec(bigIntModulus, bigIntPrivateExponent)
        val keyFactory = KeyFactory.getInstance(RSA_KEY_FACTORY)
        return keyFactory.generatePrivate(keySpec)
    }

    /**
     * 从字符串中加载公钥
     *
     * @param publicKeyStr 公钥数据字符串
     * @throws Exception 加载公钥时产生的异常
     */
    fun loadPublicKey(publicKeyStr: String): PublicKey? {
        return try {
            val buffer = publicKeyStr.base64Decode()
            val keyFactory = KeyFactory.getInstance(RSA_KEY_FACTORY)
            val keySpec = X509EncodedKeySpec(buffer)
            keyFactory.generatePublic(keySpec) as RSAPublicKey
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 从字符串中加载私钥<br></br>
     * 加载时使用的是PKCS8EncodedKeySpec（PKCS#8编码的Key指令）。
     *
     * @param privateKeyStr
     * @return
     */
    fun loadPrivateKey(privateKeyStr: String): PrivateKey? {
        return try {
            val buffer = privateKeyStr.base64Decode()
            // X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            val keySpec = PKCS8EncodedKeySpec(buffer)
            val keyFactory = KeyFactory.getInstance(RSA_KEY_FACTORY)
            keyFactory.generatePrivate(keySpec) as RSAPrivateKey
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 从文件中输入流中加载公钥
     *
     * @param in 公钥输入流
     * @throws Exception 加载公钥时产生的异常
     */
    @Throws(Exception::class)
    fun loadPublicKey(`in`: InputStream): PublicKey? {
        return try {
            loadPublicKey(readKey(`in`))
        } catch (e: IOException) {
            throw Exception("公钥数据流读取错误")
        } catch (e: NullPointerException) {
            throw Exception("公钥输入流为空")
        }
    }

    /**
     * 从文件中加载私钥
     *
     * @param in 私钥文件名
     * @return 是否成功
     * @throws Exception
     */
    @Throws(Exception::class)
    fun loadPrivateKey(`in`: InputStream): PrivateKey? {
        return try {
            loadPrivateKey(readKey(`in`))
        } catch (e: IOException) {
            throw Exception("私钥数据读取错误")
        } catch (e: NullPointerException) {
            throw Exception("私钥输入流为空")
        }
    }

    /**
     * 读取密钥信息
     *
     * @param in
     * @return
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun readKey(`in`: InputStream): String {
        val br = BufferedReader(InputStreamReader(`in`))
        var readLine: String? = null
        val sb = StringBuilder()
        while (br.readLine().also { readLine = it } != null) {
            if (readLine!![0] == '-') {
                continue
            } else {
                sb.append(readLine)
                sb.append('\r')
            }
        }
        return sb.toString()
    }

    /**
     * 打印公钥信息
     *
     * @param publicKey
     */
    fun printPublicKeyInfo(publicKey: PublicKey) {
        val rsaPublicKey = publicKey as RSAPublicKey
        println("----------RSAPublicKey----------")
        println("Modulus.length=" + rsaPublicKey.modulus.bitLength())
        println("Modulus=" + rsaPublicKey.modulus.toString())
        println("PublicExponent.length=" + rsaPublicKey.publicExponent.bitLength())
        println("PublicExponent=" + rsaPublicKey.publicExponent.toString())
        println("PublicKey=" + rsaPublicKey.encoded.base64())
        println("PrivatecPemKey=\n" + pkcs1ToPem(rsaPublicKey.encoded, true))
        println(publicPem(rsaPublicKey.encoded.base64()))
    }

    fun printPrivateKeyInfo(privateKey: PrivateKey) {
        val rsaPrivateKey = privateKey as RSAPrivateKey
        println("----------RSAPrivateKey ----------")
        println("Modulus.length=" + rsaPrivateKey.modulus.bitLength())
        println("Modulus=" + rsaPrivateKey.modulus.toString())
        println("PrivateExponent.length=" + rsaPrivateKey.privateExponent.bitLength())
        println("PrivatecExponent=" + rsaPrivateKey.privateExponent.toString())
        println("PrivatecKey=" + rsaPrivateKey.encoded.base64())
        println(privatePem(rsaPrivateKey.encoded.base64()))
        println("PrivatecPemKey=\n" + pkcs1ToPem(rsaPrivateKey.encoded, false))
    }

    @Throws(Exception::class)
    fun pkcs1ToPem(pcks1KeyBytes: ByteArray, isPublic: Boolean): String {
        val type = if (isPublic) {
            "RSA PUBLIC KEY"
        } else {
            "RSA PRIVATE KEY"
        }
        val pemObject = PemObject(type, pcks1KeyBytes)
        val stringWriter = StringWriter()
        val pemWriter = PemWriter(stringWriter)
        pemWriter.writeObject(pemObject)
        pemWriter.close()
        return stringWriter.toString()
    }

    @Throws(Exception::class)
    fun publicPem(publicKey: String?): String? {
        val pubBytes: ByteArray = Base64.getDecoder().decode(publicKey)
        val spkInfo = SubjectPublicKeyInfo.getInstance(pubBytes)
        val primitive = spkInfo.parsePublicKey()
        val publicKeyPKCS1 = primitive.encoded
        val pemObject = PemObject("RSA PUBLIC KEY", publicKeyPKCS1)
        val stringWriter = StringWriter()
        val pemWriter = PemWriter(stringWriter)
        pemWriter.writeObject(pemObject)
        pemWriter.close()
        return stringWriter.toString()
    }

    @Throws(Exception::class)
    fun privatePem(privateKey: String?): String? {
        val privBytes: ByteArray = Base64.getDecoder().decode(privateKey)
        val pkInfo = PrivateKeyInfo.getInstance(privBytes)
        val encodable = pkInfo.parsePrivateKey()
        val primitive = encodable.toASN1Primitive()
        val privateKeyPKCS1 = primitive.encoded
        return pkcs1ToPem(privateKeyPKCS1, false)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        generateRSAKeyPair(1024).also {
            printPrivateKeyInfo(it!!.private)
            printPublicKeyInfo(it.public)
        }
        val priKey =
            "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAL49TYk+THruewfw8c+LdP1gmz44q1/yY6rbKyDuQuAIT8JEqa+stdpCbMrjaduuxvaCXahl9lreNGLk9tbiZd7ACMWiLw8IM0xP6lsOkgHQaLHQy/jHQNwDeT5/Vf36EwVb7pnmSmxtDT8omdJXNv7Y1+3rT/Ztk/gEY5IUhzQ9AgMBAAECgYEAtZAkdBv1Ok5pNYcMAh0DJuAQyi2jwgrAfMx4ORGs2cyU8sA9guC/HvPOiUTKP1Rrp9F8OcA2gzsXnFsSCrxAo9Q+w57Pl1bLFsZEoYOLFsVXuG1En25YLNSVT9WwIcZU11F+dmGJ2z5EWDSbs195p+aORqK7jcR6HTGexyisVTECQQDzOO669YJ7EF3B4LuFJYAKo14R9Oz1cMDWMGfS4kRF8v95J4XjaCEMMEJ05ViC9hQJlh0xMYGZhO68eOoL2hF7AkEAyDvPQFZzwVXCO1kTGhyvzqZ54whvl15YMKomC+GYo74ybEYjJ6MINLYP5tmGRleDKUZQBvDs5XIXyX8gK29XpwJAd9MBkevoB3btqdlsqNDrvtHzQ0d2Agk1h5A7ZiKA3jEz+V0mUf134ohYBT0EGSjggESLRzQLlDVwZDvxmjspxwJASIwrBjOMkzMAQcJ/Qkm8hRIaPWD0FZLwPwmW6V0ekc06tbIf0J+oPHjugAS2OsxAUHcSTcGDA4r4BWCTBkm8JwJAUE/9Lh06Rvq+PJlDdYTgL3tKsuFxUwJF7Le7yS999MPYAz7qe6SmZZqf5t3qVzIZnFjaJOLOvzhwN6AGNDKtuQ=="
        val pubKey =
            "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC+PU2JPkx67nsH8PHPi3T9YJs+OKtf8mOq2ysg7kLgCE/CRKmvrLXaQmzK42nbrsb2gl2oZfZa3jRi5PbW4mXewAjFoi8PCDNMT+pbDpIB0Gix0Mv4x0DcA3k+f1X9+hMFW+6Z5kpsbQ0/KJnSVzb+2Nft60/2bZP4BGOSFIc0PQIDAQAB"
        val privateKey = loadPrivateKey(priKey)
        val publicKey = loadPublicKey(pubKey)
        encryptDataStr("Hello S试试".toByteArray(), publicKey!!).also {
            println("encrypt $it")

            println(decryptDataStr(it.base64Decode(), privateKey))
        }
        val encode =
            "VmFG/7B3Wm1SUBnI+eqcMunaJEIP934cB61jbFN7TvLfGwnVPBWmZGCrTs9PlYbWYoeX+rEnkQ6bOauXKUQ4kNuebRqOkKQe/fM2QXcVa0/p5fL1xzIG1u9at/s0usFohvH7wE6cWTFcr5sT62OKjesnS9qI+Hi07Zqtzgfs9nU="

        val decrypt = decryptDataStr(encode.base64Decode(), privateKey)
        println(decrypt)
//        printPublicKeyInfo(publicKey)
//        printPrivateKeyInfo(privateKey!!)
    }
}