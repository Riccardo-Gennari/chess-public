package it.ric.chess.core.log

/**
 * Returns the simple name of the class of the object.
 * Useful for logging tags.
 */
val Any.tag: String get() = javaClass.simpleName
