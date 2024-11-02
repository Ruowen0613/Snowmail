package ca.uwaterloo.persistence

import model.UserProfile

interface IAuthRepository {
    suspend fun signUpUser(email: String, password: String, firstname: String, lastname: String): Result<String>
    suspend fun signInUser(email: String, password: String): Result<String>
    suspend fun signOutUser(): String
}
