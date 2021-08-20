import com.bkahlert.kommons.dom.allParameters
import com.bkahlert.kommons.dom.copy
import com.bkahlert.kommons.dom.hashParameters
import com.bkahlert.kommons.dom.parameters
import com.bkahlert.kommons.parse
import io.ktor.http.Parameters
import io.ktor.http.ParametersBuilder
import io.ktor.http.Url
import kotlinx.browser.window
import kotlin.time.Duration

val Parameters.address: Url?
    get() = get("address")?.let { Url(it) }

var ParametersBuilder.address: Url?
    get() = get("address")?.let { Url(it) }
    set(value) {
        if (value != null) set("address", value.toString())
        else remove("address")
    }

var ParametersBuilder.refreshRate: Duration?
    get() = get("refresh-rate")?.let { Duration.parse(it) }
    set(value) {
        if (value != null) set("refresh-rate", value.toIsoString())
        else remove("refresh-rate")
    }

fun initParameters(
    defaultAddress: Url,
    defaultRefreshRate: Duration,
): Pair<Url, Duration> {

    window.location.parameters = window.location.allParameters.copy {
        address = address ?: defaultAddress
        refreshRate = refreshRate ?: defaultRefreshRate
    }
    window.location.hashParameters = Parameters.Empty

    val url = Url(window.location.allParameters["address"] ?: error("address missing"))
    val refreshRate = Duration.parse(window.location.allParameters["refresh-rate"] ?: error("refresh-rate missing"))
    return Pair(url, refreshRate)
}
