package com.saffron.cook.auth

import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<FirebaseUser?>
    val currentUserSnapshot: FirebaseUser?
    suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser>
    fun signOut()
}
