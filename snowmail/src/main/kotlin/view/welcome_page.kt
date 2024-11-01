package ca.uwaterloo.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.io.File
import javax.imageio.ImageIO
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale

@Composable
fun WebsitePageWelcome() {
    var currentPage by remember { mutableStateOf("welcome") }

    when (currentPage) {
        "login" -> loginPage ({ currentPage = "signup" }, {currentPage = "profilePage"})
        "signup" -> SignUpPage ({ currentPage = "login"}, { currentPage = "login"})
        "profilePage" -> ProfilePage()
    }
}


fun main() {
    application {
        Window(onCloseRequest = ::exitApplication) {
            WebsitePage2()
        }
    }
}

@Composable
fun WelcomePage(NavigateToSignup: () -> Unit, NavigateToLogin: () -> Unit, NavigateToWelcomePage1: () -> Unit, NavigateToEmailGenerationPage: () -> Unit) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 16.dp)
        ) {
            Button(
                onClick = NavigateToLogin,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.White,
                    contentColor = Color(0xFF12A1C0)
                )
            ) {
                Text("Log in")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = NavigateToSignup,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF487896),
                    contentColor = Color.White
                )
            ) {
                Text("Sign Up")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = NavigateToEmailGenerationPage) {
                Text("Email Generation")
            }

        }


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(176 / 255f, 212 / 255f, 213 / 255f, 0.2f))
                .height(maxHeight * 0.7f)
                .align(Alignment.Center),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column (
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .padding(end = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Snowmail helps you get hired!",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.h4,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(40.dp))

                Row (

                ) {
                    Button(
                        onClick = NavigateToWelcomePage1,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFF487896),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Get started")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { },
                        modifier = Modifier
                            .alpha(0.5f),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFF487896),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Learn more")
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .padding(end = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
//                val imagefile = File("gmail_icon.png")
//                val image = ImageIO.read(imagefile)
//                val imagebitmap = image.toComposeImageBitmap()
//                Image(
//                    bitmap = imagebitmap,
//                    contentDescription = "Welcome Image",
//                    modifier = Modifier.size(150.dp) // Set the size of the image
//                )
            }
        }
    }
}