package ca.uwaterloo

import androidx.compose.runtime.*
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import ca.uwaterloo.service.ParserService
import ca.uwaterloo.view.*
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

    // Start the Ktor server in a separate thread
//    thread {
//        embeddedServer(Netty, port = 8080) {
//            configureRouting(emailGenerationService)
//        }.start(wait = true)
//    }


    application {
        Window(onCloseRequest = ::exitApplication, title = "Snowmail", state = WindowState(size = DpSize(1200.dp, 800.dp))) {
            websitePage()
        }
    }
}


@Composable
fun websitePage() {

    var currentPage by remember { mutableStateOf("welcome") }

    when (currentPage) {
        "login" -> loginPage ({ currentPage = "signup" }, {currentPage = "profilePage"})
        "signup" -> SignUpPage ({ currentPage = "login"}, { currentPage = "login"})
        "welcome" -> WelcomePage ({ currentPage = "signup"}, {currentPage = "login"}, {currentPage = "welcome1"})
        "welcome1" -> WelcomePage1 ({ currentPage = "signup"}, {currentPage = "login"}, {currentPage = "welcome2"}, {currentPage = "welcome3"}, {currentPage = "welcome4"})
        "welcome2" -> WelcomePage2 ({ currentPage = "signup"}, {currentPage = "login"}, {currentPage = "welcome3"})
        "welcome3" -> WelcomePage3 ({ currentPage = "signup"}, {currentPage = "login"}, {currentPage = "welcome4"})
        "welcome4" -> WelcomePage4 ({ currentPage = "signup"}, {currentPage = "login"})
<<<<<<< snowmail/src/main/kotlin/Main.kt
        "profilePage" -> ProfilePage(UserSession.userId ?: "DefaultUserId", { currentPage = "profilePage"}, { currentPage = "emailgeneration"}, { currentPage = "progressPage"})
        "emailgeneration" -> EmailGenerationPage({ currentPage = "profilePage"}, { currentPage = "profilePage"}, { currentPage = "progressPage"})
        "progressPage" -> JobProgressPage(UserSession.userId ?: "DefaultUserId", {currentPage = "profilePage"}, { currentPage = "profilePage"}, { currentPage = "emailgeneration"})
=======
        "profilePage" -> ProfilePage(UserSession.userId ?: "DefaultUserId", { currentPage = "documentPage"}, { currentPage = "emailgeneration"}, { currentPage = "progressPage"})
        "emailgeneration" -> EmailGenerationPage({ currentPage = "documentPage"}, { currentPage = "profilePage"}, { currentPage = "progressPage"})
        "progressPage" -> JobProgressPage(UserSession.userId ?: "DefaultUserId", { currentPage = "documentPage"}, { currentPage = "profilePage"}, { currentPage = "emailgeneration"})
        "documentPage" -> DocumentPage({ currentPage = "emailgeneration"}, { currentPage = "profilePage"}, { currentPage = "progressPage"})
>>>>>>> snowmail/src/main/kotlin/Main.kt
    }
}


