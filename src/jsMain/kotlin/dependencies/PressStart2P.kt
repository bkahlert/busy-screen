package dependencies

import com.bkahlert.kommons.dom.appendCss
import com.bkahlert.kommons.dom.head
import kotlinx.css.FontStyle.Companion.normal
import kotlinx.css.FontWeight.Companion.w400
import kotlinx.css.fontFamily
import kotlinx.css.fontStyle
import kotlinx.css.fontWeight
import org.w3c.dom.Document

fun Document.appendPressStart2P() {

    val fileBaseName = "press-start-2p-v9-latin-ext_latin_greek_cyrillic-ext_cyrillic-regular"

    head().appendCss {
        fontFace {
            fontFamily = "'Press Start 2P'"
            fontStyle = normal
            fontWeight = w400
            put("src", "url('../fonts/$fileBaseName.eot'); /* IE9 Compat Modes */")
            put(
                "src", """
                  local(''),
                  url('../fonts/$fileBaseName.eot?#iefix') format('embedded-opentype'), /* IE6-IE8 */
                  url('../fonts/$fileBaseName.woff2') format('woff2'), /* Super Modern Browsers */
                  url('../fonts/$fileBaseName.woff') format('woff'), /* Modern Browsers */
                  url('../fonts/$fileBaseName.ttf') format('truetype'), /* Safari, Android, iOS */
                  url('../fonts/$fileBaseName.svg#PressStart2P') format('svg'); /* Legacy iOS */
            """.trimIndent()
            )
        }
    }
}
