package service


import ca.uwaterloo.model.Education
import ca.uwaterloo.model.EducationWithDegreeName
import ca.uwaterloo.model.WorkExperience
import ca.uwaterloo.service.ParserService
import integration.OpenAIClient

import model.UserInput
import model.GeneratedEmail
import model.UserProfile
import java.io.File

class EmailGenerationService(private val openAIClient: OpenAIClient, private val parserService: ParserService) {

     suspend fun generateEmail(userInput: UserInput, userProfile: UserProfile, userResume: File, skills: List<String>): GeneratedEmail? {
          val cleanedInput = cleanInput(userInput)
          val resumeText = parserService.extractTextFromPDF(userResume)

          return try {
               parserService.parseEmailContent(openAIClient.generateEmail(resumeText, userInput, skills))
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
