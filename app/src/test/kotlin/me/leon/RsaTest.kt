package me.leon

import org.junit.Test
import java.math.BigInteger

/**
 * p q 两个素数 dp dq 两个指数
 *
 * dp = e^-1 mod (p-1) dq = e^-1 mod (q-1)
 *
 * n = p * q phi = (p - 1) * (q - 1) e = 65537 (一般选择65537) d = e^-1 mod phi
 *
 * c = m^e mod n m = c^d mod n
 *
 * 公钥：(e, n) 私钥：(d, n)
 */
class RsaTest {

    /**
     * 已知 n e phi 三个数，求 d +密文c 求解出明文m
     *
     * n较小
     */
    @Test
    fun rsa1() {
        val params = "rsa01.txt".parseRsaParams().also { println(it) }
        val n = params["n"]!!
        val e = params["e"]!!
        val phi = params["phi"]!!
        val c = params["c"]!!
        c.decrypt(e.d(phi), n).also { println(it) }
    }

    /** 已知 p q e求d 已知 p q ==> n = pq , phiN = (p-1)(q-1) */
    @Test
    fun rsa2() {
        val p = 473398607161.toBigInteger()
        val q = 4511491.toBigInteger()
        val e = 17.toBigInteger()
        val phiN = p.phi(q)
        println(e.d(phiN))
    }

    /** 已知 p q e c求 m 已知 p q ==> n = pq , phiN = (p-1)(q-1) */
    @Test
    fun rsa3() {
        val params = "rsa03.txt".parseRsaParams().also { println(it) }
        val p = params["p"]!!
        val q = params["q"]!!
        val e = params["e"]!!
        val c = params["c"]!!
        val n = params["n"] ?: (p * q)
        val d = e.d(p.phi(q))
        c.decrypt(d, n).also { println(it) }
    }

    @Test
    fun rsa3_1() {
        val params = "rsa03_1.txt".parseRsaParams().also { println(it) }
        if (params["p"] == null) {
            params["n"]?.let {
                it.factorDb().also {
                    if (it.size == 2) {
                        params["p"] = it[0]
                        params["q"] = it[1]
                    }
                }
            }
        }
        val p = params["p"]!!
        val q = params["q"]!!
        val e = params["e"]!!
        val c = params["c"]!!
        val n = params["n"] ?: (p * q)
        val d = e.d(p.phi(q))

        c.decrypt(d, n).also { println(it) }
    }

    @Test
    fun rsa3_2() {
        val params = "rsa03_2.txt".parseRsaParams().also { println(it) }
        if (params["p"] == null) {
            params["n"]?.let {
                it.factorDb().also {
                    if (it.size == 2) {
                        params["p"] = it[0]
                        params["q"] = it[1]
                    }
                }
            }
        }
        val p = params["p"]!!
        val q = params["q"]!!
        val e = params["e"]!!
        val c = params["c"]!!
        val n = params["n"] ?: (p * q)
        val d = e.d(p.phi(q))
        c.decrypt(d, n).also { println(it) }
    }

    /** 已知e =1 , c , m <n ,求m m^e≡c(mod n) ， 当 e 为1 时， m^e≡m^1≡m≡c(mod n) 由于m是小于n的，题目中给出的密文c就是m */
    @Test
    fun rsa4() {
        val c =
            "56006392793403067781861231386277942050474101531963376999457063633948500765747587998496106575433840765"
        c.toBigInteger().toByteArray().toString(Charsets.UTF_8).also { println(it) }
    }

    /** 已知e =1 , c , m >n ,求m m^e≡c(mod n) ， 当 e 为1 时， m^e≡m^1≡m≡c(mod n) 由于m是小于n的，题目中给出的密文c就是m */
    @Test
    fun rsa5() {
        val c =
            "56006392793403067781861231386277942050474101531963376999457063633948500765747587998496106575433840765"
        c.toBigInteger().toByteArray().toString(Charsets.UTF_8).also { println(it) }
    }

    /** 两组e相同, nc不同 */
    @Test
    fun rsa6() {
        val params = "rsa06.txt".parseRsaParams().also { println(it) }
        val e = params["e"]!!
        val n1 = params["n1"]!!
        val c1 = params["c1"]!!
        val n2 = params["n2"]!!
        val c2 = params["c2"]!!

        val p = n1.gcd(n2)
        val q1 = n1 / p
        val q2 = n2 / p
        val d1 = e.d(p.phi(q1))
        val d2 = e.d(p.phi(q2))
        c1.decrypt(d1, n1).also { println(it) }
        c2.decrypt(d2, n2).also { println(it) }
    }

    /** n (多个数相乘,含合数) e c */
    @Test
    fun rsa7() {

        // n 由两个素数相乘
        var params = "rsa04.txt".parseRsaParams().also { println(it) }
        var n = params["n"]!!
        var e = params["e"]!!
        var c = params["c"]!!
        n.factorDb().also {
            println(it)
            if (it.size >= 2) {
                val phi = it.phi()
                val d = e.d(phi).also { println(it) }
                val propN = it.propN(n)
                c.decrypt(d, propN).also { println(it) }
            }
        }
        // n 由多个数相乘,含合数
        params = "rsa07.txt".parseRsaParams().also { println(it) }
        e = params["e"]!!
        n = params["n"]!!
        c = params["c"]!!

        val factorList = n.factorDb()
        val phi = factorList.phi()
        val d = e.d(phi).also { println(it) }
        val propN = factorList.propN(n)
        println(c.decrypt(d, propN))
    }

    @Test
    fun factor() {
        val bigDigit =
            "1346863473634347390771796960343437621220633518755545874225794040661818948117783599221788567624315" +
                    "5145465521141546915941147336786447889325606555333350540003"
        getPrimeFromFactorDb(bigDigit).also { println(it) }
        val bigDigit2 =
            "2482540078515262411777215266989018029858327661762216096122588773716205800604331015383280303052" +
                    "199186976436198142009306796121098855338013353484450237516704784370730555447242806847332" +
                    "980515991676603036451831461614974853586336814921296688024020657977899055504895476451187" +
                    "87266601929429724133167768465309665906113"
        getPrimeFromFactorDb(bigDigit2).also { println(it) }
        getPrimeFromFactorDb("183469842288888698417653802680141427113").also { println(it) }
        val bigDigit3 =
            "1141976890333971618926153237155970525208639827587600850504737512307472709358968061186974826335155" +
                    "4093957968142343831331654606932684767042958427409579115435445187908134556329979271179879129" +
                    "2956674764938867872309485203713507158089884960836947175442983432603698169802283944988567510" +
                    "9619194201154589898424028187450979188069009284053659777167477261729940771077142696476434756" +
                    "600889701275302276327083264777557131716259404433809587040455066545789922339494264087685069" +
                    "28486718265947502369103630279494597681246462305557663234176934418614365600722888121379448849" +
                    "54974348317322412816157152702695143094487806945533233359294549423"
        getPrimeFromFactorDb(bigDigit3).also { println(it) }
    }

    @Test
    fun rsa_nec() {

        val params = "rsa04.txt".parseRsaParams().also { println(it) }
        val n = params["n"]!!
        val e = params["e"]!!
        val c = params["c"]!!
        n.factorDb().also {
            println(it)
            if (it.size >= 2) {
                val phi = it.filter { it > BigInteger.ZERO }.phi()
                val d = e.d(phi).also { println(it) }
                val modN = it.filter { it < BigInteger.ZERO }.fold(n) { acc, bigInteger -> acc / bigInteger.abs() }
                c.decrypt(d, modN).also { println(it) }
            }
        }
    }
}
