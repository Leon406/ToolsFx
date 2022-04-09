package me.leon.toolsfx.plugin.net

import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateFactory
import javax.net.ssl.*
import me.leon.ext.toFile

object TrustManager {
    fun parseFromCertification(path: String) {
        val certificate =
            CertificateFactory.getInstance("X.509", "BC")
                .generateCertificate(path.toFile().inputStream())
        println(certificate)
        val ks =
            KeyStore.getInstance(KeyStore.getDefaultType()).apply {
                load(null)
                setCertificateEntry("cert", certificate)
            }
        val trustManagerFactory =
            TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(ks)

        val sslContext =
            SSLContext.getInstance("TLS").apply {
                init(null, trustManagerFactory.trustManagers, SecureRandom())
            }
        SSLContext.setDefault(sslContext)
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.socketFactory)
        HttpsURLConnection.setDefaultHostnameVerifier { _, _ -> true }
    }

    fun parseFromPkcs12(path: String, pass: String) {
        val ks =
            KeyStore.getInstance("PKCS12").apply {
                load(path.toFile().inputStream(), pass.toCharArray())
            }

        val kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
        kmf.init(ks, pass.toCharArray())

        val kms = kmf.keyManagers
        val sslContext =
            SSLContext.getInstance("TLS").apply {
                init(kms, arrayOf(MyX509TrustManager(path, pass)), SecureRandom())
            }
        SSLContext.setDefault(sslContext)

        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.socketFactory)
        HttpsURLConnection.setDefaultHostnameVerifier { _, _ -> true }
    }
}
