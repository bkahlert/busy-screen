package koodies.dom

import org.w3c.dom.Location
import org.w3c.dom.url.URL
import org.w3c.dom.url.URLSearchParams

val Location.searchParams: URLSearchParams get() = URL(href).searchParams
