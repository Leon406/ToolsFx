// Copyright (c) 2006 Damien Miller <djm@mindrot.org>
//
// Permission to use, copy, modify, and distribute this software for any
// purpose with or without fee is hereby granted, provided that the above
// copyright notice and this permission notice appear in all copies.
//
// THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
// WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
// ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
// WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
// ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
// OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
package me.leon.hash.bcrypt

import java.security.MessageDigest
import java.security.SecureRandom
import kotlin.experimental.and

/**
 * BCrypt implements OpenBSD-style Blowfish password hashing using the scheme described in "A
 * Future-Adaptable Password Scheme" by Niels Provos and David Mazieres.
 *
 * This password hashing system tries to thwart off-line password cracking using a
 * computationally-intensive hashing algorithm, based on Bruce Schneier's Blowfish cipher. The work
 * factor of the algorithm is parameterised, so it can be increased as computers get faster.
 *
 * Usage is really simple. To hash a password for the first time, call the hashpw method with a
 * random salt, like this:
 *
 * ` String pw_hash = BCrypt.hashpw(plain_password, BCrypt.gensalt()); <br></br> ` *
 *
 * To check whether a plaintext password matches one that has been hashed previously, use the
 * checkpw method:
 *
 * ` if (BCrypt.checkpw(candidate_password, stored_hash))<br></br>
 * &nbsp;&nbsp;&nbsp;&nbsp;System.out.println("It matches");<br></br> else<br></br>
 * &nbsp;&nbsp;&nbsp;&nbsp;System.out.println("It does not match");<br></br> ` *
 *
 * The gensalt() method takes an optional parameter (logRounds) that determines the computational
 * complexity of the hashing:
 *
 * ` String strong_salt = BCrypt.gensalt(10)<br></br> String stronger_salt =
 * BCrypt.gensalt(12)<br></br> ` *
 *
 * The amount of work increases exponentially (2**logRounds), so each increment is twice as much
 * work. The default logRounds is 10, and the valid range is 4 to 31.
 *
 * @author Damien Miller
 * @version 0.3
 */
class BCrypt {
    // Expanded Blowfish key
    private lateinit var p: IntArray
    private lateinit var s: IntArray

    /**
     * Blowfish encipher a single 64-bit block encoded as two 32-bit halves
     *
     * @param lr an array containing the two 32-bit half blocks
     * @param off the position in the array of the blocks
     */
    private fun encipher(lr: IntArray, off: Int) {
        var n: Int
        var l = lr[off]
        var r = lr[off + 1]
        l = l xor p[0]
        var i = 0
        while (i <= BLOWFISH_NUM_ROUNDS - 2) {

            // Feistel substitution on left word
            n = s[l shr 24 and 0xff]
            n += s[0x100 or (l shr 16 and 0xff)]
            n = n xor s[0x200 or (l shr 8 and 0xff)]
            n += s[0x300 or (l and 0xff)]
            r = r xor (n xor p[++i])

            // Feistel substitution on right word
            n = s[r shr 24 and 0xff]
            n += s[0x100 or (r shr 16 and 0xff)]
            n = n xor s[0x200 or (r shr 8 and 0xff)]
            n += s[0x300 or (r and 0xff)]
            l = l xor (n xor p[++i])
        }
        lr[off] = r xor p[BLOWFISH_NUM_ROUNDS + 1]
        lr[off + 1] = l
    }

    /** Initialise the Blowfish key schedule */
    private fun initKey() {
        p = P_orig.clone()
        s = S_orig.clone()
    }

    /**
     * Key the Blowfish cipher
     *
     * @param key an array containing the key
     * @param signExtBug true to implement the 2x bug
     * @param safety bit 16 is set when the safety measure is requested
     */
    private fun key(key: ByteArray, signExtBug: Boolean) {
        val koffp = intArrayOf(0)
        val lr = intArrayOf(0, 0)
        val plen = p.size
        val slen = s.size
        var i = 0
        while (i < plen) {
            if (!signExtBug) {
                p[i] = p[i] xor streamtoword(key, koffp)
            } else {
                p[i] = p[i] xor streamtowordBug(key, koffp)
            }
            i++
        }
        i = 0
        while (i < plen) {
            encipher(lr, 0)
            p[i] = lr[0]
            p[i + 1] = lr[1]
            i += 2
        }
        i = 0
        while (i < slen) {
            encipher(lr, 0)
            s[i] = lr[0]
            s[i + 1] = lr[1]
            i += 2
        }
    }

    /**
     * Perform the "enhanced key schedule" step described by Provos and Mazieres in "A
     * Future-Adaptable Password Scheme" https://www.openbsd.org/papers/bcrypt-paper.ps
     *
     * @param data salt information
     * @param key password information
     * @param signExtBug true to implement the 2x bug
     * @param safety bit 16 is set when the safety measure is requested
     */
    private fun ekskey(data: ByteArray, key: ByteArray, signExtBug: Boolean, safety: Int) {
        val koffp = intArrayOf(0)
        val doffp = intArrayOf(0)
        val lr = intArrayOf(0, 0)
        val plen = p.size
        val slen = s.size
        val signp = intArrayOf(0) // non-benign sign-extension flag
        var diff = 0 // zero iff correct and buggy are same
        var i = 0
        while (i < plen) {
            val words = streamtowords(key, koffp, signp)
            diff = diff or (words[0] xor words[1])
            p[i] = p[i] xor words[if (signExtBug) 1 else 0]
            i++
        }
        var sign = signp[0]

        /*
         * At this point, "diff" is zero iff the correct and buggy algorithms produced
         * exactly the same result. If so and if "sign" is non-zero, which indicates that
         * there was a non-benign sign extension, this means that we have a collision
         * between the correctly computed hash for this password and a set of passwords
         * that could be supplied to the buggy algorithm. Our safety measure is meant to
         * protect from such many-buggy to one-correct collisions, by deviating from the
         * correct algorithm in such cases. Let's check for this.
         */ diff = diff or (diff shr 16) /* still zero iff exact match */
        diff = diff and 0xffff /* ditto */
        diff += 0xffff /* bit 16 set iff "diff" was non-zero (on non-match) */
        sign = sign shl 9 /* move the non-benign sign extension flag to bit 16 */
        sign = sign and (diff.inv() and safety) /* action needed? */

        /*
         * If we have determined that we need to deviate from the correct algorithm, flip
         * bit 16 in initial expanded key. (The choice of 16 is arbitrary, but let's stick
         * to it now. It came out of the approach we used above, and it's not any worse
         * than any other choice we could make.)
         *
         * It is crucial that we don't do the same to the expanded key used in the main
         * Eksblowfish loop. By doing it to only one of these two, we deviate from a state
         * that could be directly specified by a password to the buggy algorithm (and to
         * the fully correct one as well, but that's a side-effect).
         */ p[0] = p[0] xor sign
        i = 0
        while (i < plen) {
            lr[0] = lr[0] xor streamtoword(data, doffp)
            lr[1] = lr[1] xor streamtoword(data, doffp)
            encipher(lr, 0)
            p[i] = lr[0]
            p[i + 1] = lr[1]
            i += 2
        }
        i = 0
        while (i < slen) {
            lr[0] = lr[0] xor streamtoword(data, doffp)
            lr[1] = lr[1] xor streamtoword(data, doffp)
            encipher(lr, 0)
            s[i] = lr[0]
            s[i + 1] = lr[1]
            i += 2
        }
    }

    /**
     * Perform the central password hashing step in the bcrypt scheme
     *
     * @param password the password to hash
     * @param salt the binary salt to hash with the password
     * @param logRounds the binary logarithm of the number of rounds of hashing to apply
     * @param signExtBug true to implement the 2x bug
     * @param safety bit 16 is set when the safety measure is requested
     * @return an array containing the binary hashed password
     */
    private fun cryptRaw(
        password: ByteArray,
        salt: ByteArray,
        logRounds: Int,
        signExtBug: Boolean,
        safety: Int
    ): ByteArray {
        var j: Int
        val cdata = bf_crypt_ciphertext.clone()
        val clen = cdata.size
        require(!(logRounds < 4 || logRounds > 31)) { "Bad number of rounds" }
        val rounds = 1 shl logRounds
        require(salt.size == BCRYPT_SALT_LEN) { "Bad salt length" }
        initKey()
        ekskey(salt, password, signExtBug, safety)
        var i = 0
        while (i < rounds) {
            key(password, signExtBug)
            key(salt, false)
            i++
        }
        i = 0
        while (i < 64) {
            j = 0
            while (j < clen shr 1) {
                encipher(cdata, j shl 1)
                j++
            }
            i++
        }
        val ret = ByteArray(clen * 4)
        i = 0
        j = 0
        while (i < clen) {
            ret[j++] = (cdata[i] shr 24 and 0xff).toByte()
            ret[j++] = (cdata[i] shr 16 and 0xff).toByte()
            ret[j++] = (cdata[i] shr 8 and 0xff).toByte()
            ret[j++] = (cdata[i] and 0xff).toByte()
            i++
        }
        return ret
    }

    companion object {
        // BCrypt parameters
        private const val GENSALT_DEFAULT_LOG2_ROUNDS = 10
        private const val BCRYPT_SALT_LEN = 16

        // Blowfish parameters
        private const val BLOWFISH_NUM_ROUNDS = 16

        // Initial contents of key schedule
        private val P_orig =
            intArrayOf(
                0x243f6a88,
                -0x7a5cf72d,
                0x13198a2e,
                0x03707344,
                -0x5bf6c7de,
                0x299f31d0,
                0x082efa98,
                -0x13b19377,
                0x452821e6,
                0x38d01377,
                -0x41ab9931,
                0x34e90c6c,
                -0x3f53d649,
                -0x3683af23,
                0x3f84d5b5,
                -0x4ab8f6e9,
                -0x6de92a27,
                -0x768604e5
            )
        private val S_orig =
            intArrayOf(
                -0x2ecef45a,
                -0x67204a54,
                0x2ffd72db,
                -0x2fe52049,
                -0x471e5013,
                0x6a267e96,
                -0x45836fbb,
                -0xed38067,
                0x24a19947,
                -0x4c6e9309,
                0x0801f2e2,
                -0x7a7103ea,
                0x636920d8,
                0x71574e69,
                -0x5ba7015d,
                -0xb6cc282,
                0x0d95748f,
                0x728eb658,
                0x718bcd58,
                -0x7deab512,
                0x7b54a41d,
                -0x3da5a64b,
                -0x63cf2ac7,
                0x2af26013,
                -0x3a2e4fdd,
                0x286085f0,
                -0x35be86e8,
                -0x4724c711,
                -0x71862350,
                0x603a180e,
                0x6c9e0e8b,
                -0x4fe175c2,
                -0x28ea883f,
                -0x42ceb4d9,
                0x78af2fda,
                0x55605c60,
                -0x19aada0d,
                -0x55aa546c,
                0x57489862,
                0x63e81440,
                0x55ca396a,
                0x2aab10b6,
                -0x4b33a3cc,
                0x1141e8ce,
                -0x5eab7951,
                0x7c72e993,
                -0x4c11ebef,
                0x636fbc2a,
                0x2ba9c55d,
                0x741831f6,
                -0x31a3c1ea,
                -0x64786ce2,
                -0x502945cd,
                0x6c24cf5c,
                0x7a325381,
                0x28958677,
                0x3b8f4898,
                0x6b4bb9af,
                -0x3b4017e5,
                0x66282193,
                0x61d809cc,
                -0x4de566f,
                0x487cac60,
                0x5dec8032,
                -0x107ba2a3,
                -0x167a8a4f,
                -0x23d9dcfe,
                -0x149ae478,
                0x23893e81,
                -0x2c69533b,
                0x0f6d6ff3,
                -0x7c0bbdc7,
                0x2e0b4482,
                -0x5b7bdffc,
                0x69c8f04a,
                -0x61e064a2,
                0x21c66842,
                -0x9169366,
                0x670c9c61,
                -0x542c7710,
                0x6a51a0d2,
                -0x27abd098,
                -0x69f058d8,
                -0x54aecc5d,
                0x6eef0b6c,
                0x137a3be4,
                -0x45c40fb0,
                0x7efb2a98,
                -0x5e0e9ae3,
                0x39af0176,
                0x66ca593e,
                -0x7dbcf178,
                -0x731179e7,
                0x456f9fb4,
                0x7d84a5c3,
                0x3b8b5ebe,
                -0x1f908a28,
                -0x7a3edf8d,
                0x401a449f,
                0x56c16aa6,
                0x4ed3aa62,
                0x363f7706,
                0x1bfedf72,
                0x429b023d,
                0x37d0d724,
                -0x2ff5edb8,
                -0x24f0152d,
                0x49f1c09b,
                0x075372c9,
                -0x7f66e485,
                0x25d479d8,
                -0x9172109,
                -0x1c01afe6,
                -0x4986b3c5,
                -0x68931f43,
                0x04c006ba,
                -0x3e56b04a,
                0x409f60c4,
                0x5e5c9ec2,
                0x196a2463,
                0x68fb6faf,
                0x3e6c53b5,
                0x1339b2eb,
                0x3b52ec6f,
                0x6dfc511f,
                -0x64cf6ad4,
                -0x337ebabc,
                -0x50a142f7,
                -0x411c2ffc,
                -0x21ccb503,
                0x660f2807,
                0x192e4bb3,
                -0x3f3457a9,
                0x45c8740f,
                -0x2df4a0c7,
                -0x462c0425,
                0x5579c0bd,
                0x1a60320a,
                -0x295eff3a,
                0x402c7279,
                0x679f25fe,
                -0x4e05c34,
                -0x715a1608,
                -0x24cddd08,
                0x3c7516df,
                -0x29e94eb,
                0x2f501ec8,
                -0x52faad55,
                0x323db5fa,
                -0x2dc78a0,
                0x53317b48,
                0x3e00df82,
                -0x61a3a845,
                -0x35907360,
                0x1a87562e,
                -0x20e89625,
                -0x2abd570a,
                0x287effc3,
                -0x5398cd3a,
                -0x73b0aa8d,
                0x695b27b0,
                -0x4435a738,
                -0x1e005ca3,
                -0x470fee60,
                0x10fa3d98,
                -0x2de7c48,
                0x4afcb56c,
                0x2dd1d35b,
                -0x65ac1b87,
                -0x4907ba9b,
                -0x2d71b644,
                0x4bfb9790,
                -0x1e220d26,
                -0x5b3481cd,
                0x62fb1341,
                -0x311b3918,
                -0x10df3526,
                0x36774c01,
                -0x2f816102,
                0x2bf11fb4,
                -0x6a2425b3,
                -0x516f6e68,
                -0x1552718f,
                0x6b93d5a0,
                -0x2f712e30,
                -0x5038da20,
                -0x71c3a4d1,
                -0x718a6b49,
                -0x70091d05,
                -0xdedd49c,
                -0x777747ee,
                -0x6ff20fe4,
                0x4fad5ea0,
                0x688fc31c,
                -0x2e300e6f,
                -0x4c573e53,
                0x2f2f2218,
                -0x41f1e889,
                -0x158ad202,
                -0x74fde05f,
                -0x1a5f33f1,
                -0x4a908b18,
                0x18acf3d6,
                -0x31761d67,
                -0x4b57b020,
                -0x2ec1f49,
                0x7cc43b81,
                -0x2d525727,
                0x165fa266,
                -0x7f6a88fb,
                -0x6c338cec,
                0x211a1477,
                -0x1952df9b,
                0x77b5fa86,
                -0x38abbd0b,
                -0x462ca31,
                -0x143250f4,
                0x7b3e89a0,
                -0x29bee42d,
                -0x51e181b7,
                0x00250e2d,
                0x2071b35e,
                0x226800bb,
                0x57b8e0af,
                0x2464369b,
                -0xff646e2,
                0x5563911d,
                0x59dfa6aa,
                0x78c14389,
                -0x26a5ac81,
                0x207d5ba2,
                0x02e5b9c5,
                -0x7cd9fc8a,
                0x6295cfa9,
                0x11c81968,
                0x4e734a41,
                -0x4cb8d236,
                0x7b14a94a,
                0x1b510052,
                -0x65acd6eb,
                -0x29f0a8c1,
                -0x4364391c,
                0x2b60a476,
                -0x7e198c00,
                0x08ba6fb5,
                0x571be91f,
                -0xd691395,
                0x2a0dd915,
                -0x499c9adf,
                -0x1846064a,
                -0xcbfad2,
                -0x3a7aa99c,
                0x53b02d5d,
                -0x5660705f,
                0x08ba4799,
                0x6e85076a,
                0x4b7a70e9,
                -0x4a4cd6bc,
                -0x248af6d2,
                -0x3be6d9dd,
                -0x52915950,
                0x49a7df7d,
                -0x63119f48,
                -0x70124d9a,
                -0x1355738f,
                0x699a17ff,
                0x5664526c,
                -0x3d4e611f,
                0x193602a5,
                0x75094c29,
                -0x5fa6ecc0,
                -0x1be7c5c2,
                0x3f54989a,
                0x5b429d65,
                0x6b8fe4d6,
                -0x6608c02a,
                -0x5e2d63f9,
                -0x1017cf0b,
                0x4d2d38e6,
                -0xfdaa23f,
                0x4cdd2086,
                -0x7b8f14da,
                0x6382e9c6,
                0x021ecc5e,
                0x09686b3f,
                0x3ebaefc9,
                0x3c971814,
                0x6b6a70a1,
                0x687f3584,
                0x52a0e286,
                -0x4863acfb,
                -0x55aff8c9,
                0x3e07841c,
                0x7fdeae5c,
                -0x7182bb14,
                0x5716f2b8,
                -0x4fc525c9,
                -0xfaff3f3,
                -0xfe3e0fc,
                0x0200b3ff,
                -0x51f30ae6,
                0x3cb574b2,
                0x25837a58,
                -0x23f6de43,
                -0x2e6eec07,
                0x7ca92ff6,
                -0x6bcdb88d,
                0x22f54701,
                0x3ae5e581,
                0x37c2dadc,
                -0x374a89cc,
                -0x650c2259,
                -0x56bb9eba,
                0x0fd0030e,
                -0x133738c2,
                -0x5b8ae1bf,
                -0x1dc73267,
                0x3bea0e2f,
                0x3280bba1,
                0x183eb331,
                0x4e548b38,
                0x4f6db908,
                0x6f420d03,
                -0x9f5fb41,
                0x2cb81290,
                0x24977c79,
                0x5679b072,
                -0x43507651,
                -0x216588e1,
                -0x266cf7f0,
                -0x4c7451ee,
                -0x2330c0d2,
                0x5512721f,
                0x2e6b7124,
                0x501adde6,
                -0x607b3279,
                0x7a584718,
                0x7408da17,
                -0x43606544,
                -0x16b48274,
                -0x138513c6,
                -0x247ae206,
                0x63094366,
                -0x3b9b3c2e,
                -0x10e3e7b9,
                0x3215d908,
                -0x22bcc4c9,
                0x24c2ba16,
                0x12a14d43,
                0x2a65c451,
                0x50940002,
                0x133ae4dd,
                0x71dff89e,
                0x10314e55,
                -0x7e53882a,
                0x5f11199b,
                0x043556f1,
                -0x285c3895,
                0x3c11183b,
                0x5924a509,
                -0xd701913,
                -0x680e0406,
                -0x614540d4,
                0x1e153c6e,
                -0x791cba90,
                -0x1516904f,
                -0x79f1a1f6,
                0x5a3e2ab3,
                0x771fe71c,
                0x4e3d06fa,
                0x2965dcb9,
                -0x6618e2f1,
                -0x7fc1762a,
                0x5266c825,
                0x2e4cc978,
                -0x63ef4c96,
                -0x39eaf146,
                -0x6b1d1588,
                -0x5a03c3ad,
                0x1e0a2df4,
                -0xd08b159,
                0x361d2b3d,
                0x1939260f,
                0x19c27960,
                0x5223a708,
                -0x8eced4a,
                -0x14520192,
                -0x153ce09a,
                -0x1c43ba6b,
                -0x5984377d,
                -0x4e80c82f,
                0x018cff28,
                -0x3ccd2211,
                -0x4193a55b,
                0x65582185,
                0x68ab9802,
                -0x11315af1,
                -0x24d06ac5,
                0x2aef7dad,
                0x5b6e2f84,
                0x1521b628,
                0x29076170,
                -0x1322b88b,
                0x619f1510,
                0x13cca830,
                -0x149e426a,
                0x0334fe1e,
                -0x55fc9c31,
                -0x4a8ca370,
                0x4c70a239,
                -0x2a6161f5,
                -0x345521ec,
                -0x11337944,
                0x60622ca7,
                -0x6354a355,
                -0x4d0c7b92,
                0x648b1eaf,
                0x19bdf0ca,
                -0x5fdc9647,
                0x655abb50,
                0x40685a32,
                0x3c2ab4b3,
                0x319ee9d5,
                -0x3fde4709,
                -0x64abf4e7,
                -0x78a05f67,
                -0x6a086682,
                0x623d7da8,
                -0x7c87766,
                -0x681cd289,
                0x11ed935f,
                0x16681281,
                0x0e358829,
                -0x3819e02a,
                -0x6921205f,
                0x7858ba99,
                0x57f584a5,
                0x1b227263,
                -0x647c3c01,
                0x1ac24696,
                -0x324cf515,
                0x532e3054,
                -0x7026b71c,
                0x6dbc3128,
                0x58ebf2ef,
                0x34c6ffea,
                -0x1d7129f,
                -0x1183c38d,
                0x5d4a14d9,
                -0x179b481d,
                0x42105d14,
                0x203e13e0,
                0x45eee2b6,
                -0x5c555416,
                -0x2493b0eb,
                -0x534b030,
                -0x38bd0bbe,
                -0x1095444b,
                0x654f3b1d,
                0x41cd2105,
                -0x27e18662,
                -0x797ab239,
                -0x1bb4b896,
                0x3d816250,
                -0x309d5e0e,
                0x5b8d2646,
                -0x3777c60,
                -0x3e38495d,
                0x7f1524c3,
                0x69cb7492,
                0x47848a0b,
                0x5692b285,
                0x095bbf00,
                -0x52e6b763,
                0x1462b174,
                0x23820e00,
                0x58428d2a,
                0x0c55f5ea,
                0x1dadf43e,
                0x233f7061,
                0x3372f092,
                -0x726c81bf,
                -0x29a0130f,
                0x6c223bdb,
                0x7cde3759,
                -0x34118ba0,
                0x4085f2a7,
                -0x3188cd92,
                -0x59f87f7c,
                0x19f8509e,
                -0x171027ab,
                0x61d99735,
                -0x56965856,
                -0x3af3f93e,
                0x5a04abfc,
                -0x7ff43524,
                -0x61bb85d2,
                -0x3cbacb7c,
                -0x22a98fb,
                0x0e1e9ec9,
                -0x248c242d,
                0x105588cd,
                0x675fda79,
                -0x1c98bcc0,
                -0x3a3bcb9b,
                0x713e38d8,
                0x3d28f89e,
                -0xe9200e0,
                0x153e21e7,
                -0x704fc2b6,
                -0x191c60d5,
                -0x247c5209,
                -0x16c2a598,
                -0x6b7ebf09,
                -0x9b3d9e4,
                -0x6b96d6cc,
                0x411520f7,
                0x7602d4f7,
                -0x430b94d2,
                -0x2b5dff98,
                -0x2bf7db8f,
                0x3320f46a,
                0x43b7d4b7,
                0x500061af,
                0x1e39f62e,
                -0x68dbbaba,
                0x14214f74,
                -0x407477c0,
                0x4d95fc1d,
                -0x694a6e51,
                0x70f4ddd3,
                0x66a02f45,
                -0x4043f614,
                0x03bd9785,
                0x7fac6dd0,
                0x31cb8504,
                -0x6914d84d,
                0x55fd3941,
                -0x25dab81a,
                -0x5435f566,
                0x28507825,
                0x530429f4,
                0x0a2c86da,
                -0x16499205,
                0x68dc1462,
                -0x28b79700,
                0x680ec0a4,
                0x27a18dee,
                0x4f3ffea2,
                -0x17785274,
                -0x4a731ffa,
                0x7af4d6b6,
                -0x5531e184,
                -0x2cc8a014,
                -0x31875c67,
                0x406b2a42,
                0x20fe9e35,
                -0x260c7a47,
                -0x11c62855,
                0x3b124e8b,
                0x1dc9faf7,
                0x4b6d1856,
                0x26a36631,
                -0x151c684e,
                0x3a6efa74,
                -0x22a4bcce,
                0x6841e7f7,
                -0x3587df05,
                -0x4f50ab2,
                -0x27014c69,
                0x454056ac,
                -0x45b76ad9,
                0x55533a3a,
                0x20838d87,
                -0x1945649,
                -0x2f696ab5,
                0x55a867bc,
                -0x5eea65a8,
                -0x3356d69d,
                -0x661e24cd,
                -0x59d5b5aa,
                0x3f3125f9,
                0x5ef47e1c,
                -0x6fd6ce84,
                -0x20717fe,
                0x04272f70,
                -0x7f44eaa4,
                0x05282ce3,
                -0x6a3eeab8,
                -0x1b3992de,
                0x48c1133f,
                -0x38f07924,
                0x07f9c9ee,
                0x41041f0f,
                0x404779a4,
                0x5d886e17,
                0x325f51eb,
                -0x2a643f2f,
                -0xd433e71,
                0x41113564,
                0x257b7834,
                0x602a9c60,
                -0x2007175d,
                0x1f636c1b,
                0x0e12b4c2,
                0x02e1329e,
                -0x5099b02f,
                -0x352e7eeb,
                0x6b2395e0,
                0x333e92e1,
                0x3b240b62,
                -0x114146de,
                -0x7a4d5df2,
                -0x1945f267,
                -0x218df374,
                0x2da2f728,
                -0x2fed87bb,
                -0x6a486b03,
                0x647d0862,
                -0x18330a10,
                0x5449a36f,
                -0x7882b706,
                -0x3c6202d9,
                -0xcc172e2,
                0x0a476341,
                -0x66d1008c,
                0x3a6f6eab,
                -0xb0702c9,
                -0x57ed23a0,
                -0x5e142208,
                -0x66e41eb4,
                -0x249194f3,
                -0x3984aaf0,
                0x6d672c37,
                0x2765d43b,
                -0x232f17fc,
                -0xed6f239,
                -0x33ff005d,
                -0x4ac6f06e,
                0x690fed0b,
                0x667b9ffb,
                -0x31248264,
                -0x5f6e30f5,
                -0x26eaa15d,
                -0x44ecd078,
                0x515bad24,
                0x7b9479bf,
                0x763bd6eb,
                0x37392eb3,
                -0x33eea687,
                -0x7fd91d69,
                -0xbd1ced3,
                0x6842ada7,
                -0x3995d4c5,
                0x12754ccc,
                0x782ef11c,
                0x6a124237,
                -0x486dae19,
                0x06a1bbe6,
                0x4bfb6350,
                0x1a6b1018,
                0x11caedfa,
                0x3d25bdd8,
                -0x1d1e3c37,
                0x44421659,
                0x0a121386,
                -0x26f31392,
                -0x2a5415d6,
                0x64af674e,
                -0x257957a1,
                -0x41401678,
                0x64e4c3fe,
                -0x62437fa9,
                -0xf083f7a,
                0x60787bf8,
                0x6003604d,
                -0x2e027cba,
                -0x9c7e050,
                0x7745ae04,
                -0x28c90334,
                -0x7cbd94cd,
                -0xfe1548f,
                -0x4f7fbe79,
                0x3c005e5f,
                0x77a057be,
                -0x421751dc,
                0x55464299,
                -0x40a7d19f,
                0x4e58f48f,
                -0xd22025e,
                -0xb8b10c8,
                -0x7876423e,
                0x5366f9c3,
                -0x374c718c,
                -0x4b8a0dab,
                0x46fcd9b9,
                0x7aeb2661,
                -0x74e2207c,
                -0x7b95f187,
                -0x6ea06a1e,
                0x466e598e,
                0x20b45770,
                -0x732aaa6f,
                -0x36fd21b4,
                -0x46f4531f,
                -0x447dfa30,
                0x11a86248,
                0x7574a99e,
                -0x4880e64a,
                -0x1f5623f7,
                0x662d09a1,
                -0x3bcdb9cd,
                -0x17a5e0fe,
                0x09f0be8c,
                0x4a99a025,
                0x1d6efe10,
                0x1ab93d1d,
                0x0ba5a4df,
                -0x5e790df1,
                0x2868f169,
                -0x2348257d,
                0x573906fe,
                -0x5e1d3165,
                0x4fcd7f52,
                0x50115e01,
                -0x58f97c06,
                -0x5ffd4a3c,
                0x0de6d027,
                -0x650773d9,
                0x773f8641,
                -0x3c9fb3fa,
                0x61a806b5,
                -0xfe885d8,
                -0x3f0a7920,
                0x006058aa,
                0x30dc7d62,
                0x11e69ed7,
                0x2338ea63,
                0x53c2dd94,
                -0x3d3de9cc,
                -0x443411aa,
                -0x6f434922,
                -0x1403825f,
                -0x31a6e28a,
                0x6f05e409,
                0x4b7c0188,
                0x39720a3d,
                0x7c927c24,
                -0x791c8da1,
                0x724d9db9,
                0x1ac15bb4,
                -0x2c614704,
                -0x12abaa88,
                0x08fca5b5,
                -0x27c2832d,
                0x4dad0fc4,
                0x1e50ef5e,
                -0x4e9e1908,
                -0x5d7aeb27,
                0x6c51133c,
                0x6fd5c7e7,
                0x56e14ec4,
                0x362abfce,
                -0x223937c9,
                -0x2865cdcc,
                -0x6d9c7dee,
                0x670efa8e,
                0x406000e0,
                0x3a39ce37,
                -0x2c050a31,
                -0x543d88c9,
                0x5ac52d1b,
                0x5cb0679e,
                0x4fa33742,
                -0x2c7dd8c0,
                -0x66436442,
                -0x2aee7163,
                -0x40f08ceb,
                -0x29d2e382,
                -0x38ff3b85,
                -0x4873e495,
                0x21a19045,
                -0x4d914e42,
                0x6a366eb4,
                0x5748ab2f,
                -0x436b9187,
                -0x395c892e,
                0x6549c2c8,
                0x530ff8ee,
                0x468dde7d,
                -0x2a8cf5e3,
                0x4cd04dc6,
                0x2939bbdb,
                -0x5645b9b0,
                -0x536ad918,
                -0x41a11cfc,
                -0x5e052a10,
                0x6a2d519a,
                0x63ef8ce2,
                -0x657911de,
                -0x3f763d48,
                0x43242ef6,
                -0x5ae1fc56,
                -0x630d2f5c,
                -0x7c3f9e46,
                -0x641695b3,
                -0x701aeab0,
                -0x459ba42a,
                0x2826a2f9,
                -0x58c5c51f,
                0x4ba99586,
                -0x10aa9d17,
                -0x38d0102d,
                -0x8ad0826,
                0x3f046f69,
                0x77fa0a59,
                -0x7f1b56eb,
                -0x784f79ff,
                -0x64f61953,
                0x3b3ee593,
                -0x166f02a6,
                -0x61cb2869,
                0x2cf0b7d9,
                0x022b8b51,
                -0x692a53c6,
                0x017da67d,
                -0x2e30c12a,
                0x7c7d2d28,
                0x1f9f25cf,
                -0x520d4765,
                0x5ad6b472,
                0x5a88f54c,
                -0x1fd6538f,
                -0x1fe65a1a,
                0x47b0acfd,
                -0x126c0565,
                -0x172c3b73,
                0x283b57cc,
                -0x72a99d7,
                0x79132e28,
                0x785f0191,
                -0x128a9fab,
                -0x869f1bc,
                -0x1c2ca174,
                0x15056dd4,
                -0x770b9246,
                0x03a16125,
                0x0564f0bd,
                -0x3c1461eb,
                0x3c9057a2,
                -0x68d8e514,
                -0x56c5f8d6,
                0x1b3f6d9b,
                0x1e6321f5,
                -0xa639905,
                0x26dcf319,
                0x7533d928,
                -0x4eaa020b,
                0x03563482,
                -0x7545c345,
                0x28517711,
                -0x3df52608,
                -0x5433ae99,
                -0x33526da1,
                0x4de81751,
                0x3830dc8e,
                0x379d5862,
                -0x6cdf066f,
                -0x15856f3e,
                -0x4c18432,
                0x5121ce64,
                0x774fbe32,
                -0x57491c82,
                -0x3cd6c2ba,
                0x48de5369,
                0x6413e680,
                -0x5d51f7f0,
                -0x22924ddc,
                0x69852dfd,
                0x09072166,
                -0x4c65b9f6,
                0x6445c0dd,
                0x586cdecf,
                0x1c20c8ae,
                0x5bbef7dd,
                0x1b588d40,
                -0x332dfe81,
                0x6bb4e3bb,
                -0x225d9582,
                0x3a59ff45,
                0x3e350a44,
                -0x434b322b,
                0x72eacea8,
                -0x59b7b45,
                -0x7299ed52,
                -0x40c390b9,
                -0x2d641b9d,
                0x542f5d9e,
                -0x513d88e5,
                -0x9b19c90,
                0x740e0d8d,
                -0x18a4eca9,
                -0x78de98f,
                -0x50ac82a3,
                0x4040cb08,
                0x4eb4e2cc,
                0x34d2466a,
                0x0115af84,
                -0x1e4ffbd8,
                -0x6a67c5e3,
                0x06b89fb4,
                -0x31915fb8,
                0x6f3f3b82,
                0x3520ab82,
                0x011a1d4b,
                0x277227f8,
                0x611560b1,
                -0x186cc024,
                -0x44c586d5,
                0x344525bd,
                -0x5f77c61f,
                0x51ce794b,
                0x2f32c9b7,
                -0x5fe04537,
                -0x1fe33782,
                -0x43382e0a,
                -0x30feee3d,
                -0x5e175539,
                0x1a908749,
                -0x2bb04266,
                -0x2f252135,
                -0x2af525c8,
                0x0339c32a,
                -0x396ec999,
                -0x7206ce84,
                -0x1f4ed4b1,
                -0x861a649,
                0x43f5bb3a,
                -0xd2ae601,
                0x27d9459c,
                -0x4068ddd4,
                0x15e6fc2a,
                0x0f91fc71,
                -0x646beadb,
                -0x51a6c9f,
                -0x31496315,
                -0x3d579ba7,
                0x12baa8d1,
                -0x493ef8a2,
                -0x1cfa95f4,
                0x10d25065,
                -0x34fc5bbe,
                -0x1f1391f2,
                0x1698db3b,
                0x4c98a0be,
                0x3278e964,
                -0x60e06ace,
                -0x1f2c6d21,
                -0x2c5fcbd5,
                -0x768e0de2,
                0x1b0a7441,
                0x4ba3348c,
                -0x3a418ee0,
                -0x3c89cd28,
                -0x20ca6073,
                -0x6466d0d2,
                -0x19f490b9,
                0x0fe3f11d,
                -0x1ab325ac,
                0x1edad891,
                -0x319d8631,
                -0x32c18191,
                0x1618b166,
                -0x2d3e2fb,
                -0x7b702d3b,
                -0x904dd67,
                -0xadc0ca9,
                -0x59cd89dd,
                -0x6c57cacf,
                0x56cccd02,
                -0x530f7e9e,
                0x5a75ebb5,
                0x6e163697,
                -0x772d8c34,
                -0x21699d6e,
                -0x7e46b630,
                0x4c50901b,
                0x71c65614,
                -0x19393843,
                0x327a140a,
                0x45e1d006,
                -0x3c0d8466,
                -0x3655ac03,
                0x62a80f00,
                -0x44da401e,
                0x35bdd2f6,
                0x71126905,
                -0x4dfbfdde,
                -0x49343084,
                -0x328963d5,
                0x53113ec0,
                0x1640e3d3,
                0x38abbd60,
                0x2547adf0,
                -0x45c7df64,
                -0x8b9318a,
                0x77afa1c5,
                0x20756060,
                -0x7a3401b2,
                -0x75177228,
                0x7aaaf9b0,
                0x4cf9aa7e,
                0x1948c25c,
                0x02fb8a8c,
                0x01c36ae4,
                -0x29141e07,
                -0x6f2b0797,
                -0x59a32160,
                0x3f09252d,
                -0x3df71961,
                -0x48b19ece,
                -0x31881da5,
                0x578fdfe3,
                0x3ac372e6
            )

        // bcrypt IV: "OrpheanBeholderScryDoubt"
        private val bf_crypt_ciphertext =
            intArrayOf(0x4f727068, 0x65616e42, 0x65686f6c, 0x64657253, 0x63727944, 0x6f756274)

        // Table for Base64 encoding
        private val base64_code =
            charArrayOf(
                '.',
                '/',
                'A',
                'B',
                'C',
                'D',
                'E',
                'F',
                'G',
                'H',
                'I',
                'J',
                'K',
                'L',
                'M',
                'N',
                'O',
                'P',
                'Q',
                'R',
                'S',
                'T',
                'U',
                'V',
                'W',
                'X',
                'Y',
                'Z',
                'a',
                'b',
                'c',
                'd',
                'e',
                'f',
                'g',
                'h',
                'i',
                'j',
                'k',
                'l',
                'm',
                'n',
                'o',
                'p',
                'q',
                'r',
                's',
                't',
                'u',
                'v',
                'w',
                'x',
                'y',
                'z',
                '0',
                '1',
                '2',
                '3',
                '4',
                '5',
                '6',
                '7',
                '8',
                '9'
            )

        // Table for Base64 decoding
        private val index_64 =
            byteArrayOf(
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                0,
                1,
                54,
                55,
                56,
                57,
                58,
                59,
                60,
                61,
                62,
                63,
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                2,
                3,
                4,
                5,
                6,
                7,
                8,
                9,
                10,
                11,
                12,
                13,
                14,
                15,
                16,
                17,
                18,
                19,
                20,
                21,
                22,
                23,
                24,
                25,
                26,
                27,
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                28,
                29,
                30,
                31,
                32,
                33,
                34,
                35,
                36,
                37,
                38,
                39,
                40,
                41,
                42,
                43,
                44,
                45,
                46,
                47,
                48,
                49,
                50,
                51,
                52,
                53,
                -1,
                -1,
                -1,
                -1,
                -1
            )
        const val MIN_LOG_ROUNDS = 4
        const val MAX_LOG_ROUNDS = 31

        /**
         * Encode a byte array using bcrypt's slightly-modified base64 encoding scheme. Note that
         * this is **not** compatible with the standard MIME-base64 encoding.
         *
         * @param d the byte array to encode
         * @param len the number of bytes to encode
         * @param rs the destination buffer for the base64-encoded string
         * @exception IllegalArgumentException if the length is invalid
         */
        @Throws(IllegalArgumentException::class)
        fun encodeBase64(d: ByteArray, len: Int, rs: StringBuilder) {
            var off = 0
            var c1: Int
            var c2: Int
            require(!(len <= 0 || len > d.size)) { "Invalid len" }
            while (off < len) {
                c1 = d[off++].toInt() and 0xff
                rs.append(base64_code[c1 shr 2 and 0x3f])
                c1 = c1 and 0x03 shl 4
                if (off >= len) {
                    rs.append(base64_code[c1 and 0x3f])
                    break
                }
                c2 = d[off++].toInt() and 0xff
                c1 = c1 or (c2 shr 4 and 0x0f)
                rs.append(base64_code[c1 and 0x3f])
                c1 = c2 and 0x0f shl 2
                if (off >= len) {
                    rs.append(base64_code[c1 and 0x3f])
                    break
                }
                c2 = d[off++].toInt() and 0xff
                c1 = c1 or (c2 shr 6 and 0x03)
                rs.append(base64_code[c1 and 0x3f])
                rs.append(base64_code[c2 and 0x3f])
            }
        }

        /**
         * Look up the 3 bits base64-encoded by the specified character, range-checking againt
         * conversion table
         *
         * @param x the base64-encoded value
         * @return the decoded value of x
         */
        private fun char64(x: Char): Byte {
            return if (x.code < 0 || x.code >= index_64.size) {
                -1
            } else {
                index_64[x.code]
            }
        }

        /**
         * Decode a string encoded using bcrypt's base64 scheme to a byte array. Note that this is
         * *not* compatible with the standard MIME-base64 encoding.
         *
         * @param s the string to decode
         * @param maxLen the maximum number of bytes to decode
         * @return an array containing the decoded bytes
         * @throws IllegalArgumentException if maxLen is invalid
         */
        @Throws(IllegalArgumentException::class)
        fun decodeBase64(s: String, maxLen: Int): ByteArray {
            val rs = StringBuilder()
            var off = 0
            val sLen = s.length
            var olen = 0
            var c1: Byte
            var c2: Byte
            var c3: Byte
            var c4: Byte
            var o: Byte
            require(maxLen > 0) { "Invalid maxolen" }
            while (off < sLen - 1 && olen < maxLen) {
                c1 = char64(s[off++])
                c2 = char64(s[off++])
                if (c1.toInt() == -1 || c2.toInt() == -1) {
                    break
                }
                o = (c1.toInt() shl 2).toByte()
                o = (o.toInt() or ((c2 and 0x30).toInt() shr 4)).toByte()
                rs.append(Char(o.toUShort()))
                if (++olen >= maxLen || off >= sLen) {
                    break
                }
                c3 = char64(s[off++])
                if (c3.toInt() == -1) {
                    break
                }
                o = ((c2 and 0x0f).toInt() shl 4).toByte()
                o = (o.toInt() or ((c3 and 0x3c).toInt() shr 2)).toByte()
                rs.append(Char(o.toUShort()))
                if (++olen >= maxLen || off >= sLen) {
                    break
                }
                c4 = char64(s[off++])
                o = ((c3 and 0x03).toInt() shl 6).toByte()
                o = (o.toInt() or c4.toInt()).toByte()
                rs.append(Char(o.toUShort()))
                ++olen
            }
            val ret = ByteArray(olen)
            off = 0
            while (off < olen) {
                ret[off] = rs[off].code.toByte()
                off++
            }
            return ret
        }

        /**
         * Cynically extract a word of key material
         *
         * @param data the string to extract the data from
         * @param offp a "pointer" (as a one-entry array) to the current offset into data
         * @param signp a "pointer" (as a one-entry array) to the cumulative flag for non-benign
         *   sign extension
         * @return correct and buggy next word of material from data as int[2]
         */
        private fun streamtowords(data: ByteArray, offp: IntArray, signp: IntArray): IntArray {
            val words = intArrayOf(0, 0)
            var off = offp[0]
            var sign = signp[0]
            var i = 0
            while (i < 4) {
                words[0] = words[0] shl 8 or (data[off].toInt() and 0xff)
                words[1] = words[1] shl 8 or data[off].toInt() // sign extension bug
                if (i > 0) {
                    sign = sign or (words[1] and 0x80)
                }
                off = (off + 1) % data.size
                i++
            }
            offp[0] = off
            signp[0] = sign
            return words
        }

        /**
         * Cycically extract a word of key material
         *
         * @param data the string to extract the data from
         * @param offp a "pointer" (as a one-entry array) to the current offset into data
         * @return the next word of material from data
         */
        private fun streamtoword(data: ByteArray, offp: IntArray): Int {
            val signp = intArrayOf(0)
            return streamtowords(data, offp, signp)[0]
        }

        /**
         * Cycically extract a word of key material, with sign-extension bug
         *
         * @param data the string to extract the data from
         * @param offp a "pointer" (as a one-entry array) to the current offset into data
         * @return the next word of material from data
         */
        private fun streamtowordBug(data: ByteArray, offp: IntArray): Int {
            return streamtowords(data, offp, intArrayOf(0))[1]
        }

        fun roundsForLogRounds(logRounds: Int): Long {
            require(!(logRounds < 4 || logRounds > 31)) { "Bad number of rounds" }
            return 1L shl logRounds
        }

        /**
         * Hash a password using the OpenBSD bcrypt scheme
         *
         * @param password the password to hash
         * @param salt the salt to hash with (perhaps generated using BCrypt.gensalt)
         * @return the hashed password
         */
        @JvmStatic
        fun hashpw(password: String, salt: String?): String {
            return hashpw(password.toByteArray(), salt)
        }

        /**
         * Hash a password using the OpenBSD bcrypt scheme
         *
         * @param passwordb the password to hash, as a byte array
         * @param salt the salt to hash with (perhaps generated using BCrypt.gensalt)
         * @return the hashed password
         */
        @JvmStatic
        fun hashpw(passwordb: ByteArray, salt: String?): String {
            var tmpPass = passwordb
            val hashed: ByteArray
            var minor = 0.toChar()
            val rounds: Int
            val off: Int
            val rs = StringBuilder()
            requireNotNull(salt) { "salt cannot be null" }
            val saltLength = salt.length
            require(saltLength >= 28) { "Invalid salt" }
            require(!(salt[0] != '$' || salt[1] != '2')) { "Invalid salt version" }
            if (salt[2] == '$') {
                off = 3
            } else {
                minor = salt[2]
                require(
                    !(minor != 'a' && minor != 'x' && minor != 'y' && minor != 'b' ||
                        salt[3] != '$')
                ) {
                    "Invalid salt revision"
                }
                off = 4
            }

            // Extract number of rounds
            require(salt[off + 2] <= '$') { "Missing salt rounds" }
            require(!(off == 4 && saltLength < 29)) { "Invalid salt" }
            rounds = salt.substring(off, off + 2).toInt()
            val realSalt = salt.substring(off + 3, off + 25)
            val saltB = decodeBase64(realSalt, BCRYPT_SALT_LEN)
            if (minor >= 'a') {
                tmpPass = tmpPass.copyOf(tmpPass.size + 1)
            }
            val b = BCrypt()
            hashed =
                b.cryptRaw(tmpPass, saltB, rounds, minor == 'x', if (minor == 'a') 0x10000 else 0)
            rs.append("$2")
            if (minor >= 'a') {
                rs.append(minor)
            }
            rs.append("$")
            if (rounds < 10) {
                rs.append("0")
            }
            rs.append(rounds)
            rs.append("$")
            encodeBase64(saltB, saltB.size, rs)
            encodeBase64(hashed, bf_crypt_ciphertext.size * 4 - 1, rs)
            return rs.toString()
        }

        @JvmOverloads
        @Throws(IllegalArgumentException::class)
        @JvmStatic
        fun genSalt(
            prefix: String = "$2a",
            logRounds: Int = GENSALT_DEFAULT_LOG2_ROUNDS,
            random: SecureRandom = SecureRandom()
        ): String {
            val rnd = ByteArray(BCRYPT_SALT_LEN)
            random.nextBytes(rnd)
            return genSalt(rnd, prefix, logRounds)
        }

        @JvmOverloads
        @Throws(IllegalArgumentException::class)
        @JvmStatic
        fun genSalt(
            salt: ByteArray,
            prefix: String = "$2a",
            logRounds: Int = GENSALT_DEFAULT_LOG2_ROUNDS,
        ): String {
            val rs = StringBuilder()
            require(
                !(!prefix.startsWith("$2") ||
                    prefix[2] != 'a' && prefix[2] != 'y' && prefix[2] != 'b')
            ) {
                "Invalid prefix"
            }
            require(!(logRounds < 4 || logRounds > 31)) { "Invalid logRounds" }
            rs.append("$2")
            rs.append(prefix[2])
            rs.append("$")
            if (logRounds < 10) {
                rs.append("0")
            }
            rs.append(logRounds)
            rs.append("$")
            encodeBase64(salt, salt.size, rs)
            return rs.toString()
        }

        /**
         * Check that a plaintext password matches a previously hashed one
         *
         * @param plaintext the plaintext password to verify
         * @param hashed the previously-hashed password
         * @return true if the passwords match, false otherwise
         */
        @JvmStatic
        fun checkPw(plaintext: String, hashed: String): Boolean {
            return equalsNoEarlyReturn(hashed, hashpw(plaintext, hashed))
        }

        /**
         * Check that a password (as a byte array) matches a previously hashed one
         *
         * @param password the password to verify, as a byte array
         * @param hashed the previously-hashed password
         * @return true if the passwords match, false otherwise
         * @since 5.3
         */
        @JvmStatic
        fun checkPw(password: ByteArray, hashed: String): Boolean {
            return equalsNoEarlyReturn(hashed, hashpw(password, hashed))
        }

        private fun equalsNoEarlyReturn(a: String, b: String): Boolean {
            return MessageDigest.isEqual(a.toByteArray(), b.toByteArray())
        }
    }
}
