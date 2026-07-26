package it.ric.chess.di

import it.ric.chess.BuildConfig
import it.ric.chess.core.model.AppInfo

object AppInfoFactory {
    fun fromBuildConfig() =
        AppInfo(
            appName = BuildConfig.APP_NAME,
        )
}
