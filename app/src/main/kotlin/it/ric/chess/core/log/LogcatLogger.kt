package it.ric.chess.core.log

import android.util.Log

internal class LogcatLogger(
    private val appTag: String,
) : Logger {
    override fun setUID(uid: String) {
        Log.d(appTag, "UID: $uid")
    }

    override fun debug(
        tag: String,
        message: String,
        exception: Throwable?,
    ) {
        Log.d("$appTag/$tag", message, exception)
    }

    override fun error(
        tag: String,
        message: String,
        exception: Throwable?,
    ) {
        Log.e("$appTag/$tag", message, exception)
    }

    override fun info(
        tag: String,
        message: String,
        exception: Throwable?,
    ) {
        Log.i("$appTag/$tag", message, exception)
    }

    override fun warning(
        tag: String,
        message: String,
        exception: Throwable?,
    ) {
        Log.w("$appTag/$tag", message, exception)
    }
}
