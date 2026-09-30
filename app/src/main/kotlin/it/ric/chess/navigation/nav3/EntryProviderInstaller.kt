package it.ric.chess.navigation.nav3

import androidx.navigation3.runtime.EntryProviderScope
import it.ric.chess.navigation.Route

/**
 * A typealias for a function that installs navigation entries into an [EntryProviderScope].
 * This is used for Hilt multibindings to decouple feature navigation from the main app module.
 */
typealias EntryProviderInstaller = EntryProviderScope<Route>.() -> Unit
