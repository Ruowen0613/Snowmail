package ca.uwaterloo.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ca.uwaterloo.controller.SignUpController


// import ca.uwaterloo.persistence.DBStorage

import integration.SupabaseClient


val fullPageColor = 0xFFebecf0
// val formColor = 0xFFFFFFFF
val formColor = fullPageColor
val buttonColor = 0xFF487896


@Composable
fun SignUpPage(NavigateToLogin: () -> Unit, NavigateToHome: () -> Unit) {
    Box(Modifier.fillMaxSize().background(Color(fullPageColor)), contentAlignment = Alignment.Center) {
        Column (horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
            Row { Spacer(modifier = Modifier.fillMaxHeight(0.05f)) }
            Row {
                Box(modifier = Modifier.fillMaxHeight(0.15f).background(Color(fullPageColor))) {
                    Row {
                        Text(
                            text = "Join ",
                            fontSize = 50.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontFamily = FontFamily.Default
                        )
                        Text(
                            text = "Snowmail!",
                            fontSize = 50.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xff2b5dc7),
                            fontFamily = FontFamily.Default
                        )
                    }
                }
            }

            Row { Spacer(modifier = Modifier.fillMaxHeight(0.01f)) }
            Row(Modifier.fillMaxHeight(0.93f)) { RegisterForm(NavigateToLogin, NavigateToHome) }
            Row { Spacer(modifier = Modifier.fillMaxHeight(0.01f)) }
        }

    }


}





@Composable
fun RegisterForm(NavigateToLogin: () -> Unit, NavigateToHome: () -> Unit) {
    val dbStorage = SupabaseClient()
    val signInController = SignUpController(dbStorage.authRepository)
    Box (Modifier.fillMaxWidth(0.7f).fillMaxHeight().background(Color(formColor))) {
        Row {
            Column(Modifier.fillMaxWidth(0.1f)) { Box {} }
            Column(horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth(0.8f).fillMaxWidth()) {

                Row(modifier = Modifier.fillMaxHeight(0.03f)) {}
                Row(modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.06f)) {
                    Column(Modifier.fillMaxWidth(0.53f)) { Text("First Name", textAlign = TextAlign.Start) }
                    Column { Text(text = "Last Name", textAlign = TextAlign.End) }
                }

                var firstName by remember { mutableStateOf("") }
                var lastName by remember { mutableStateOf("") }
                var email by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }
                var passwordConfirm by remember { mutableStateOf("") }

                Row(Modifier.fillMaxWidth()) {
                    // first name input
                    Column(Modifier.fillMaxWidth(0.48f)) {
                        OutlinedTextField(
                            value = firstName,
                            onValueChange = { firstName = it },
                            singleLine = true
                        )
                    }
                    Column(Modifier.fillMaxWidth(0.04f)) { Box{} }
                    // Spacer(modifier = Modifier.fillMaxWidth(0.04f))
                    // last name input
                    Column(Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = lastName,
                            onValueChange = { lastName = it },
                            singleLine = true
                        )
                    }
                }

                Row(modifier = Modifier.fillMaxHeight(0.03f)) {}
                Row { Text("Email Address") }
                // email address input
                Row (Modifier.fillMaxWidth()){ OutlinedTextField(value = email, onValueChange = { email = it }, singleLine = true, modifier = Modifier.fillMaxWidth()) }

                Row(modifier = Modifier.fillMaxHeight(0.03f)) {}
                // password input
                Row { Text("Password") }
                Row { OutlinedTextField(value = password, onValueChange = { password = it }, singleLine = true, modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation()
                ) }

                Row(modifier = Modifier.fillMaxHeight(0.03f)) {}
                Row { Text("Confirm Password") }
                Row {
                    OutlinedTextField(
                        value = passwordConfirm,
                        onValueChange = { passwordConfirm = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation()
                    )
                }

                val errorInformation = true
                var errorMessage by remember { mutableStateOf("") }

                Row(modifier = Modifier.fillMaxHeight(0.03f)) {}
                Row {
                    // register button
                    Button(onClick = {
                        // if first name is missing
                        if (firstName.isEmpty())  errorMessage = "please fill in first name"

                        // if last name is missing
                        else if (lastName.isEmpty())  errorMessage = "please fill in last name"

                        // if email is missing
                        else if (email.isEmpty())  errorMessage = "please fill in email"

                        // if password is missing
                        else if (password.isEmpty())  errorMessage = "please fill in password"

                        // if password is too short
                        else if (password.length < 6) errorMessage = "Password is too short"

                        // if password is not confirmed
                        else if (password != passwordConfirm) errorMessage = "Passwords do not match"

                        else {
                            val result = signInController.signUpUser(email, password, firstName, lastName)
                            result.onSuccess {
                                NavigateToHome()
                            }.onFailure { error ->
                                errorMessage = error.message ?: "Unknown error"
                            }
                        }


                    },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(buttonColor))) {
                        Text("Register", color = Color.White)
                    }
                }


                // potential error message shown

                if (errorMessage.isNotEmpty()) {
                    Text(text = errorMessage, color = Color.Red, textAlign = TextAlign.Center, modifier = Modifier.padding(10.dp))
                }

                Row(modifier = Modifier.fillMaxHeight(0.03f)) {}
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Already have an account?")
                    // navigate to login page
                    TextButton(onClick = { navigateLoginPage(NavigateToLogin) }) {
                        Text("Sign in", color = Color(buttonColor))
                    }
                }

                Row(modifier = Modifier.fillMaxHeight(0.03f)) {}
                Row { Divider() }

                Row(modifier = Modifier.fillMaxHeight(0.03f)) {}
                Row (horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()){ Text(text = "Or register with") }


                // gmail register
                // val iconfile = File("gmail_icon.png")
                // val iconimage = ImageIO.read(iconfile)
                // val iconbitmap = iconimage.toComposeImageBitmap()

                Row(modifier = Modifier.fillMaxHeight(0.03f)) { Box{} }
                Row (horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()){
                    Button(onClick = { GmailRegister() }, colors = ButtonDefaults.buttonColors(backgroundColor = Color(buttonColor))) {
                        Row (verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                            // Image(iconbitmap, "gmail")

                            Text("Register by Gmail", color = Color.White)
                        }
                    }
                }

            }
            Column(Modifier.fillMaxWidth(0.3f)) { Box {} }
        }
    }
}







fun navigateLoginPage(NavigateToLogin: () -> Unit) {
    NavigateToLogin()
}

fun GmailRegister() { }





// ---- ONLY FOR TESTING THIS SINGLE PAGE, TO BE DELETED LATER --------
@Composable
fun WebsitePage() {
    var currentPage by remember { mutableStateOf("signup") }

    when (currentPage) {
        "login" -> loginPage ({ currentPage = "signup" }, {currentPage = "login"})
        "signup" -> SignUpPage ({ currentPage = "login"}, { currentPage = "login"})
    }
}




fun main() {
    application {
        Window(onCloseRequest = ::exitApplication) {
            WebsitePage()
        }
    }
}
