package me.leon.math.factor

import kotlin.system.measureTimeMillis
import kotlin.test.Ignore
import kotlin.test.assertEquals
import me.leon.*
import me.leon.ctf.rsa.*
import me.leon.domain.FactorDbResponse
import me.leon.ext.fromJson
import me.leon.ext.math.getPrimeFromFactorDb
import me.leon.ext.math.root
import me.leon.ext.readFromNet
import org.junit.Test

/**
 * trial divide rho p-1
 *
 * https://github.com/nishanth17/factor/blob/master/factor.py
 * http://helper.ipam.ucla.edu/publications/scws4/scws4_6744.pdf
 */
@Ignore
class FactorTest {

    @Test
    fun factor() {
        // prime
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

        val n =
            "10967139461853415659071654077230663606055071146545582952638294516827112521800750316180738615328664832" +
                "8529071790130095763349089936429151343426469415497138306284842512691195220650548494501337831" +
                "207724397925027651025644976364368511262187227825083865690186591571250267479440100917127211" +
                "65687756617925887051069066502558020047757412363425967003915212560783485540826984882693890" +
                "24074723748926932661198597096587885650141957669927136468326240010678983239136397122846736" +
                "73557898135411273837250194079399384826384835709060797351332323930309379071015140548478132" +
                "32347499396492505539935066160006856860246829574857385680429365994204754303287843631691899" +
                "17166366811166313220829193861784600961153559638823136829943438221286263708504550249055423" +
                "67255179074441679461101331332093019368492643"
        val primes = getPrimeFromFactorDb(n)
        if (primes.groupBy { it }.size == 1) {
            println("${primes.first()} ^ ${primes.size}")
        }

        println(primes)
    }

    @Test
    @Ignore
    fun factorDbNewApi() {
        "http://factordb.com/api?query=11111111111111111111116667777777777777777777777999999999333333117777"
            .readFromNet()
            .fromJson(FactorDbResponse::class.java)
            .also { println(it.allFactorMap) }
    }

    @Test
    fun trialDivide() {
        measureTimeMillis {
                println("123122331123112312313223123".toBigInteger().trialDivide())
                println("65536".toBigInteger().trialDivide())
            }
            .also { println(it) }
        println("118273132683105007025190320003".toBigInteger().trialDivide())
    }

    @Test
    fun fermatFactor() {
        var params = "n1.txt".parseRsaParams()
        requireNotNull(params["n"]).fermat()

        params = "n2.txt".parseRsaParams()
        requireNotNull(params["n"]).fermat()

        params = "n3.txt".parseRsaParams()
        requireNotNull(params["n"]).fermat()

        params = "n4.txt".parseRsaParams()
        requireNotNull(params["n"]).fullFermat().also {
            println("____${it.size}")
            println(it.joinToString("\n"))
        }

        params = "rsa_nec_fermat_4.txt".parseRsaParams()
        val fermatMore = requireNotNull(params["n"]).factor()
        assertEquals(4, fermatMore.size)
    }

    @Test
    fun factorAllInOne() {
        println("118273132683105007025190320003".toBigInteger().factor())
        println("118273132683105007025190320003123".toBigInteger().factor())
        println("118273132683105007025190320123123232546346012312312303123".toBigInteger().factor())
        println(
            "118273132683105007025190320123123232546346012312312301231231231233123"
                .toBigInteger()
                .factor()
        )
    }

    @Test
    fun rho() {
        requireNotNull("n_rho.txt".parseRsaParams()["n"]).pollardsRhoFactors().also {
            println(it)
            assertEquals(4, it.size)
        }

        println(
            "118273132683105007025190320123123232"
                .toBigInteger()
                .also {
                    println("--------factor: " + it.factor())
                    println(it.root(4).contentToString())
                }
                .pollardsRhoFactors()
        )
    }

    @Test
    fun pm1() {
        requireNotNull("n_pm1.txt".parseRsaParams()["n"]).pollardsPM1Factors().also { println(it) }
    }
}
