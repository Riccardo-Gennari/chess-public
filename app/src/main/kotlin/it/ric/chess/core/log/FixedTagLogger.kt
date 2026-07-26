package it.ric.chess.core.log

/**
 * Wraps a [Logger] to provide a [TaglessLogger] implementation with a pre-defined tag.
 *
 * @property tag The fixed tag to use for all log calls.
 * @property logger The underlying [Logger] to which log calls are delegated.
 */
class FixedTagLogger(
    val tag: String,
    private val logger: Logger,
) : TaglessLogger {
    override fun setUID(uid: String) = logger.setUID(uid)

    override fun debug(
        message: String,
        exception: Throwable?,
    ) = logger.debug(tag, message, exception)

    override fun error(
        message: String,
        exception: Throwable?,
    ) = logger.error(tag, message, exception)

    override fun info(
        message: String,
        exception: Throwable?,
    ) = logger.info(tag, message, exception)

    override fun warning(
        message: String,
        exception: Throwable?,
    ) = logger.warning(tag, message, exception)

    override fun exception(exception: Throwable) = logger.exception(tag, exception)
}

/**
 * Creates a [FixedTagLogger] wrapping this logger with the given [tag].
 */
fun Logger.withFixedTag(tag: String) = FixedTagLogger(tag, this)
