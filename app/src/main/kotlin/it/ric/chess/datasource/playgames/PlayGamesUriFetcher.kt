package it.ric.chess.datasource.playgames

import android.content.Context
import android.graphics.drawable.Drawable
import coil3.ImageLoader
import coil3.Uri
import coil3.asImage
import coil3.decode.DataSource
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.fetch.ImageFetchResult
import coil3.request.Options
import coil3.toAndroidUri
import coil3.toCoilUri
import com.google.android.gms.common.images.ImageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

/**
 * A Coil [Fetcher] that loads images from Google Play Games Service's restricted content provider.
 */
class PlayGamesUriFetcher(
    private val data: android.net.Uri,
    private val context: Context,
) : Fetcher {
    override suspend fun fetch(): FetchResult? {
        val drawable = loadPlayGamesImage(context, data) ?: return null
        return ImageFetchResult(
            image = drawable.asImage(),
            isSampled = false,
            dataSource = DataSource.DISK,
        )
    }

    private suspend fun loadPlayGamesImage(
        context: Context,
        uri: android.net.Uri,
    ): Drawable? =
        withContext(Dispatchers.Main.immediate) {
            suspendCancellableCoroutine { continuation ->
                ImageManager.create(context).loadImage(
                    { _, drawable, _ -> continuation.resume(drawable) },
                    uri,
                )
            }
        }

    class Factory(
        private val context: Context,
    ) : Fetcher.Factory<Any> {
        override fun create(
            data: Any,
            options: Options,
            imageLoader: ImageLoader,
        ): Fetcher? {
            val coilUri =
                when (data) {
                    is Uri -> data
                    is android.net.Uri -> data.toCoilUri()
                    else -> return null
                }

            if (coilUri.authority == PLAY_GAMES_AUTHORITY) {
                return PlayGamesUriFetcher(coilUri.toAndroidUri(), context)
            }
            return null
        }
    }

    companion object {
        private const val PLAY_GAMES_AUTHORITY = "com.google.android.gms.games.background"
    }
}
