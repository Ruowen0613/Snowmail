package integration

import ca.uwaterloo.model.Education
import ca.uwaterloo.model.EducationWithDegreeName
import ca.uwaterloo.model.WorkExperience
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

import io.ktor.http.*
import kotlinx.datetime.LocalDate
import kotlinx.serialization.json.Json


import model.UserInput
import model.GeneratedEmail
import model.OpenAIRequest
import model.UserProfile


@Serializable
data class ChatCompletionResponse(
    val id: String,
    val choices: List<Choice>
)

@Serializable
data class Choice(
    val message: Message
)

@Serializable
data class Message(
    val content: String
)


class OpenAIClient(private val httpClient: HttpClient) {


    suspend fun generateEmail2(resumeText:String, userInput: UserInput): String? {
        val prompt = buildPrompt2(resumeText, userInput)
        val message = prepareMessage(prompt)
        val response = sendOpenAIRequest(message)
        val emailContent = getEmailContent(response)
        return emailContent
    }


    suspend fun generateEmail(userInput: UserInput, userProfile: UserProfile, education: List<Education>, workExperience: List<WorkExperience>, skills: List<String>): GeneratedEmail {
        val prompt = buildPrompt(userInput, userProfile, education, workExperience)
        val message = prepareMessage(prompt)
        val response = sendOpenAIRequest(message)
        val emailContent = getEmailContent(response)
        return parseGeneratedText(emailContent)
    }

    private fun buildPrompt2(resumeText: String, userInput: UserInput): String {
        val companyName = userInput.company
        val jobDescription = userInput.jobDescription
        val recruiterName = userInput.recruiterName

        return """
            The company I am looking to apply to is $companyName, with the following job description: $jobDescription.
            
            Here is my resume:
            $resumeText
            
            Send a job application email to the recruiter, $recruiterName, that is personalized, formal, and aligned with my profile, skills, and experience as they relate to the job description provided. 
        """.trimIndent()
    }



    private fun buildPrompt(userInput: UserInput, userProfile: UserProfile, education: List<Education>, workExperience: List<WorkExperience>): String {
        val skills = userProfile.skills?.joinToString(", ") ?: "Not provided"

        val educationDetails = education.joinToString("\n") { e ->
            """
            - Institution: ${e.institutionName}
            - Degree: ${e.major}
            - GPA: ${e.gpa ?: "Not provided"}
            - End Date: ${e.endDate}
            """.trimIndent()
        }

        val workExperienceDetails = workExperience.joinToString("\n") { w ->
            """
            - Company: ${w.companyName}
            - Position: ${w.title}
            - Description: ${w.description}
            """.trimIndent()
        }


        return """
            Job Description: ${userInput.jobDescription}
            User Profile:
            - First Name: ${userProfile.firstName}
            - Last Name: ${userProfile.lastName}
            - Skills: $skills
            Education:
            $educationDetails
            Work Experience:
            $workExperienceDetails
            """.trimIndent()
    }

    private fun prepareMessage(prompt: String): List<Map<String, String>> {
        return listOf(
            mapOf(
                "role" to "system",
                "content" to """
                    You are a professional email generator that creates highly effective job application emails. 
                    The emails should be personalized, formal, and aligned with the user's profile, skills, and experience as they relate to the job description provided. 
                    Ensure the email includes:
                    - A courteous and professional greeting
                    - A brief introduction of the applicant
                    - Highlights of relevant skills and experiences tailored to the job description
                    - A clear, polite call to action for follow-up
                    - A welcome with "Hi" or "Hello", followed up the recruiter's name 
                    - Always specify the subject with "Subject:" followed by the subject of the email
                    - A formal closing with the applicant's name and something like "Best regards" or "Sincerely"
                    Keep the tone professional and succinct, avoiding overly casual language or excessive detail.
                    
                    Real Example of a Job Application Email:
                    Subject: Software Engineer Internship Opportunities at Coinbase
                    
                    Hi Jane,

                    I hope this message finds you well. My name is John Doe, a third-year Computer Science student at the University of Waterloo, and I’m excited about the innovative work happening at Coinbase. With my background in data engineering and software development, I’m confident I can contribute meaningfully to your team.
                    Here’s a quick snapshot of my relevant experience:

                    - Manulife: As a Data Engineer Intern, I worked on optimizing CI/CD pipelines, automated GitHub  repo access management, and implemented scalable solutions for database migrations and cloud deployment.
                    - Baraka (YC ’21): As a Software Engineer Intern, I developed backend systems, deployed scalable solutions, and built efficient ETL pipelines for financial data processing.

                    I would love to discuss your team’s current challenges and explore how I can help solve them. I'm happy to volunteer my time to demonstrate the value I can bring. Would you be available for a brief 15-minute conversation this week?
                    I have attached my resume, looking forward to hearing from you.

                    Best regards,
                    John Doe
                    
                """.trimIndent()
            ),
            mapOf("role" to "user", "content" to prompt))
    }

    private suspend fun sendOpenAIRequest(message: List<Map<String, String>>): HttpResponse {
        val request = OpenAIRequest(
            model = "gpt-3.5-turbo",
            messages = message,
            max_tokens = 500
        )

        return try {
            httpClient.post("https://api.openai.com/v1/chat/completions") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer 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")
                setBody(Json.encodeToString(OpenAIRequest.serializer(), request))
            }
        } catch (e: Exception) {
            throw RuntimeException("Failed to call OpenAI API: ${e.message}")
        }
    }

    private suspend fun getEmailContent(response: HttpResponse): String? {
        val responseBody: String = response.bodyAsText()

        val json = Json {
            ignoreUnknownKeys = true // This will ignore unknown keys
        }

        return try {
            val parsedResponse = json.decodeFromString(ChatCompletionResponse.serializer(), responseBody)
            parsedResponse.choices.firstOrNull()?.message?.content
        } catch (e: Exception) {
            println("Failed to parse content: ${e.message}")
            null
        }
    }

    private suspend fun parseGeneratedText(responseBody: String?): GeneratedEmail {
        // This needs to be improved
        val prompt = """
            This is the generated email:
            $responseBody
            
            Return a tuple with the subject in the first element and the body in the second element.
        """.trimIndent()



        val message = prepareMessage(prompt)
        val response = sendOpenAIRequest(message)
        val emailContent = getEmailContent(response)

        return GeneratedEmail("Subject", emailContent)
    }


    suspend fun parseResume(resumeText: String): Map<String, Any> {
        val prompt = """
            Extract the following details from the resume:
            - Name
            - Email
            - Phone
            - Education
            - Work Experience
            - Skills
    
            Resume:
            $resumeText
        """.trimIndent()

        val message = listOf(
            mapOf("role" to "user", "content" to prompt)
        )

        val request = OpenAIRequest(
            model = "gpt-3.5-turbo",
            messages = message,
            max_tokens = 1000
        )

        val response: HttpResponse = httpClient.post("https://api.openai.com/v1/chat/completions") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer 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")
            setBody(Json.encodeToString(OpenAIRequest.serializer(), request))
        }

        val responseBody: String = response.bodyAsText()
        println("OpenAI API response: $responseBody")

        val json = Json { ignoreUnknownKeys = true }
        return try {
            val parsedResponse = json.decodeFromString(ChatCompletionResponse.serializer(), responseBody)
            val extractedText = parsedResponse.choices.firstOrNull()?.message?.content ?: "No data extracted"

            // Parse the extracted text to create a user profile object
            val userProfileDetails = mutableMapOf<String, Any>()
            val lines = extractedText.split("\n")
            for (line in lines) {
                val parts = line.split(":")
                if (parts.size == 2) {
                    userProfileDetails[parts[0].trim()] = parts[1].trim()
                }
            }
            userProfileDetails
        } catch (e: Exception) {
            println("Failed to parse response: ${e.message}")
            throw RuntimeException("Failed to parse response: ${e.message}")
        }
    }
}

suspend fun main() {
//    val openAIClient = OpenAIClient(HttpClient(CIO))
//    val userInput = UserInput(
//        jobDescription = "Software Engineer",
//        recruiterEmail = "recruiter@example.com",
//        jobTitle = "Software Engineer",
//        company = "Example Corp",
//        recruiterName = "Jane Doe",
//        fileURLs = listOf("https://example.com/resume.pdf"),
//    )
//
//    val userProfile = UserProfile(
//        userId = "123",
//        firstName = "John",
//        lastName = "Doe",
//        skills = listOf("Java", "Kotlin", "SQL")
//    )
//
//    val education = Education(
//        id = 12,
//        userId = "123",
//        degreeId = 3,
//        institutionName = "University of Waterloo",
//        major = "Computer Science",
//        gpa = 3.8f,
//        startDate = LocalDate(2019, 9, 1),
//        endDate = LocalDate(2023, 6, 1)
//    )
//
//
//
//    val workExperience = WorkExperience(
//        userId = "123",
//        currentlyWorking = false,
//        startDate = LocalDate(2021, 5, 1),
//        endDate = LocalDate(2021, 8, 1),
//        companyName = "Example Corp",
//        title = "Software Engineer",
//        description = "Developed backend systems, deployed scalable solutions, and built efficient ETL pipelines for financial data processing."
//    )
//
//    println(openAIClient.generateEmail(userInput, userProfile, listOf(education), listOf(workExperience)))
}
