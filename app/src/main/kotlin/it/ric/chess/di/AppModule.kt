package it.ric.chess.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import it.ric.chess.core.infrastructure.ActivityProvider
import it.ric.chess.core.log.LogcatLogger
import it.ric.chess.core.log.Logger
import it.ric.chess.core.model.AppInfo
import it.ric.chess.datasource.firebase.Firebase
import it.ric.chess.datasource.playgames.AndroidPlayGamesDataSource
import it.ric.chess.datasource.playgames.PlayGamesDataSource
import kotlinx.coroutines.flow.SharingStarted
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun provideSharingStarted(): SharingStarted = SharingStarted.WhileSubscribed(5_000)

    @Provides
    @Singleton
    fun provideAppInfo(): AppInfo = AppInfoFactory.fromBuildConfig()

    @Provides
    @Singleton
    fun provideLogger(appInfo: AppInfo): Logger = LogcatLogger(appTag = appInfo.appName)

    @Provides
    @Singleton
    fun provideFirebase(logger: Logger): Firebase = Firebase(logger)

    @Provides
    @Singleton
    fun providePlayGamesDataSource(
        activityProvider: ActivityProvider,
        logger: Logger,
    ): PlayGamesDataSource = AndroidPlayGamesDataSource(activityProvider, logger)
}
