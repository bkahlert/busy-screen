package koodies

import io.ktor.client.HttpClient
import io.ktor.client.call.receive
import io.ktor.client.engine.js.Js
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.URLBuilder
import io.ktor.http.Url
import io.ktor.utils.io.core.use
import koodies.dom.allParameters
import koodies.dom.copy
import koodies.dom.url
import koodies.serialization.UrlSerializer
import koodies.time.seconds
import kotlinx.browser.window
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.dom.addClass
import kotlinx.dom.clear
import kotlinx.dom.removeClass
import kotlinx.html.dom.append
import kotlinx.html.js.a
import kotlinx.html.js.li
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.w3c.dom.HTMLElement
import org.w3c.dom.asList

@Serializable
data class Info(
    val hostname: String,
    val username: String,
    val addresses: List<@Serializable(UrlSerializer::class) Url>,
) {
    companion object {
        private val format = Json { isLenient = true }
        fun fromJson(json: String): Info = format.decodeFromString(json)
        val Url.infoEndpoint: Url get() = URLBuilder(this).apply { path("info") }.build()

        suspend fun resolve(url: Url = Url("http://localhost:1880")): Info =
            HttpClient(Js).use { client ->
                val infoEndpoint = url.infoEndpoint
                console.info("Getting info using $infoEndpoint")
                client.get<HttpResponse>(infoEndpoint)
                    .receive<String>()
                    .let {
                        console.log("Received info", it)
                        fromJson(it)
                    }
            }
    }

    suspend fun update(element: HTMLElement): HTMLElement {
        element.querySelectorAll(".device").asList().forEach {
            it.textContent = "$username@$hostname"
        }

        element.querySelectorAll(".addresses").asList().forEach { ul ->
            ul.clear()
            addresses.forEach { address ->
                console.info("Checking $address (${address.infoEndpoint})")
                val params = window.location.allParameters.copy { set("location", address.toString()) }
                val url = window.location.url.copy(parameters = params)
                val li: HTMLElement = ul.append {
                    li("addresses__address addresses__address--checking") {
                        a(url.toString(), classes = "addresses__address-link") { +address.toSimpleString() }
                    }
                }.first()
                coroutineScope {
                    @Suppress("DeferredResultUnused")
                    async {
                        kotlin.runCatching {
                            withTimeoutOrNull(5.seconds) {
                                kotlin.runCatching { resolve(address) }.fold(
                                    {
                                        console.info("Successfully connected to $address (${address.infoEndpoint})")
                                        li.removeClass("addresses__address--checking")
                                        li.addClass("addresses__address--ok")
                                    }, {
                                        console.error("Failed to connected to $address (${address.infoEndpoint})", it.message)
                                        li.removeClass("addresses__address--checking")
                                        li.addClass("addresses__address--fail")
                                    })
                            }
                        }.onFailure {
                            console.error("Timeout while connecting to $address (${address.infoEndpoint})", it.message)
                            li.removeClass("addresses__address--checking")
                            li.addClass("addresses__address--fail")
                        }
                    }
                }
            }
        }

        return element
    }
}
