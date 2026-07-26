package it.ric.chess.feature.match

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet
import it.ric.chess.navigation.EntryProviderInstaller

@Module
@InstallIn(ActivityRetainedComponent::class)
object ChessboardNavigationModule {
    @Provides
    @IntoSet
    fun provideChessboardEntry(): EntryProviderInstaller = {
        chessboard()
    }
}
