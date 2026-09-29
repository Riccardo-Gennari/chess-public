package it.ric.chess.di

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class AppInfoFactoryTest :
    FunSpec({

        test("fromBuildConfig should return AppInfo with BuildConfig appName") {
            val appInfo = AppInfoFactory.fromBuildConfig()
            appInfo.appName shouldBe it.ric.chess.BuildConfig.APP_NAME
        }
    })
