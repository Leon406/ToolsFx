package me.leon.ctf

import kotlin.test.Ignore
import kotlin.test.assertEquals
import me.leon.*
import me.leon.ctf.rsa.RsaSolver.solve
import me.leon.ctf.rsa.RsaSolver.solveN2E2C2
import me.leon.ctf.rsa.RsaSolver.solvePQEC
import me.leon.ext.math.*
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
@Ignore
class RsaTest {

    /**
     * 已知 n e phi 三个数，求 d +密文c 求解出明文m
     *
     * n较小
     */
    @Test
    fun rsa1() {
        val params = "rsa01.txt".parseRsaParams()
        assertEquals("picoCTF{sma11_N_n0_g0od_55304594}", solve(params))
    }

    /** 已知 p q e求d 已知 p q ==> n = pq , phiN = (p-1)(q-1) */
    @Test
    fun rsa2() {
        val p = 473_398_607_161.toBigInteger()
        val q = 4_511_491.toBigInteger()
        val e = 17.toBigInteger()
        val phiN = p.phi(q)
        assertEquals("125631357777427553", e.invert(phiN).toString())
    }

    /** n e c , 小 e, e=3 开方爆破 c= m^e mod n => kn+c = m^e ==> 开e次根 m =(kn+c)^(1/e) */
    @Test
    fun rsa_02() {
        val params = "rsa02.txt".parseRsaParams()
        assertEquals("flag{20d6e2da95dcc1fa5f5432a436c4be18}", solve(params))
    }

    /** 已知 p q e c求 m 已知 p q ==> n = pq , phiN = (p-1)(q-1) */
    @Test
    fun rsa3() {
        // yafu 分解n后，可以得到p q
        var params = "rsa03.txt".parseRsaParams()
        assertEquals("flag{01d80670b01b654fe4831a3e81870734}", solvePQEC(params))

        // p q ec
        params = "rsa03_1.txt".parseRsaParams()
        assertEquals("flag{01d80670b01b654fe4831a3e81870734}", solvePQEC(params))

        // e phi不互素
        params = "rsa03_2.txt".parseRsaParams()
        assertEquals("flag{1f803313-8999-4ec3-abc6-907a10fde606}", solvePQEC(params))
    }

    /** 已知e =1 , c , m >n ,求m m^e≡c(mod n) ， 当 e 为1 时， m^e≡m^1≡m≡c(mod n) 由于m是小于n的，题目中给出的密文c就是m */
    @Test
    fun rsa5() {
        val c =
            "56006392793403067781861231386277942050474101531963376999457063633948500765747587998496106575433840765"
        c.toBigInteger().toByteArray().toString(Charsets.UTF_8).also {
            assertEquals("flag{046b9e03-474f-4ac0-9372-25bfc545dc08}", it)
        }
        val params = "rsa05.txt".parseRsaParams()
        assertEquals("flag{20d6e2da95dcc1fa5f5432a436c4be18}", solve(params))
    }

    /** 两组e相同, nc不同,且N不互素 */
    @Test
    fun rsa6() {
        val params = "rsa06.txt".parseRsaParams()
        assertEquals("flag{c9a2c0b2-d078-44a2-b942-3c1030483781}", solve(params))
    }

    /** n (多个数相乘,含合数) e c */
    @Test
    @Ignore
    fun rsa7() {
        // n 由两个素数相乘
        println("_______  n 由两个素数相乘 ________")
        var params = "rsa04_2.txt".parseRsaParams()
        assertEquals("flag{8fb873baba0df4a6423be9f4bd525d93}", solve(params))

        // n 为素数
        println("_______ n 为素数 ________")
        params = "rsa04.txt".parseRsaParams()
        assertEquals("flag{630b1953a612a8392af021393e36538b}", solve(params).toBigInteger().n2s())

        // n 由多个数相乘,含合数
        println("_______ n 由多个数相乘,含合数 ________")
        params = "rsa07.txt".parseRsaParams()
        assertEquals("flag{5c066086-178b-46a7-b0f8-f1afba6f2910}", solve(params))

        // nc不互素
        println("_______ nc不互素 ________")
        params = "rsa11.txt".parseRsaParams()
        assertEquals("flag{ac8eec28-edb0-498f-b50d-94407a3f104f}", solve(params))
    }

    /** 共模攻击 已知两组 n,e,c , 共模 n, e不同 */
    @Test
    fun rsa8() {
        val params = "rsa08.txt".parseRsaParams()
        assertEquals("flag{01d80670b01b654fe4831a3e81870734}", solveN2E2C2(params))
    }

    /**
     * dp泄露 dp = d % (p-1) ==> k(p-1) +dp = d , 又 ed = 1 % phi_n,两边乘以e ==> ke(p-1) + e*dp = 1 %
     * phi_n ==> 如果e*dp <n , p = (e*dp-1)/k +1 尝试爆破 k,求p
     */
    @Test
    fun rsa9() {
        val params = "rsa09.txt".parseRsaParams()
        assertEquals("flag{b098c622-5a99-4aff-a0a7-7b082e7e1c92}", solve(params))
    }

    /**
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
        val params = "rsa10.txt".parseRsaParams()
        assertEquals("flag{96bd68e0-983e-4683-83c5-9cde3d18bea3}", solve(params))
    }

    @Test
    fun rsa12_fermat() {
        var params = "rsa12_fermat.txt".parseRsaParams()
        assertEquals("flag{01d80670b01b654fe4831a3e81870734}", solve(params))

        params = "rsa12_fermat2.txt".parseRsaParams()
        assertEquals("flag{d1fference_between_p_And_q_1s_t00_5mall}", solve(params))
    }

    @Test
    fun rsa_wiener() {
        var params = "rsa14_wiener.txt".parseRsaParams()
        assertEquals("flag{20d6e2da95dcc1fa5f5432a436c4be18}", solve(params))

        params = "rsa14_wiener2.txt".parseRsaParams()
        //        println(wiener(params))
        assertEquals("Tr0y{W1eNer_AttaCk_1s_p0werfu1!}", solve(params))
    }

    @Test
    fun rsa_wiener3() {
        val params = "rsa14_wiener_pqr.txt".parseRsaParams()
        assertEquals("CBCTF{W13ner_4ttack_ca^_d0_m0r3!}", solve(params))
    }

    @Test
    fun rsa_broadcast() {
        var params = "rsa15_broadcast.txt".parseRsaParams()
        assertEquals("flag{59007b62-e7d6-423a-a662-c4706c91a06a}", solve(params))

        params = "rsa15_broadcast_10.txt".parseRsaParams()
        assertEquals("flag{wo0_th3_tr4in_i5_leav1ng_g3t_on_it}", solve(params))

        params = "rsa15_broadcast_octal.txt".parseRsaParams()
        assertEquals("noxCTF{D4mn_y0u_h4s74d_wh47_4_b100dy_b4s74rd!}", solve(params))
    }

    @Test
    fun rsa_pqrec() {
        // 三个因子
        var params = "rsa_19_pqrec.txt".parseRsaParams()
        solve(params).also { assertEquals("flag{9617be6d-80e6-4748-92eb-b1eadfd94509}", it) }

        // 四个因子
        params = "rsa_19_pqrec2.txt".parseRsaParams()
        solve(params).also { assertEquals("flag{077f0bed-c7dc-46e1-800a-bb2dc27a218f}", it) }
        // e phi不互素
        params = "rsa_19_pqrec3_e_phi_not_coprime.txt".parseRsaParams()
        solve(params).also { assertEquals("HECTF{Congratulation!!you_find_flag}", it) }
    }

    @Test
    @Ignore
    fun rsa_n2ec() {
        val params = "rsa_20_n2ec.txt".parseRsaParams()
        solve(params).also { assertEquals("SangFor{qSccmm1WrgvIg2Uq_cZhmqNfEGTz2GV8}", it) }
    }

    @Test
    fun amm() {
        val params = "rsa_amm.txt".parseRsaParams()
        assertEquals("flag{Enj01_m1sc_A0d_cr0}", solve(params))
    }

    /** It takes 10min+, depends on your cpu */
    @Test
    @Ignore
    fun amm2() {
        val params = "rsa_amm2.txt".parseRsaParams()
        println(solve(params))
    }

    @Test
    fun dec() {
        val params = "rsa_dec_p_next_q.txt".parseRsaParams()
        assertEquals("NCTF{70u2_nn47h_14_v3ry_gOO0000000d}", solve(params))
    }

    @Test
    @Ignore
    fun nec3() {
        val params = "nec3.txt".parseRsaParams()
        assertEquals("CMISCCTF{3_RSA}", solve(params))
    }
}
