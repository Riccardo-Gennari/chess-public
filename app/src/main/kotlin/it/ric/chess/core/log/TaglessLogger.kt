package it.ric.chess.core.log

/**
 * A simpler logger interface for components that don't need to specify a tag on every call.
 * This is usually implemented by a wrapper around [Logger] that provides a fixed tag.
 */
interface TaglessLogger {
    /** @see Logger.debug */
    fun debug(
        message: String,
        exception: Throwable? = null,
    )

    /** @see Logger.error */
    fun error(
        message: String,
        exception: Throwable? = null,
    )

    /** @see Logger.exception */
    fun exception(exception: Throwable)

    /** @see Logger.info */
    fun info(
        message: String,
        exception: Throwable? = null,
    )

    /** @see Logger.warning */
    fun warning(
        message: String,
        exception: Throwable? = null,
    )

    /** @see Logger.setUID */
    fun setUID(uid: String)
}
