package com.saffron.cook.core.testing

import com.google.firebase.auth.FirebaseUser
import com.saffron.cook.core.auth.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeAuthRepository : AuthRepository {
    override val currentUser: Flow<FirebaseUser?> = flowOf(null)
    override val currentUserSnapshot: FirebaseUser? = null

    override suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser> =
        Result.failure(UnsupportedOperationException("not implemented in fake"))

    override fun signOut() = Unit
}
