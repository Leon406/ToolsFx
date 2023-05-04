package me.leon.misc.net

import me.leon.misc.net.Whois.ENTITY_ROLE_ADMINISTRATIVE

/**
 * @author Leon
 * @since 2023-02-23 11:12
 * @email deadogone@gmail.com
 */
data class Rdap(
    val entities: List<Entity>,
    val events: List<Event>,
    val handle: String,
    val lang: String,
    val ldhName: String,
    val links: List<Link>,
    val nameservers: List<Nameserver>,
    val notices: List<Notice>,
    val objectClassName: String,
    val port43: String,
    val rdapConformance: List<String>,
    val secureDNS: SecureDNS,
    val status: List<String>,
    val unicodeName: String
) {

    var errorCode = ""
    var title = ""
    val dnsSecInfo
        get() = "DNSSEC: ${"signed".takeIf { secureDNS.delegationSigned } ?: "unsigned"}"

    val serverInfo
        get() =
            "Registry Server URL: " +
                links.joinToString(" ") { it.href } +
                "\nLast updated from Registry RDAP DB: " +
                events.find { it.eventAction == "last update of RDAP database" }!!.eventDate

    val domainInfo: String
        get() =
            StringBuilder()
                .apply {
                    append("Name: $ldhName").appendLine()
                    append("Registry Domain ID: $handle").appendLine()
                    append("Domain Status: ${status.joinToString(",")}").appendLine()
                    append("Nameservers: \n\t${nameservers.joinToString("\n\t") { it.ldhName }}")
                        .appendLine()

                    append("Dates").appendLine()

                    events
                        .filter { it.eventAction != "last update of RDAP database" }
                        .joinToString("\n") { "\t${it.translate()}: ${it.eventDate}" }
                        .also { append(it) }
                }
                .toString()

    val registrarInfo: String
        get() = info(Whois.ENTITY_ROLE_REGISTRAR)

    val technicalInfo
        get() = info(Whois.ENTITY_ROLE_TECHNICAL)

    val registrantInfo
        get() = info(Whois.ENTITY_ROLE_REGISTRANT)

    val administrativeInfo
        get() = info(ENTITY_ROLE_ADMINISTRATIVE)

    val showInfo
        get() =
            StringBuilder()
                .apply {
                    if (errorCode == "404") {
                        append(title)
                        return@apply
                    }

                    append("Domain Information:\n\n")
                    append(domainInfo)
                    with(registrantInfo) {
                        if (isNotEmpty()) {
                            append("\n\nContact Information:\n\n")
                            append(this)
                        }
                    }

                    with(registrarInfo) {
                        if (isNotEmpty()) {
                            append("\n\nRegistrar Information:\n\n")
                            append(registrarInfo)
                        }
                    }

                    append(dnsSecInfo)
                    append("\n\nAuthoritative Servers:\n\n")
                    append(serverInfo)
                }
                .toString()

    private fun info(role: String): String =
        entities
            .find { it.roles.contains(role) }
            ?.run {
                val sb = StringBuilder()
                publicIds?.let {
                    sb.append(it.joinToString("\n") { "${it.type}:  ${it.identifier}" })
                        .appendLine()
                }
                if (vcardArray.last() is List<*>) {
                    (vcardArray.last() as List<List<*>>).forEach {
                        sb.append(it.info())
                        if (it.info().isNotEmpty()) {
                            sb.appendLine()
                        }
                    }
                }
                sb.toString()
            }
            .orEmpty()
}

private val eventMapping =
    mapOf(
        "expiration" to "Registry Expiration",
        "last changed" to "Updated",
        "registration" to "Created",
    )

private fun Event.translate() = eventMapping[eventAction] ?: eventAction

private fun List<*>.info() =
    when (first()) {
        //            "version" -> last()
        "fn" -> "Name: " + last()
        "org" -> "Organization: " + last()
        "email" -> "email: " + last()
        "tel" -> "phone (${(this[1] as Map<String, String>)["type"]}): " + last()
        "adr" -> "Address: " + (last() as List<String>).dropLast(2).joinToString(" ")
        else ->
            ""
                .also {
                    //                println("info ${first()} ${last()} $this")
                }
    }

data class Entity(
    val entities: List<EntityX>,
    val handle: String,
    val links: List<Link>,
    val objectClassName: String,
    val publicIds: List<PublicId>?,
    val remarks: List<Remark>,
    val roles: List<String>,
    val vcardArray: List<Any>
)

data class Event(val eventAction: String, val eventDate: String)

data class Link(val href: String, val rel: String, val type: String, val value: String) {
    var title: String = ""
}

data class Nameserver(
    val ldhName: String,
    val links: List<Link>,
    val objectClassName: String,
    val unicodeName: String
)

data class Notice(
    val description: List<String>,
    val links: List<Link>,
    val title: String,
    val type: String
)

data class SecureDNS(val delegationSigned: Boolean)

data class EntityX(
    val handle: String,
    val objectClassName: String,
    val roles: List<String>,
    val vcardArray: List<Any>
)

data class PublicId(val identifier: String, val type: String)

data class Remark(val description: List<String>, val title: String, val type: String)
