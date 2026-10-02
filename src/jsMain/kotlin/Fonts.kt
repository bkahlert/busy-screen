import kotlinx.coroutines.await
import org.w3c.dom.Document
import kotlin.js.Promise

/** The fonts the document's stylesheets declare, see [FontFaceSet](https://developer.mozilla.org/en-US/docs/Web/API/FontFaceSet). */
external interface FontFaceSet {
    fun forEach(callback: (FontFace) -> Unit)
    fun check(font: String): Boolean
}

external interface FontFace {
    fun load(): Promise<FontFace>
}

val Document.fonts: FontFaceSet get() = asDynamic().fonts.unsafeCast<FontFaceSet>()

/** Returns once every declared font is loaded or has failed to load; the browser falls back for a failed one. */
suspend fun Document.awaitFonts() {
    val loads = mutableListOf<Promise<*>>()
    fonts.forEach { loads += it.load().catch { } }
    Promise.all(loads.toTypedArray()).await()
}
