package me.leon.asymmetric

import java.math.BigInteger
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue
import me.leon.*
import me.leon.ctf.rsa.RsaSolver.fermat
import me.leon.ctf.rsa.RsaSolver.solve
import me.leon.ctf.rsa.RsaSolver.solveN2E2C2
import me.leon.ctf.rsa.RsaSolver.solvePQEC
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
        assertEquals("picoCTF{sma11_N_n0_g0od_55304594}", solve(params))
    }

    /** 已知 p q e求d 已知 p q ==> n = pq , phiN = (p-1)(q-1) */
    @Test
    fun rsa2() {
        val p = 473_398_607_161.toBigInteger()
        val q = 4_511_491.toBigInteger()
        val e = 17.toBigInteger()
        val phiN = p.phi(q)
        println(e.invert(phiN))
        assertEquals("125631357777427553", e.invert(phiN).toString())
    }

    /** n e c , 小 e, 开方爆破 c= m^e mod n => kn+c = m^e ==> 开e次根 m =(kn+c)^(1/e) */
    @Test
    fun rsa_02() {
        val params = "rsa02.txt".parseRsaParams().also { println(it) }
        solve(params).also {
            println(it)
            assertEquals("flag{20d6e2da95dcc1fa5f5432a436c4be18}", it)
        }
    }

    /** 已知 p q e c求 m 已知 p q ==> n = pq , phiN = (p-1)(q-1) */
    @Test
    fun rsa3() {
        // yafu 分解n后，可以得到p q
        var params = "rsa03.txt".parseRsaParams().also { println(it) }
        solvePQEC(params).also {
            println(it)
            assertEquals("flag{01d80670b01b654fe4831a3e81870734}", it)
        }

        // p q ec
        params = "rsa03_1.txt".parseRsaParams().also { println(it) }
        solvePQEC(params).also {
            println(it)
            assertEquals("flag{01d80670b01b654fe4831a3e81870734}", it)
        }

        // e phi不互素
        params = "rsa03_2.txt".parseRsaParams().also { println(it) }
        solvePQEC(params).also {
            println(it)
            assertEquals("flag{1f803313-8999-4ec3-abc6-907a10fde606}", it)
        }
    }

    /** 已知e =1 , c , m >n ,求m m^e≡c(mod n) ， 当 e 为1 时， m^e≡m^1≡m≡c(mod n) 由于m是小于n的，题目中给出的密文c就是m */
    @Test
    fun rsa5() {
        val c =
            "56006392793403067781861231386277942050474101531963376999457063633948500765747587998496106575433840765"
        c.toBigInteger().toByteArray().toString(Charsets.UTF_8).also { println(it) }
        val params = "rsa05.txt".parseRsaParams().also { println(it) }
        println(solve(params))
    }

    /** 两组e相同, nc不同,且N不互素 */
    @Test
    fun rsa6() {
        val params = "rsa06.txt".parseRsaParams().also { println(it) }
        val e = requireNotNull(params["e"])
        val n1 = requireNotNull(params["n1"])
        val c1 = requireNotNull(params["c1"])
        val n2 = requireNotNull(params["n2"])
        val c2 = requireNotNull(params["c2"])

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
        println("_______  n 由两个素数相乘 ________")
        // n 由两个素数相乘
        var params = "rsa04_2.txt".parseRsaParams()

        solve(params).also {
            println(it)
            assertEquals("flag{8fb873baba0df4a6423be9f4bd525d93}", it)
        }

        println("_______ n 为素数 ________")

        // n 为素数
        params = "rsa04.txt".parseRsaParams()
        solve(params).also {
            println(it)
            assertEquals("flag{8fb873baba0df4a6423be9f4bd525d93}", it)
        }

        println("_______ n 由多个数相乘,含合数 ________")
        // n 由多个数相乘,含合数
        params = "rsa07.txt".parseRsaParams()
        solve(params).also { assertEquals("", it) }

        println("_______ nc不互素 ________")
        // nc不互素
        params = "rsa11.txt".parseRsaParams()
        solve(params).also { assertEquals("", it) }
    }

    /** 共模攻击 已知两组 n,e,c , 共模 n, e不同 */
    @Test
    fun rsa8() {
        val params = "rsa08.txt".parseRsaParams().also { println(it) }
        val n1 = requireNotNull(params["n1"])
        val n2 = requireNotNull(params["n2"])
        val e1 = requireNotNull(params["e1"])
        val e2 = requireNotNull(params["e2"])
        val c1 = requireNotNull(params["c1"])
        val c2 = requireNotNull(params["c2"])
        val (_, s1, s2) = e1.gcdExt(e2)
        println((c1.modPow(s1, n1) * c2.modPow(s2, n2) % n1).n2s())

        println(solveN2E2C2(params))
    }

    /**
     * dp泄露 dp = d % (p-1) ==> k(p-1) +dp = d , 又 ed = 1 % phi_n,两边乘以e ==> ke(p-1) + e*dp = 1 %
     * phi_n ==> 如果e*dp <n , p = (e*dp-1)/k +1 尝试爆破 k,求p
     */
    @Test
    fun rsa9() {
        val params = "rsa09.txt".parseRsaParams().also { println(it) }
        val n = requireNotNull(params["n"])
        val e = requireNotNull(params["e"])
        val c = requireNotNull(params["c"])
        val dp = requireNotNull(params["dp"])
        var p = BigInteger.ONE
        var q = BigInteger.ONE
        for (k in 1..65_537) {
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
        val params = "rsa10.txt".parseRsaParams().also { println(it) }
        val p = requireNotNull(params["p"])
        val q = requireNotNull(params["q"])
        val c = requireNotNull(params["c"])
        val dp = requireNotNull(params["dp"])
        val dq = requireNotNull(params["dq"])

        val invQ = q.invert(p)
        val mp = c.modPow(dp, p)
        val mq = c.modPow(dq, q)

        val m = (((mp - mq) * invQ) % p) * q + mq

        println(m.n2s())
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
        measureTimedValue { i.root(30_000) }.also {
            println(it.value.contentToString())
            println("${it.value.first().n2s()} ${it.duration}")
        }
    }

    @Test
    fun fermaFactor() {
        val n =
            ("1123639643894546407917671714319647108788043012479864019452312458488316148374435576188172092479" +
                    "8661332027501424643154414538029585287580122761405974427818841257794157497994556608202723391478" +
                    "0277601817059243175334203054448092234441280346543672103311370689586938405828928194954878260459" +
                    "5657715607415666894223213940210846234934035289857248111540669831812129978798287391650259139688" +
                    "4489682255184448165523604671743400422220149772905676655777228607948091675612455989601008858361" +
                    "7593273704033067606741955063942803870243573225867322980601699624268943607759818771698956329279" +
                    "06390632063530920611197753716095903307467004289983267")
                .toBigInteger()

        println("49".toBigInteger().root().joinToString("\n"))
        fermat(n)
        var params = "n1.txt".parseRsaParams().also { println(it) }

        fermat(requireNotNull(params["n"]))

        params = "n2.txt".parseRsaParams().also { println(it) }
        fermat(requireNotNull(params["n"]))
    }

    @Test
    fun crtTest() {
        // 三三数之剩二，五五数之剩三，七七数之剩二。问物几何？
        val data2 =
            listOf(
                DivideResult("2", "3"),
                DivideResult("3", "5"),
                DivideResult("2", "7"),
            )
        println(crt(data2))
    }
}
