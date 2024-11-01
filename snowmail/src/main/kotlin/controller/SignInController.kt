package ca.uwaterloo.controller

// import ca.uwaterloo.persistence.DBStorage
import kotlinx.coroutines.runBlocking

import integration.SupabaseClient

class SignInController(private val dbStorage: SupabaseClient) {

    //Sign in the user and return either the user id or error message
    suspend fun signInUser(email: String, password: String): Result<String> {
        return dbStorage.authRepository.signInUser(email, password)
    }

//    suspend fun logoutUser(): String {
//        return dbStorage.signOutUser()
//    }
}

fun main() = runBlocking<Unit> {
    val dbStorage = SupabaseClient()
    val signInController = SignInController(dbStorage)

    val email = "wrw040613@gmail.com"
    val password = "s"

    // call loginUser and return results
    println(signInController.signInUser(email, password))
}