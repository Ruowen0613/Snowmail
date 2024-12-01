package ca.uwaterloo

import androidx.compose.runtime.*
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import ca.uwaterloo.service.ParserService
import ca.uwaterloo.view.pages.*
import ca.uwaterloo.view.theme.AppTheme
import integration.OpenAIClient
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import service.EmailGenerationService

fun main() {
    // Initialize the OpenAIClient
    val httpClient = HttpClient(CIO)
    val openAIClient = OpenAIClient(httpClient)

    val parserService = ParserService(openAIClient)

    // Initialize the EmailGenerationService
    val emailGenerationService = EmailGenerationService(openAIClient, parserService)


    application {
        AppTheme {
            Window(onCloseRequest = ::exitApplication, title = "Snowmail", state = WindowState(size = DpSize(1200.dp, 800.dp))) {
                websitePage()
            }
        }
    }
}


@Composable
fun websitePage() {

    var currentPage by remember { mutableStateOf("welcome") }

    when (currentPage) {
        "login" -> loginPage ({ currentPage = "signup" }, { currentPage = "welcome" }, {currentPage = "profilePage"})
        "signup" -> SignUpPage ({ currentPage = "login"}, { currentPage = "welcome" }, { currentPage = "login"})
        "welcome" -> WelcomePage ({ currentPage = "signup"}, {currentPage = "login"}, {currentPage = "introductionPage"})
        "introductionPage" -> IntroductionPage ({ currentPage = "signup"}, {currentPage = "login"}, {currentPage = "welcome"}, {currentPage = "coldEmailGenerationIntroduction"}, {currentPage = "jobApplicationProgressIntroduction"}, {currentPage = "sendEmailsDirectlyIntroduction"})
        "coldEmailGenerationIntroduction" -> ColdEmailGenerationIntroduction ({ currentPage = "signup"}, {currentPage = "login"}, {currentPage = "introductionPage"}, {currentPage = "jobApplicationProgressIntroduction"})
        "jobApplicationProgressIntroduction" -> JobApplicationProgressIntroduction ({ currentPage = "signup"}, {currentPage = "login"}, {currentPage = "introductionPage"}, {currentPage = "sendEmailsDirectlyIntroduction"})
        "sendEmailsDirectlyIntroduction" -> SendEmailsDirectlyIntroduction ({ currentPage = "signup"}, {currentPage = "login"}, {currentPage = "introductionPage"})

        "profilePage" -> ProfilePage(UserSession.userId ?: "DefaultUserId", { currentPage = "documentPage"}, { currentPage = "emailgeneration"}, { currentPage = "progressPage"}, {currentPage = "login"})
        "emailgeneration" -> EmailGenerationPage(UserSession.userId ?: "DefaultUserId", { currentPage = "documentPage"}, { currentPage = "profilePage"}, { currentPage = "progressPage"}, { currentPage = "login"})
        "progressPage" -> JobProgressPage(UserSession.userId ?: "DefaultUserId", { currentPage = "documentPage"}, { currentPage = "profilePage"}, { currentPage = "emailgeneration"}, { currentPage = "login"})
        "documentPage" -> DocumentPage({ currentPage = "emailgeneration"}, { currentPage = "profilePage"}, { currentPage = "progressPage"}, { currentPage = "login"})
    }
}


