package ca.uwaterloo.view

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
import ca.uwaterloo.persistence.DBStorage
import kotlinx.coroutines.runBlocking

@Composable
fun loginPage(NavigateToSignup: () -> Unit, NavigateToHome: () -> Unit) {
    Column (
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,) {
        Row {
            Text(
                "Job Hunting: ",
                textAlign = TextAlign.Center,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp),
                color = Color.Black
            )
            Text(
                "Tough",
                textAlign = TextAlign.Center,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp),
                color = Color(0xff2b5dc7)
            )
        }
        Row {
            Text(
                "But you Do Not ",
                textAlign = TextAlign.Center,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp),
                color = Color.Black
            )
            Text(
                "Have",
                textAlign = TextAlign.Center,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp),
                color = Color(0xff2b5dc7)
            )
        }
        Row {
            Text(
                "to Do It ",
                textAlign = TextAlign.Center,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp),
                color = Color.Black
            )
            Text(
                "Alone!",
                textAlign = TextAlign.Center,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp),
                color = Color(0xff2b5dc7)
            )
        }


        Row(Modifier.fillMaxWidth()) { loginForm(NavigateToSignup, NavigateToHome) }
    }


}


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
    val dbStorage = DBStorage()
    val signInController = SignInController(dbStorage)
    Column (
        modifier = Modifier.fillMaxWidth().padding(horizontal = 160.dp).padding(vertical = 15.dp)){

        // email
        Row { Text("Email Address") }
        Row(modifier = Modifier.fillMaxHeight(0.03f)) { Box{} }
        var email by remember { mutableStateOf("") }
        Row { OutlinedTextField(value = email, onValueChange = { email = it }, modifier = Modifier.fillMaxWidth(), singleLine = true)}

        // password
        Row(modifier = Modifier.fillMaxHeight(0.07f)) { Box{} }
        Row { Text("Password") }
        Row(modifier = Modifier.fillMaxHeight(0.03f)) { Box{} }
        var password by remember { mutableStateOf("") }
        Row { OutlinedTextField(value = password, onValueChange =  {password = it}, modifier = Modifier.fillMaxWidth(), singleLine = true) }




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
                            NavigateToHome()
                        }.onFailure { error ->
                            // Show error message if sign-in fails
                            errorMessage = error.message ?: "Login failed. Please try again."
                            email = ""
                            password = ""
                        }
                    }
                    // if successful, navigate to home page
//                    if (SUCCESS) {
//                        NavigateToHome()
//                    }
//                    // if email doesn't exist, show and clear
//                    else if (EMAILDOESNOTEXIST) {
//                        errorMessage = "Email does not exist, please register first"
//                        email = ""
//                        password = ""
//                    }
//                    // if password and email does not match
//                    else {
//                        errorMessage = "Email and password do not match, please try again"
//                        email = ""
//                        password = ""
//                    }
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
        Text("Or log in with")
        // val iconfile = File("gmail_icon.png")

        // val iconimage = ImageIO.read(iconfile)
        // val iconbitmap = iconimage.toComposeImageBitmap()
        Button(onClick = { LoginWithGmail() }, colors = ButtonDefaults.buttonColors(backgroundColor = Color(formColor))) {
            Row (horizontalArrangement = Arrangement.Center){
                // Image(iconbitmap, "gmail")
                // Spacer(modifier = Modifier.width(8.dp))
                Text("Gmail", color = Color.Black)
            }

        }
    }
}



fun LoginWithGmail() {

}


// --- ONLY FOR TESTING THIS SINGLE PAGE, TO BE DELETED LATER

@Composable
fun WebsitePage2() {
    var currentPage by remember { mutableStateOf("login") }

    when (currentPage) {
        "login" -> loginPage ({ currentPage = "signup" }, {currentPage = "homepage"})
        "signup" -> SignUpPage ({ currentPage = "login"}, { currentPage = "home"})
        "homepage" -> homePage()
    }
}


fun main() {
    application {
        Window(onCloseRequest = ::exitApplication) {
            WebsitePage2()
        }
    }
}
