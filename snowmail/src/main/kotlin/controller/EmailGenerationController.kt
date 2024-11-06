package controller

import ca.uwaterloo.model.Education
import ca.uwaterloo.model.WorkExperience
import integration.OpenAIClient
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import model.GeneratedEmail

import model.UserInput
import model.UserProfile


class EmailGenerationController(private val openAIClient: OpenAIClient) {

    suspend fun generateEmail(userInput: UserInput, userProfile: UserProfile, education: List<Education>, workExperience: List<WorkExperience>): GeneratedEmail {
        val cleanedInput = cleanInput(userInput)

        return try {
            openAIClient.generateEmail(cleanedInput, userProfile, education, workExperience)
        } catch (e: Exception) {
            // Handle exceptions and return a meaningful error response
            throw RuntimeException("Failed to generate email: ${e.message}")
        }
    }

    private fun cleanInput(userInput: UserInput): UserInput {
        // Implement input cleaning logic here
        return userInput
    }
}
