package com.saffron.cook.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

class FirebaseAuthRepository(private val auth: FirebaseAuth) : AuthRepository {

    override val currentUser: Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser) }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override val currentUserSnapshot: FirebaseUser?
        get() = auth.currentUser

    override suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser> = runCatching {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        suspendCancellableCoroutine { cont ->
            auth.signInWithCredential(credential)
                .addOnSuccessListener { result ->
                    val user = result.user
                    if (user != null) cont.resume(user) { _, _, _ -> }
                    else cont.resumeWithException(Exception("Sign-in succeeded but user was null"))
                }
                .addOnFailureListener { cont.resumeWithException(it) }
        }
    }

    override fun signOut() = auth.signOut()
}
