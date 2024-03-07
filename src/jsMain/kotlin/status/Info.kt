package status

import address
import com.bkahlert.kommons.dom.allParameters
import com.bkahlert.kommons.dom.copy
import com.bkahlert.kommons.dom.currentTarget
import com.bkahlert.kommons.dom.favicon
import com.bkahlert.kommons.dom.firstInstanceOrNull
import com.bkahlert.kommons.dom.forEach
import com.bkahlert.kommons.dom.forEachInstance
import com.bkahlert.kommons.dom.parameters
import com.bkahlert.kommons.dom.toggleClass
import com.bkahlert.kommons.dom.url
import com.bkahlert.kommons.serialization.UrlSerializer
import com.bkahlert.kommons.toSimpleString
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.js.Js
import io.ktor.client.request.get
import io.ktor.http.URLBuilder
import io.ktor.http.Url
import io.ktor.http.path
import io.ktor.utils.io.core.use
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.dom.addClass
import kotlinx.dom.clear
import kotlinx.dom.removeClass
import kotlinx.html.dom.append
import kotlinx.html.js.a
import kotlinx.html.js.div
import kotlinx.html.js.li
import kotlinx.html.js.option
import kotlinx.html.title
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLFormElement
import org.w3c.dom.HTMLOptionElement
import org.w3c.dom.HTMLSelectElement
import org.w3c.dom.HTMLUListElement
import org.w3c.dom.events.Event
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Serializable
data class Info(
    val hostname: String?,
    val username: String?,
    val addresses: List<@Serializable(UrlSerializer::class) Url>?,
    val nearby: Map<@Serializable(UrlSerializer::class) Url, String>?,
    val status: Status?,
) {

    companion object {

        private val format = Json {
            isLenient = true
            ignoreUnknownKeys = true
        }

        fun fromJson(json: String): Info = format.decodeFromString(json)

        val Url.infoEndpoint: Url get() = URLBuilder(this).apply { path("info") }.build()

        suspend fun poll(
            url: Url,
            pauseBetweenAttempts: Duration = 5.seconds,
            callback: (Throwable) -> Unit = {},
        ): Info = coroutineScope {
            HttpClient(Js).use { client ->
                val endpoint = url.infoEndpoint
                var info: Info? = null
                while (info == null) {
                    console.log("Requesting $endpoint")
                    try {
                        val response = client.get(endpoint).body<String>()
                        console.log("Response from $endpoint", response)
                        info = fromJson(response)
                    } catch (error: Throwable) {
                        console.error(error.message)
                        callback(error)
                        delay(pauseBetweenAttempts)
                        continue
                    }
                }
                info
            }
        }

        suspend fun isReachable(
            address: Url,
        ): Boolean = coroutineScope {
            kotlin.runCatching {
                withTimeout(5.seconds) {
                    HttpClient(Js).use { client ->
                        val endpoint = address.infoEndpoint
                        console.log("Checking $endpoint")
                        val response = client.get(endpoint).body<String>()
                        console.log("Response from $endpoint", response)
                        fromJson(response)
                    }
                }
            }.fold({ true }, { false })
        }

        suspend fun update(element: HTMLElement, error: Throwable) {
            element.addClass("offline")
            element.removeClass("online")
        }

        private val TOGGLE_NEARBY_CALLBACK: (Event) -> Unit = { event ->
            event.stopPropagation()
            event.preventDefault()

            document.forEachInstance<HTMLFormElement>(".nearby") {
                it.toggleClass("nearby--disabled")
            }
        }

        private val SWITCH_TO_NEARBY_CALLBACK: (Event) -> Unit = { event ->
            event.stopPropagation()
            event.preventDefault()

            event.currentTarget<HTMLFormElement>()
                ?.firstInstanceOrNull<HTMLSelectElement>("select")
                ?.selectedOptions
                ?.firstInstanceOrNull<HTMLOptionElement>()
                ?.value
                ?.let { Url(it) }
                ?.also { url ->
                    val newParams = window.location.parameters.copy { address = url }
                    if (window.location.parameters == newParams) {
                        window.location.reload()
                    } else {
                        window.location.parameters = newParams
                    }
                }
        }
    }

    /**
     * Merges this info and [other] by filling up information missing in this but
     * present in the other instance.
     */
    fun merge(other: Info?): Info = copy(
        hostname = hostname ?: other?.hostname,
        username = username ?: other?.username,
        addresses = addresses ?: other?.addresses,
        nearby = nearby ?: other?.nearby,
        status = status ?: other?.status
    )

    suspend fun update(element: HTMLElement) {
        status?.also {
            document.title = it.name
            document.favicon = it.image
        }

        element.removeClass("init")
        element.removeClass("offline")
        element.addClass("online")

        element.forEachInstance<HTMLElement>(".status") {
            (status ?: Status.DEFAULT_STATUS).update(it)
            it.forEach("figcaption") {
                it.clear()
                it.append {
                    div {
                        a("#") {
                            title = "Switch to different user"
                            +(username ?: "unknown")
                        }
                    }
                }.forEach {
                    it.firstInstanceOrNull<HTMLAnchorElement>("a")
                        ?.addEventListener("click", TOGGLE_NEARBY_CALLBACK)
                }
            }
        }

        element.forEachInstance<HTMLFormElement>("form.nearby") {
            nearby?.update(it)
        }

        element.forEach(".device") {
            it.textContent = hostname ?: ""
        }

        element.forEachInstance<HTMLUListElement>(".addresses") {
            addresses?.update(it)
        }
    }

    private suspend fun Map<Url, String>.update(form: HTMLFormElement) {
        form.forEachInstance<HTMLSelectElement>("select") { update(it) }
        form.addEventListener("submit", SWITCH_TO_NEARBY_CALLBACK)
    }

    private suspend fun Map<Url, String>.update(select: HTMLSelectElement) {
        select.clear()
        select.append {
            option {
                value = window.location.parameters.address?.toString() ?: "error"
                selected = true
                +(username ?: "unknown")
                +(hostname?.let { " — $it" } ?: "")
                +" — current"
            }
        }
        coroutineScope {
            forEach { (address, name) ->
                val option = select.append {
                    option {
                        value = address.toString()
                        +"$name — $address"
                    }
                }.filterIsInstance<HTMLOptionElement>().first()
                if (!isReachable(address)) {
                    option.disabled = true
                    option.textContent += " — unreachable"
                }
            }
        }
    }

    private suspend fun Collection<Url>.update(ul: HTMLUListElement) {
        ul.clear()
        coroutineScope {
            forEach { address ->
                launch {
                    val params = window.location.allParameters.copy { set("address", address.toString()) }
                    val url = URLBuilder(window.location.url).apply {
                        parameters.clear()
                        parameters.appendAll(params)
                    }
                    val li: HTMLElement = ul.append {
                        li("addresses__address addresses__address--checking") {
                            a(url.toString(), classes = "addresses__address-link") {
                                +address.toSimpleString()
                                    .replace('.', '·')
                                    .replace("[", "")
                                    .replace("]", "")
                            }
                        }
                    }.first()

                    if (address == window.location.parameters.address || isReachable(address)) {
                        console.info("Successfully connected to $address")
                        li.removeClass("addresses__address--checking")
                        li.addClass("addresses__address--ok")
                    } else {
                        console.info("Failed to connect to $address")
                        li.removeClass("addresses__address--checking")
                        li.addClass("addresses__address--fail")
                    }
                }
            }
        }
    }
}
