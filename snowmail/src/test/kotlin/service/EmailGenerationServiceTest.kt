package service

import ca.uwaterloo.model.*
import ca.uwaterloo.service.ParserService
import integration.OpenAIClient
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import model.GeneratedEmail
import model.UserInput
import model.UserProfile
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import service.EmailGenerationService
import java.io.File

class EmailGenerationServiceTest {

    private val openAIClient = mockk<OpenAIClient>()
    private val parserService = mockk<ParserService>()
    private val emailGenerationService = EmailGenerationService(openAIClient, parserService)

    @Test
    fun `test generateEmailFromResume`() = runBlocking {
        val userInput = UserInput("test@example.com", "Test Subject", "Test Body")
        val userProfile = UserProfile("John Doe", "john.doe@example.com")
        val resumeFile = File.createTempFile("resume", ".pdf")
        val resumeText = "Extracted resume text"
        val generatedEmailContent = "Generated email content"
        val generatedEmail = GeneratedEmail("Test Subject", "Generated Body")

        coEvery { parserService.extractTextFromPDF(resumeFile) } returns resumeText
        coEvery { openAIClient.generateEmailFromResume(userInput, userProfile, resumeText) } returns generatedEmailContent
        coEvery { parserService.parseEmailContent(generatedEmailContent) } returns generatedEmail

        val result = emailGenerationService.generateEmailFromResume(userInput, userProfile, resumeFile)

        assertNotNull(result)
        assertEquals(generatedEmail, result)
    }

    @Test
    fun `test generateEmailFromProfile`() = runBlocking {
        val userInput = UserInput("Building scalable ", "Software Engineer", "Google")
        val userProfile = UserProfile("123", "John", "Doe", "john.doe@gmail.com")
        val education = listOf(EducationWithDegreeName(1, "123", "Bachelor's", "University of Waterloo", "Computer Science", 4.0f,  LocalDate.parse("2019-09-01"), LocalDate.parse("2023-06-01")))
        val workExperience = listOf(WorkExperience("Company", "Title", "Description", "2020-01-01", "2021-01-01"))
        val projects = listOf(PersonalProject("Project", "Description", "2020-01-01", "2021-01-01"))
        val skills = listOf("Kotlin", "Java")
        val generatedEmailContent = "Generated email content"
        val generatedEmail = GeneratedEmail("Test Subject", "Generated Body")

        coEvery { openAIClient.generateEmailFromProfile(userInput, userProfile, education, workExperience, projects, skills) } returns generatedEmailContent
        coEvery { parserService.parseEmailContent(generatedEmailContent) } returns generatedEmail

        val result = emailGenerationService.generateEmailFromProfile(userInput, userProfile, education, workExperience, projects, skills)

        assertNotNull(result)
        assertEquals(generatedEmail, result)
    }
}
