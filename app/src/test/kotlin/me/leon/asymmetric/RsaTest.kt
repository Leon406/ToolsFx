package me.leon.asymmetric

import java.math.BigInteger
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue
import me.leon.*
import me.leon.asymmetric.RsaSolver.solveNEC
import me.leon.asymmetric.RsaSolver.solvePQEC
import org.junit.Test

/**
 * p q 两个素数 dp dq 两个指数
 *
 * dp = e^-1 mod (p-1) dq = e^-1 mod (q-1)
 *
 * n = p * q phi = (p - 1) * (q - 1) e = 65537 (一般选择65537) ed = 1 mod phi d = e^-1 mod phi
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
        c.decrypt(e.invert(phi).also { println(it) }, n).also {
            println(it)
            assertEquals("picoCTF{sma11_N_n0_g0od_55304594}", it)
        }
    }

    /** 已知 p q e求d 已知 p q ==> n = pq , phiN = (p-1)(q-1) */
    @Test
    fun rsa2() {
        val p = 473398607161.toBigInteger()
        val q = 4511491.toBigInteger()
        val e = 17.toBigInteger()
        val phiN = p.phi(q)
        println(e.invert(phiN))
    }
    /** n e c , 小 e, 开方爆破 c= m^e mod n => kn+c = m^e ==> 开e次根 m =(kn+c)^(1/e) */
    @Test
    fun rsa_02() {
        val params = "rsa02.txt".parseRsaParams().also { println(it) }
        val e = params["e"]!!
        val n = params["n"]!!
        val c = params["c"]!!

        solveNEC(n, e, c).also { println(it) }
    }

    /** 已知 p q e c求 m 已知 p q ==> n = pq , phiN = (p-1)(q-1) */
    @Test
    fun rsa3() {
        // yafu 分解n后，可以得到p q
        var params = "rsa03.txt".parseRsaParams().also { println(it) }
        var p = params["p"]!!
        var q = params["q"]!!
        var e = params["e"]!!
        var c = params["c"]!!

        solvePQEC(p, q, e, c).also { println(it) }

        // p q ec
        params = "rsa03_1.txt".parseRsaParams().also { println(it) }
        p = params["p"]!!
        q = params["q"]!!
        e = params["e"]!!
        c = params["c"]!!

        solvePQEC(p, q, e, c).also { println(it) }

        // e phi不互素
        params = "rsa03_2.txt".parseRsaParams().also { println(it) }
        p = params["p"]!!
        q = params["q"]!!
        e = params["e"]!!
        c = params["c"]!!
        solvePQEC(p, q, e, c).also { println(it) }
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

    /** 两组e相同, nc不同,且N不互素 */
    @Test
    fun rsa6() {
        var params = "rsa06.txt".parseRsaParams().also { println(it) }
        var e = params["e"]!!
        var n1 = params["n1"]!!
        var c1 = params["c1"]!!
        var n2 = params["n2"]!!
        var c2 = params["c2"]!!

        val p = n1.gcd(n2)
        val q1 = n1 / p
        val q2 = n2 / p
        val d1 = e.invert(p.phi(q1))
        val d2 = e.invert(p.phi(q2))
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

        solveNEC(n, e, c)

        // n 由多个数相乘,含合数
        params = "rsa07.txt".parseRsaParams().also { println(it) }
        e = params["e"]!!
        n = params["n"]!!
        c = params["c"]!!

        solveNEC(n, e, c)

        // nc不互素
        params = "rsa11.txt".parseRsaParams().also { println(it) }
        e = params["e"]!!
        n = params["n"]!!
        c = params["c"]!!

        solveNEC(n, e, c)
    }

    /** 共模攻击 已知两组 n,e,c , 共模 n, e不同 */
    @Test
    fun rsa8() {
        var params = "rsa08.txt".parseRsaParams().also { println(it) }
        var n1 = params["n1"]!!
        var n2 = params["n2"]!!
        var e1 = params["e1"]!!
        var e2 = params["e2"]!!
        var c1 = params["c1"]!!
        var c2 = params["c2"]!!
        val (_, s1, s2) = e1.gcdExt(e2)
        println((c1.modPow(s1, n1) * c2.modPow(s2, n2) % n1).n2s())
    }

    /**
     * dp泄露 dp = d % (p-1) ==> k(p-1) +dp = d , 又 ed = 1 % phi_n,两边乘以e ==> ke(p-1) + e*dp = 1 %
     * phi_n ==> 如果e*dp <n , p = (e*dp-1)/k +1 尝试爆破 k,求p
     */
    @Test
    fun rsa9() {
        var params = "rsa09.txt".parseRsaParams().also { println(it) }
        var n = params["n"]!!
        var e = params["e"]!!
        var c = params["c"]!!
        var dp = params["dp"]!!
        var p: BigInteger = BigInteger.ONE
        var q: BigInteger = BigInteger.ONE
        for (k in 1..65537) {
            p = (e * dp - BigInteger.ONE) / k.toBigInteger() + BigInteger.ONE
            if (n.gcd(p) != BigInteger.ONE) {
                q = n / p
                break
            }
        }
        println(c.decrypt(e.invert(p.phi(q)), n))
    }

    /**
     *
     * dp dq
     *
     * p q c dp ,dq dp = d % (p-1) dq = d % (q-1)
     *
     * invQ = q mod p 逆元 mq = c^dp % q mp = c^dq % p
     *
     * m = ((mp - mq) * invQ %p) *q + mq
     */
    @Test
    fun rsa10() {
        var params = "rsa10.txt".parseRsaParams().also { println(it) }
        var p = params["p"]!!
        var q = params["q"]!!
        var c = params["c"]!!
        var dp = params["dp"]!!
        var dq = params["dq"]!!

        val invQ = q.invert(p)
        val mp = c.modPow(dp, p)
        val mq = c.modPow(dq, q)

        val m = (((mp - mq) * invQ) % p) * q + mq

        println(m.n2s())
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

    @OptIn(ExperimentalTime::class)
    @Test
    fun rootN() {
        val i =
            ("2217344750798294937344050117513831761010547351781457575945714176628679412650463329" +
                    "4234669550268044399317656271118568881021332348369140068180238399943422830231427029931" +
                    "826653444453257342990474092233543389488631718467806742449257243340911537016978649186" +
                    "95050507247415283070309")
                .toBigInteger()
        measureTimedValue { i.root(30000) }.also {
            println(it.value.contentToString())
            println("${it.value.first().n2s()} ${it.duration}")
        }
    }
}
