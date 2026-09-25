package it.ric.chess.datasource.firebase

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import it.ric.chess.core.log.Logger
import it.ric.chess.core.log.tag
import it.ric.chess.core.log.withFixedTag
import it.ric.chess.domain.model.AuthUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import com.google.firebase.Firebase as GoogleFirebase

class Firebase(
    log: Logger,
) {
    private val log = log.withFixedTag(tag)
    private val auth: FirebaseAuth by lazy { GoogleFirebase.auth }
    private val database: FirebaseDatabase by lazy { GoogleFirebase.database }

    val uid: String? get() = auth.currentUser?.uid

    /**
     * Authenticates with a credential.
     */
    suspend fun signInWithCredential(credential: AuthCredential) {
        auth.signInWithCredential(credential).await()
    }

    /**
     * Signs out from Firebase.
     */
    fun signOut() {
        auth.signOut()
    }

    /**
     * Observes authentication state changes.
     */
    fun observeAuthState(): Flow<AuthUser?> =
        callbackFlow {
            val listener =
                FirebaseAuth.AuthStateListener { auth ->
                    val user = auth.currentUser
                    if (user != null) {
                        trySend(AuthUser(user.uid))
                    } else {
                        trySend(null)
                    }
                }
            auth.addAuthStateListener(listener)
            awaitClose { auth.removeAuthStateListener(listener) }
        }

    fun generateId(path: String): String {
        val key = database
            .getReference(path)
            .push()
            .key

        requireNotNull(key) { "Failed to generate ID for path: ${android.R.attr.path}" }

        return key
    }

    suspend fun <T> setValue(
        path: String,
        value: T,
    ) {
        database.getReference(path).setValue(value).await()
    }

    suspend fun updateChildren(
        path: String,
        updates: Map<String, Any?>,
    ) {
        database.getReference(path).updateChildren(updates).await()
    }

    suspend fun <T : Any> getValue(
        path: String,
        type: Class<T>,
    ): T? =
        database
            .getReference(path)
            .get()
            .await()
            .getValue(type)

    fun observeValue(path: String): Flow<DataSnapshot> =
        callbackFlow {
            val ref = database.getReference(path)
            val listener =
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        trySend(snapshot)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        log.error("Query observation cancelled: ${error.message}", error.toException())
                        close(error.toException())
                    }
                }
            ref.addValueEventListener(listener)
            awaitClose { ref.removeEventListener(listener) }
        }

    fun observeQuery(queryBuilder: (DatabaseReference) -> Query): Flow<DataSnapshot> =
        callbackFlow {
            val query = queryBuilder(database.reference)
            val listener =
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        trySend(snapshot)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        log.error("Query observation cancelled: ${error.message}", error.toException())
                        close(error.toException())
                    }
                }
            query.addValueEventListener(listener)
            awaitClose { query.removeEventListener(listener) }
        }
}
