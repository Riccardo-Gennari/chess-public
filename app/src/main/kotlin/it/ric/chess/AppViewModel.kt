package it.ric.chess

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.ric.chess.navigation.EntryProviderInstaller
import it.ric.chess.navigation.Nav3Navigator
import javax.inject.Inject

@HiltViewModel
class AppViewModel
    @Inject
    constructor(
        val navigator: Nav3Navigator,
        val entryProviderInstallers: Set<@JvmSuppressWildcards EntryProviderInstaller>,
    ) : ViewModel()
