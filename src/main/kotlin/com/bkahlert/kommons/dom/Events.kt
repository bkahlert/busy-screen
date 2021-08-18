package com.bkahlert.kommons.dom

import org.w3c.dom.events.Event

inline fun <reified T> Event.currentTarget(): T? =
    currentTarget.let { it as? T }
