package it.ric.chess.core.log

/**
 * Core logging interface for the application.
 * Provides methods for different logging levels and a way to set a unique user identifier (UID).
 */
interface Logger {
    /**
     * Sets a unique user identifier to be included in subsequent log entries.
     * @param uid The unique identifier for the user.
     */
    fun setUID(uid: String)

    /**
     * Logs a debug message.
     * @param tag Used to identify the source of a log message.
     * @param message The message you would like logged.
     * @param exception An exception to log.
     */
    fun debug(
        tag: String,
        message: String,
        exception: Throwable? = null,
    )

    /**
     * Logs an exception as an error.
     * @param tag Used to identify the source of a log message.
     * @param exception The exception to log.
     */
    fun exception(
        tag: String,
        exception: Throwable,
    ) {
        error(tag, exception.message ?: "Exception encountered", exception)
    }

    /**
     * Logs an error message.
     * @param tag Used to identify the source of a log message.
     * @param message The message you would like logged.
     * @param exception An exception to log.
     */
    fun error(
        tag: String,
        message: String,
        exception: Throwable? = null,
    )

    /**
     * Logs an informational message.
     * @param tag Used to identify the source of a log message.
     * @param message The message you would like logged.
     * @param exception An exception to log.
     */
    fun info(
        tag: String,
        message: String,
        exception: Throwable? = null,
    )

    /**
     * Logs a warning message.
     * @param tag Used to identify the source of a log message.
     * @param message The message you would like logged.
     * @param exception An exception to log.
     */
    fun warning(
        tag: String,
        message: String,
        exception: Throwable? = null,
    )

    companion object {
        /**
         * Returns a no-op implementation of [Logger].
         */
        fun noOp(): Logger =
            object : Logger {
                override fun setUID(uid: String) {}

                override fun debug(
                    tag: String,
                    message: String,
                    exception: Throwable?,
                ) {}

                override fun error(
                    tag: String,
                    message: String,
                    exception: Throwable?,
                ) {}

                override fun info(
                    tag: String,
                    message: String,
                    exception: Throwable?,
                ) {}

                override fun warning(
                    tag: String,
                    message: String,
                    exception: Throwable?,
                ) {}
            }
    }
}
