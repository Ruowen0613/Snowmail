package ca.uwaterloo.controller

// import ca.uwaterloo.persistence.DBStorage
import ca.uwaterloo.persistence.IAuthRepository
import kotlinx.coroutines.runBlocking

import integration.SupabaseClient

class SignInController(private val authRepository: IAuthRepository) {

    //Sign in the user and return either the user id or error message
    suspend fun signInUser(email: String, password: String): Result<String> {
        return authRepository.signInUser(email, password)
    }

    suspend fun logoutUser(): String {
        return authRepository.signOutUser()
    }

    //Send OTP to the user's email1
    suspend fun sendOtpToEmail(email: String): Result<Boolean> {
        return authRepository.sendOtpToEmail(email)
    }

    //Sign in the user with email and OTP
    suspend fun signInUserWithOTP(email: String, otp: String): Result<String> {
        return authRepository.verifyEmailOtp(email, otp)
    }

    //send reset password email
    suspend fun sendResetPasswordEmail(email: String): Result<Boolean> {
        return SupabaseClient().authRepository.sendResetPasswordEmail(email)
    }

    //reset password
    suspend fun resetPassword(newPassword: String): Result<Boolean> {
        return SupabaseClient().authRepository.resetPassword(newPassword)
    }

    //parse and import session
    suspend fun parseAndImportSession(url: String): Result<Boolean> {
        return SupabaseClient().authRepository.parseAndImportSession(url)
    }


}

fun main() = runBlocking<Unit> {
    val dbStorage = SupabaseClient()
    val signInController = SignInController(dbStorage.authRepository)

    val email = "wrw040613@gmail.com"
    val password = "s"

    // call loginUser and return results
    println(signInController.signInUser(email, password))
}