package integration

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

import io.ktor.http.*
//import io.ktor.http.ContentType.Application.Json
//
//import kotlinx.serialization.encodeToString
//import kotlinx.serialization.json.Json


import kotlinx.serialization.json.Json
import model.UserInput
import model.GeneratedEmail
import model.OpenAIRequest
import model.UserProfile

class OpenAIClient(private val httpClient: HttpClient) {

    suspend fun generateEmail(userInput: UserInput, userProfile: UserProfile): GeneratedEmail {
        val prompt = buildPrompt(userInput, userProfile)

        val messages = listOf(
            mapOf(
                "role" to "system",
                "content" to """
                    You are a professional email generator that creates highly effective job application emails. 
                    The emails should be personalized, formal, and aligned with the user's, profile, skills and experience as they relate to the job description provided. 
                    Ensure the email includes:
                    - A courteous and professional greeting
                    - A brief introduction of the applicant
                    - Highlights of relevant skills and experiences tailored to the job description
                    - A clear, polite call to action for follow-up
                    - A formal closing
                    Keep the tone professional and succinct, avoiding overly casual language or excessive detail.
                """.trimIndent()
            ),
            mapOf("role" to "user", "content" to prompt)
        )

        println(messages)

        val request = OpenAIRequest(
            model = "gpt-3.5-turbo",
            messages = messages,
            max_tokens = 150
        )

        val response: HttpResponse = try {
            httpClient.post("https://api.openai.com/v1/chat/completions") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer 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")
                setBody(Json.encodeToString(OpenAIRequest.serializer(), request))
            }
        } catch (e: Exception) {
            throw RuntimeException("Failed to call OpenAI API: ${e.message}")
        }

        val responseBody: String = response.bodyAsText()

        println(responseBody)


       return parseGeneratedText(responseBody)

    }

    private fun buildPrompt(userInput: UserInput, userProfile: UserProfile): String {
        val skills = userProfile.skills?.joinToString(", ") ?: "Not provided"
        return """
            Job Description: ${userInput.jobDescription}
            User Profile:
            - First Name: ${userProfile.firstName}
            - Last Name: ${userProfile.lastName}
            - User ID: ${userProfile.userId}
            - Skills: $skills
        """.trimIndent()
    }

    private fun parseGeneratedText(responseBody: String): GeneratedEmail {
        // Implement the logic to parse the response body and extract the email content
        // This is a placeholder implementation
        return GeneratedEmail(
            subject = "Generated Subject",
            body = "Generated Body"
        )
    }
}

suspend fun main() {
    val openAIClient = OpenAIClient(HttpClient(CIO))
    val userInput = UserInput(
        jobDescription = "Software Engineer",
        recruiterEmail = "recruiter@example.com",
        jobTitle = "Software Engineer",
        company = "Example Corp",
        recruiterName = "Jane Doe"
    )

    val userProfile = UserProfile(
        userId = "123",
        firstName = "John",
        lastName = "Doe",
        skills = listOf("Java", "Kotlin", "SQL")
    )

    println(openAIClient.generateEmail(userInput, userProfile))
    println("Done")
}
