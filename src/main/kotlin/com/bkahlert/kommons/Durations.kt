package com.bkahlert.kommons

import com.bkahlert.kommons.time.hours
import com.bkahlert.kommons.time.minutes
import com.bkahlert.kommons.time.seconds
import kotlin.time.Duration

private val regex = Regex("P.*T" +
    "(?<sign>[+-])?" +
    "(?:(?<hours>\\d+)H)?" +
    "(?:(?<minutes>\\d+)M)?" +
    "(?:(?<seconds>\\d+)S)?" +
    "(?:(?<fraction>\\.\\d+))?" +
    "")

fun Duration.Companion.parse(text: String): Duration = when {
    text.startsWith("P") -> {
        requireNotNull(regex.matchEntire(text)
            ?.run {
                var duration = ZERO
                val sign = groups[1]?.value.let { if (it == "-") -1 else 1 }
                val hours = groups[2]?.value?.toInt()
                if (hours != null) duration += hours.hours
                val minutes = groups[3]?.value?.toInt()
                if (minutes != null) duration += minutes.minutes
                val seconds = groups[4]?.value?.toInt()
                if (seconds != null) duration += seconds.seconds
                val fraction = groups[5]?.value?.let { "0.$it".toDouble() }
                if (fraction != null) duration += fraction.seconds
                duration * sign
            }) { "Failed to parse duration" }
    }
    else -> {
        milliseconds(text.toLong())
    }
}
