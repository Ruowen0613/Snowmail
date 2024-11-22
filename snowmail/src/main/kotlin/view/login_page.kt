package ca.uwaterloo.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ca.uwaterloo.controller.SignInController
// import ca.uwaterloo.persistence.DBStorage
import kotlinx.coroutines.runBlocking
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import ca.uwaterloo.view.theme.AppTheme

import integration.SupabaseClient

object UserSession {
    var userId: String? = null
}

@Composable
fun loginPage(NavigateToSignup: () -> Unit, NavigateToHome: () -> Unit) {
    AppTheme {
        Box (
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
        )  {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row { Spacer(modifier = Modifier.padding(60.dp)) }
                Row {
                    Text(
                        "Job Hunting's ",
                        textAlign = TextAlign.Center,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Text(
                        "Tough",
                        textAlign = TextAlign.Center,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xff2b5dc7),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
                Row {
                    Text(
                        "But You Don't Have To Do It ",
                        textAlign = TextAlign.Center,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        "Alone!",
                        textAlign = TextAlign.Center,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xff2b5dc7)
                    )
                }

                Row { Spacer(modifier = Modifier.padding(20.dp)) }
                Row(Modifier.fillMaxWidth()) { loginForm(NavigateToSignup, NavigateToHome) }
            }


        }
    }}


@Composable
fun loginForm(NavigateToSignup: () -> Unit, NavigateToHome: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row { loginWithAccount(NavigateToSignup, NavigateToHome) }
        Row(Modifier.fillMaxHeight(0.03f)) { Divider() }
        // HorizontalDivider(thickness = 2.dp)
        Row { loginWithGmail() }
    }
}


@Composable
fun loginWithAccount(NavigateToSignup: () -> Unit, NavigateToHome: () -> Unit) {
    val dbStorage = SupabaseClient()
    val signInController = SignInController(dbStorage.authRepository)
    var passwordVisible by remember { mutableStateOf(false) }
    Column (
        modifier = Modifier.fillMaxWidth().padding(horizontal = 400.dp).padding(vertical = 15.dp)
    ) {

        // Email Adress
        Row { Text("Email Address") }
        Row(modifier = Modifier.fillMaxHeight(0.03f)) { Box{} }
        var email by remember { mutableStateOf("") }
        Row {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        // Passowrd
        Row(modifier = Modifier.fillMaxHeight(0.07f)) { Box{} }
        Row { Text("Password") }
        Row(modifier = Modifier.fillMaxHeight(0.03f)) { Box{} }
        var password by remember { mutableStateOf("") }
        Row {
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    TextButton(onClick = { passwordVisible = !passwordVisible }) {
                        Text(
                            text = if (passwordVisible) "Hide" else "Show",
                            color = Color.Gray // Set text color to grey
                        )
                    }
                }
            )
        }

        Row(modifier = Modifier.fillMaxHeight(0.07f)) { Box{} }
        // potential error message shown
        var errorMessage by remember { mutableStateOf("") }
        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = Color.Red, textAlign = TextAlign.Center, modifier = Modifier.padding(10.dp))
        }

        // sign in button

//        val SUCCESS = false
//        val EMAILDOESNOTEXIST = false
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Button(
                onClick = {
                    runBlocking {
                        val signInResult = signInController.signInUser(email, password)

                        signInResult.onSuccess { userId ->
                            // If sign-in succeeds, navigate to home page
                            UserSession.userId = userId
                            NavigateToHome()
                        }.onFailure { error ->
                            // Show error message if sign-in fails
                            errorMessage = error.message ?: "Login failed. Please try again."
                            email = ""
                            password = ""
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(buttonColor))
            ) {
                Text("Sign In", color = Color.White)
            }
        }

        Row(modifier = Modifier.fillMaxHeight(0.07f)) { Box{} }
        // forgot your password?
        ClickableText(AnnotatedString("Forgot Your Password?"), onClick = {})



        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Don't have an account?")
            // navigate to login page
            TextButton(onClick = { navigateLoginPage(NavigateToSignup) }) {
                Text("Sign up", color = Color(buttonColor))
            }
        }
    }
}



@Composable
fun loginWithGmail() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally) {
        //Text("Or log in with")
        // val iconfile = File("gmail_icon.png")

        // val iconimage = ImageIO.read(iconfile)
        // val iconbitmap = iconimage.toComposeImageBitmap()
//        Button(onClick = { LoginWithGmail() }, colors = ButtonDefaults.buttonColors(backgroundColor = Color(formColor))) {
//            Row (horizontalArrangement = Arrangement.Center){
//                // Image(iconbitmap, "gmail")
//                // Spacer(modifier = Modifier.width(8.dp))
//                Text("Gmail", color = Color.Black)
//            }
//
//        }
    }
}



fun LoginWithGmail() {

}


// --- ONLY FOR TESTING THIS SINGLE PAGE, TO BE DELETED LATER

@Composable
fun WebsitePage2() {
    var currentPage by remember { mutableStateOf("login") }

    when (currentPage) {
        "login" -> loginPage ({ currentPage = "signup" }, {currentPage = "profilePage"})
        "signup" -> SignUpPage ({ currentPage = "login"}, { currentPage = "login"})
    }
}


fun main() {
    application {
        Window(onCloseRequest = ::exitApplication) {
            WebsitePage2()
        }
    }
}
