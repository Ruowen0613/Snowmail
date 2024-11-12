package controller

import ca.uwaterloo.model.Education
import ca.uwaterloo.model.WorkExperience
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

import service.EmailGenerationService
import model.UserInput
import model.UserProfile


fun Route.emailRoutes(emailGenerationService: EmailGenerationService) {
    post("/generate-email") {
        try {
            val userInput = call.receive<UserInput>()
            val userProfile = call.receive<UserProfile>()
            val education = call.receive<List<Education>>()
            val workExperience = call.receive<List<WorkExperience>>()
            val skills = call.receive<List<String>>()

            val generatedEmail = emailGenerationService.generateEmail(userInput, userProfile, education, workExperience, skills)
            call.respond(HttpStatusCode.OK, generatedEmail)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Failed to generate email: ${e.message}")
        }
    }
}

fun Application.configureRouting(emailGenerationService: EmailGenerationService) {
    routing {
        emailRoutes(emailGenerationService)
        get("/status") {
            call.respond(HttpStatusCode.OK, "Service is running")
        }
    }
}
