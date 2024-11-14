package controller

import ca.uwaterloo.model.Education
import ca.uwaterloo.model.EducationWithDegreeName
import ca.uwaterloo.model.PersonalProject
import ca.uwaterloo.model.WorkExperience
import ca.uwaterloo.service.ParserService
import integration.OpenAIClient
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import model.GeneratedEmail

import service.EmailGenerationService
import model.UserInput
import model.UserProfile
import java.io.File


class EmailGenerationController(private val emailGenerationService: EmailGenerationService) {

    suspend fun generateEmail(
        informationSource: String,
        userInput: UserInput,
        userProfile: UserProfile,
        education: List<EducationWithDegreeName> = emptyList(),
        workExperience: List<WorkExperience> = emptyList(),
        projects: List<PersonalProject> = emptyList(),
        skills: List<String> = emptyList(),
        resumeFile: File? = null
    ): GeneratedEmail? {
        return if (resumeFile != null && informationSource == "resume") {
            emailGenerationService.generateEmailFromResume(userInput, userProfile, resumeFile)
        } else {
            emailGenerationService.generateEmailFromProfile(userInput, userProfile, education, workExperience, projects, skills)
        }
    }

}

