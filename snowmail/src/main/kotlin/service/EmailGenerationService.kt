package service


import ca.uwaterloo.model.Education
import ca.uwaterloo.model.EducationWithDegreeName
import ca.uwaterloo.model.WorkExperience
import integration.OpenAIClient

import model.UserInput
import model.GeneratedEmail
import model.UserProfile

class EmailGenerationService(private val openAIClient: OpenAIClient) {

     suspend fun generateEmail(userInput: UserInput, userProfile: UserProfile, education: List<EducationWithDegreeName>, workExperience: List<WorkExperience>, skills: List<String>): GeneratedEmail {
          val cleanedInput = cleanInput(userInput)

          return try {
               openAIClient.generateEmail(cleanedInput, userProfile, education, workExperience, skills)
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
