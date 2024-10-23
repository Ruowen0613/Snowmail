package ca.uwaterloo.controller

import ca.uwaterloo.persistence.DBStorage
import kotlinx.coroutines.runBlocking

class SignInController(private val dbStorage: DBStorage) {

    // Sign up a new user and return either userId or error message
    fun signUpUser(email: String, password: String, firstname: String, lastname: String): Result<String> {
        return runBlocking {
            dbStorage.signUpUser(email, password, firstname, lastname)
        }
    }
}

// Testing SignInController
fun main() {
    val dbStorage = DBStorage()
    val signInController = SignInController(dbStorage)

    // Test user registration
    val email = "wrw040613@gmail.com"
    val password = "sssssss"
    val firstname = "Cherry"
    val lastname = "Wang"

    val result = signInController.signUpUser(email, password, firstname, lastname)

    result.onSuccess { userId ->
        println("Sign-up process completed. User ID: $userId")
    }.onFailure { error ->
        println("Sign-up process failed. Error: ${error.message}")
    }
}
