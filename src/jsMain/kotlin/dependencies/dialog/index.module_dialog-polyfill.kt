@file:JsModule("dialog-polyfill")
@file:JsNonModule
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")

package dependencies.dialog

import org.w3c.dom.HTMLElement

external interface DialogPolyfillType {
    fun registerDialog(dialog: HTMLElement)
    fun forceRegisterDialog(dialog: HTMLElement)
}

@JsName("default")
external var dialogPolyfill: DialogPolyfillType
