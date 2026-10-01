package com.bkahlert.kommons

import kotlin.js.Date
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

operator fun Date.minus(other: Date): Duration = (getTime() - other.getTime()).milliseconds

operator fun Date.plus(duration: Duration): Date = Date(getTime() + duration.inWholeMilliseconds)

operator fun Date.minus(duration: Duration): Date = Date(getTime() - duration.inWholeMilliseconds)
