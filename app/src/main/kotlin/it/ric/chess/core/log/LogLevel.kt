package it.ric.chess.core.log

/**
 * Defines the priority of a log message.
 * Order is important for log level checks (least important to most important).
 */
enum class LogLevel {
    /**
     * Used for detailed debugging information.
     */
    DEBUG,

    /**
     * Used for general informational messages.
     */
    INFO,

    /**
     * Used for potentially harmful situations.
     */
    WARNING,

    /**
     * Used for error events that might still allow the application to continue running.
     */
    ERROR,
}
