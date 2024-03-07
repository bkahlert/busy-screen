package com.bkahlert.kommons.dom

import org.w3c.dom.ItemArrayLike
import org.w3c.dom.asList

/**
 * Returns a list containing all elements that are instances of specified type parameter R.
 */
inline fun <reified R> ItemArrayLike<Any?>.filterIsInstance(): List<R> =
    asList().filterIsInstance<R>()


/**
 * Returns a list containing the results of applying the given [transform] function
 * to each element in the original collection.
 */
inline fun <T, R> ItemArrayLike<T>.map(transform: (T) -> R): List<R> =
    asList().map(transform)

/**
 * Returns a list containing the results of applying the given [transform] function
 * to each element of type [T] in the original collection.
 */
inline fun <reified T, reified R> ItemArrayLike<Any?>.mapInstance(transform: (T) -> R): List<R> =
    asList().filterIsInstance<T>().map(transform)

/**
 * Performs the given [action] on each element.
 */
inline fun <T> ItemArrayLike<T>.forEach(action: (T) -> Unit) {
    asList().forEach(action)
}

/**
 * Performs the given [action] on each element of type [T].
 */
inline fun <reified T> ItemArrayLike<Any?>.forEachInstance(action: (T) -> Unit) {
    asList().filterIsInstance<T>().forEach(action)
}

/**
 * Returns the first element, or `null` if the list is empty.
 */
fun <T> ItemArrayLike<T>.firstOrNull(): T? {
    return asList().firstOrNull()
}

/**
 * Returns the first element of type [R], or `null` if the list contains no such element.
 */
inline fun <reified R> ItemArrayLike<Any?>.firstInstanceOrNull(): R? {
    return asList().filterIsInstance<R>().firstOrNull()
}
