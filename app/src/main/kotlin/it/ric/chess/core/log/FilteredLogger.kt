package it.ric.chess.core.log

/**
 * Wraps a [Logger] to provide call filtering based on [LogLevel].
 * Only logs with a priority equal to or higher than [threshold] will be delegated.
 *
 * @property threshold The minimum [LogLevel] required for a log message to be processed.
 * @property delegate The underlying [Logger] to which log calls are delegated.
 */
class FilteredLogger(
    private val threshold: LogLevel,
    private val delegate: Logger,
) : Logger by delegate {
    override fun debug(
        tag: String,
        message: String,
        exception: Throwable?,
    ) {
        if (LogLevel.DEBUG >= threshold) delegate.debug(tag, message, exception)
    }

    override fun info(
        tag: String,
        message: String,
        exception: Throwable?,
    ) {
        if (LogLevel.INFO >= threshold) delegate.info(tag, message, exception)
    }

    override fun warning(
        tag: String,
        message: String,
        exception: Throwable?,
    ) {
        if (LogLevel.WARNING >= threshold) delegate.warning(tag, message, exception)
    }

    override fun error(
        tag: String,
        message: String,
        exception: Throwable?,
    ) {
        if (LogLevel.ERROR >= threshold) delegate.error(tag, message, exception)
    }

    override fun exception(
        tag: String,
        exception: Throwable,
    ) {
        if (LogLevel.ERROR >= threshold) delegate.exception(tag, exception)
    }
}

/**
 * Creates a [FilteredLogger] wrapping this logger with the given [threshold].
 */
fun Logger.withFiltering(threshold: LogLevel) = FilteredLogger(threshold, this)
