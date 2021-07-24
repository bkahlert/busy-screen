package koodies.time

import kotlin.js.Date
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

inline val Now: Date get() = Date()


@ExperimentalTime
inline operator fun Date.plus(date: Date): Duration = Duration.milliseconds(getTime().toLong() + date.getTime().toLong())

@ExperimentalTime
inline operator fun Date.minus(date: Date): Duration = Duration.milliseconds(getTime().toLong() - date.getTime().toLong())


@ExperimentalTime
inline operator fun Date.plus(duration: Duration): Date = Date(getTime().toLong() + duration.inWholeMilliseconds)

@ExperimentalTime
inline operator fun Date.minus(duration: Duration): Date = Date(getTime().toLong() - duration.inWholeMilliseconds)
