package me.leon.toolsfx.plugin.net

import java.io.FileInputStream
import java.security.KeyStore
import java.security.cert.X509Certificate
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

internal class MyX509TrustManager(caPath: String?, caPassword: String) : X509TrustManager {

    init {
        val ks = KeyStore.getInstance("JKS")
        // 获取CA证书
        val caInputStream = FileInputStream(caPath)
        ks.load(caInputStream, caPassword.toCharArray())
        val tmf = TrustManagerFactory.getInstance("SunX509", "SunJSSE")
        tmf.init(ks)
        val tms = tmf.trustManagers
        for (i in tms.indices) {
            if (tms[i] is X509TrustManager) {
                sunJSSEX509TrustManager = tms[i] as X509TrustManager
            }
        }
    }

    private var sunJSSEX509TrustManager: X509TrustManager? = null

    override fun checkClientTrusted(x509Certificates: Array<X509Certificate>, s: String) {
        runCatching { sunJSSEX509TrustManager!!.checkClientTrusted(x509Certificates, s) }
    }

    override fun checkServerTrusted(x509Certificates: Array<X509Certificate>, s: String) {
        runCatching { sunJSSEX509TrustManager!!.checkServerTrusted(x509Certificates, s) }
    }

    override fun getAcceptedIssuers(): Array<X509Certificate> {
        return sunJSSEX509TrustManager!!.acceptedIssuers
    }
}
