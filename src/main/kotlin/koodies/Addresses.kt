package koodies

import io.ktor.client.HttpClient
import io.ktor.client.call.receive
import io.ktor.client.engine.js.Js
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.URLBuilder
import io.ktor.http.Url
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
data class Addresses(
    val addresses: List<String>,
) {
    companion object {
        private val format = Json { isLenient = true }
        fun fromJson(json: String): Addresses = format.decodeFromString(json)

        suspend fun resolve(url: Url = Url("http://localhost:1880")): Addresses {
            val addressesEndpoint = URLBuilder(url).apply { path("addresses") }.build()

            val client = HttpClient(Js)
            console.info("Resolving backend addresses", addressesEndpoint.toString())
            return client.get<HttpResponse>(addressesEndpoint)
                .receive<String>()
                .let {
                    console.log("Received backend addresses", it)
                    fromJson(it)
                }
        }
    }
}
