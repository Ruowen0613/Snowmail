package controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
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

            val generatedEmail = emailGenerationService.generateEmail(userInput, userProfile)
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
