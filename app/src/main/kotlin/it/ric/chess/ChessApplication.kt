package it.ric.chess

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import com.google.android.gms.games.PlayGamesSdk
import com.google.firebase.Firebase
import com.google.firebase.initialize
import dagger.hilt.android.HiltAndroidApp
import it.ric.chess.core.infrastructure.ActivityProvider
import it.ric.chess.datasource.playgames.PlayGamesUriFetcher
import javax.inject.Inject

@HiltAndroidApp
class ChessApplication :
    Application(),
    SingletonImageLoader.Factory {
    @Inject
    lateinit var activityProvider: ActivityProvider

    override fun onCreate() {
        super.onCreate()
        Firebase.initialize(this)
        PlayGamesSdk.initialize(this)
        registerActivityLifecycleCallbacks(activityProvider)
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader =
        ImageLoader
            .Builder(context)
            .components {
                add(PlayGamesUriFetcher.Factory(context))
            }.build()
}
