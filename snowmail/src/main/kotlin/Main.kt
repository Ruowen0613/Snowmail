package ca.uwaterloo

import androidx.compose.runtime.*
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import ca.uwaterloo.view.*

fun main() {
    application {
        Window(onCloseRequest = ::exitApplication, state = WindowState(size = DpSize(1200.dp, 800.dp))) {
            WebsitePage()
        }
    }
}


@Composable
fun WebsitePage() {

    var currentPage by remember { mutableStateOf("welcome") }

    when (currentPage) {
        "login" -> loginPage ({ currentPage = "signup" }, {currentPage = "homepage"})
        "signup" -> SignUpPage ({ currentPage = "login"}, { currentPage = "homepage"})
        "welcome" -> WelcomePage ({ currentPage = "signup"}, {currentPage = "login"}, {currentPage = "welcome1"})
        "welcome1" -> WelcomePage1 ({ currentPage = "signup"}, {currentPage = "login"}, {currentPage = "welcome2"}, {currentPage = "welcome3"}, {currentPage = "welcome4"})
        "welcome2" -> WelcomePage2 ({ currentPage = "signup"}, {currentPage = "login"}, {currentPage = "welcome3"})
        "welcome3" -> WelcomePage3 ({ currentPage = "signup"}, {currentPage = "login"}, {currentPage = "welcome4"})
        "welcome4" -> WelcomePage4 ({ currentPage = "signup"}, {currentPage = "login"})
        "homepage" -> homePage()
    }
}


