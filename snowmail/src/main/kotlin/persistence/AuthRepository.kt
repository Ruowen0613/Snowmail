package ca.uwaterloo.persistence

import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import ca.uwaterloo.model.UserProfile
import io.github.jan.supabase.postgrest.from


class AuthRepository(private val supabase: SupabaseClient) {

    //register a new user with email and password and return user id
    //password needs to be at least 6 chars
    //if the email has already used, log in
    //have to check data is inserted successfully in user_profile (need to implemented later)
    suspend fun signUpUser(
        email: String,
        password: String,
        firstname: String,
        lastname: String
    ): Result<String> {
        return try {
            // Register user with built-in email provider
            supabase.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }

            // Get the user info from current session
            val user = supabase.auth.retrieveUserForCurrentSession(updateSession = true)

            // Insert firstname and lastname into the user_profile table
            val userProfile = UserProfile(
                user_id = user.id,
                first_name = firstname,
                last_name = lastname
            )
            supabase.from("user_profile").insert(userProfile)

            // Return user ID if successful
            Result.success(user.id)
        } catch (e: Exception) {
            // Return specific error message as failure
            val errorMessage = if (e.message?.contains("User already registered") == true) {
                "User already registered. Please sign in."
            } else {
                "Error during sign-up: ${e.message}"
            }
            Result.failure(Exception(errorMessage))
        }
    }

    //sign in the user with email and password
    suspend fun signInUser(email: String, password: String): Result<String> {
        return try {
            val result = supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }

            val user = supabase.auth.currentUserOrNull()
            if (user != null) {
                Result.success(user.id) // Sign-in successful, return user ID
            } else {
                Result.failure(Exception("Invalid credentials or session not created."))
            }
        } catch (e: Exception) {
            val errorMessage = when {
                e.message?.contains("Invalid login credentials") == true -> "Sign in in failed. Please use the correct email and password."
                else -> "Sign-in failed: ${e.message}"
            }
            Result.failure(Exception(errorMessage))
        }
    }

    //sign out the user in the current session
    suspend fun signOutUser(): String {
        return try {
            // Sign out the current user
            supabase.auth.signOut()
            "User signed out successfully."
        } catch (e: Exception) {
            "Error during sign-out: ${e.message}"
        }
    }
}
