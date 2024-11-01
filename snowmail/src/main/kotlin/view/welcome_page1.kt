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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale

@Composable
fun WebsitePageWelcome1() {
    var currentPage by remember { mutableStateOf("welcome") }

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

@Composable
fun WelcomePage1(NavigateToSignup: () -> Unit, NavigateToLogin: () -> Unit, NavigateToWelcomePage2: () -> Unit, NavigateToWelcomePage3: () -> Unit, NavigateToWelcomePage4: () -> Unit) {
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
        }


        Column(
            modifier = Modifier
                .fillMaxWidth()
                //.background(Color(176 / 255f, 212 / 255f, 213 / 255f, 0.2f))
                .height(maxHeight * 0.7f)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Why Choose Snowmail?",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.h4,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(15.dp))
            Row (
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .padding(end = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        //.fillMaxWidth() // Makes the box take the full width
                        .padding(16.dp) // Adds padding around the box
                        .clickable(onClick = NavigateToWelcomePage2) // Makes the box clickable
                        .background(
                            color = Color.LightGray, // Background color
                            shape = RoundedCornerShape(16.dp) // Rounded corners
                        )
                        .padding(16.dp), // Padding inside the box
                    contentAlignment = Alignment.Center // Centers the text inside the box
                ) {
                        Text("Cold Email Generation")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        //.fillMaxWidth() // Makes the box take the full width
                        .padding(16.dp) // Adds padding around the box
                        .clickable(onClick = NavigateToWelcomePage3) // Makes the box clickable
                        .background(
                            color = Color.LightGray, // Background color
                            shape = RoundedCornerShape(16.dp) // Rounded corners
                        )
                        .padding(16.dp), // Padding inside the box
                    contentAlignment = Alignment.Center // Centers the text inside the box
                ) {
                    Text("Job Application Progress")
                }

            }
           Box(
               modifier = Modifier
                   //.fillMaxWidth() // Makes the box take the full width
                   .padding(16.dp) // Adds padding around the box
                   .clickable(onClick = NavigateToWelcomePage4) // Makes the box clickable
                   .background(
                       color = Color.LightGray, // Background color
                       shape = RoundedCornerShape(16.dp) // Rounded corners
                   )
                   .padding(16.dp), // Padding inside the box
               contentAlignment = Alignment.Center // Centers the text inside the box
           ) {
               Text("Send Cold Emails Directly")
           }

        }
    }
}