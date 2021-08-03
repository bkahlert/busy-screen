package koodies

import koodies.time.minutes
import koodies.time.seconds
import kotlin.time.Duration

private val regex = Regex("P.*T(?:(?<minutes>\\d+)M)?(?:(?<seconds>\\d+)S)?")

fun Duration.Companion.parse(text: String): Duration = when {
    text.startsWith("P") -> {
        requireNotNull(regex.matchEntire(text)
            ?.run {
                var duration = ZERO
                val minutes = groups[1]?.value?.toInt()
                if (minutes != null) duration += minutes.minutes
                val seconds = groups[2]?.value?.toInt()
                if (seconds != null) duration += seconds.seconds
                duration
            }) { "Failed to parse duration" }
    }
    else -> {
        milliseconds(text.toLong())
    }
}
