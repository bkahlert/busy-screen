@file:JsModule("dialog-polyfill")
@file:JsNonModule

package dependencies.dialog

import org.w3c.dom.HTMLElement

external interface DialogPolyfillType {
    fun registerDialog(dialog: HTMLElement)
    fun forceRegisterDialog(dialog: HTMLElement)
}

@JsName("default")
external var dialogPolyfill: DialogPolyfillType
