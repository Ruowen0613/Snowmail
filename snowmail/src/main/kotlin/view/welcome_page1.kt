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
import androidx.compose.ui.draw.clip
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .padding(50.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.End,

            modifier = Modifier
                .fillMaxWidth()
                //.align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 16.dp)
                //.border(BorderStroke(2.dp, Color.Gray)),
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
                .fillMaxWidth(),
                //.background(Color(176 / 255f, 212 / 255f, 213 / 255f, 0.2f))
                //.height(maxHeight * 0.7f)
                //.align(Alignment.Center)
            //.border(BorderStroke(2.dp, Color.Gray)),
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
                    .padding(end = 5.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .clickable(onClick = NavigateToWelcomePage2) // Makes the box clickable
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            color = Color(240 / 255f, 232 / 255f, 232 / 255f, 0.2f), // Background color
                        )
                        .height(220.dp)
                        .width(370.dp)
                        .border(BorderStroke(2.dp, Color.LightGray), RoundedCornerShape(16.dp)),
                        //.padding(16.dp), // Padding inside the box
                    horizontalAlignment = Alignment.CenterHorizontally,
//                    horizontalArrangement = Arrangement.Center,
//                    contentAlignment = Alignment.Center // Centers the text inside the box
                ) {
                    Image(
                        painter = painterResource("egss.png"),
                        contentDescription = null,
                        modifier = Modifier
                            .size(170.dp)
                    )
                        Text("Cold Email Generation", fontSize = 14.sp)
                        Text("Easily create professional and personalized emails in minutes", fontSize = 7.sp)
                    }

                    Spacer(modifier = Modifier.width(50.dp))

                Column(
                    modifier = Modifier
                        //.fillMaxWidth() // Makes the box take the full width
                        //.padding(16.dp) // Adds padding around the box
                        .clickable(onClick = NavigateToWelcomePage2) // Makes the box clickable
                        .clip(RoundedCornerShape(16.dp))
                        //.border(BorderStroke(2.dp, Color.LightGray))
                        .background(
                            color = Color(240 / 255f, 232 / 255f, 232 / 255f, 0.2f), // Background color
                        )
                        .height(220.dp)
                        .width(370.dp)
                        .border(BorderStroke(2.dp, Color.LightGray), RoundedCornerShape(16.dp)),
                        //.padding(16.dp), // Padding inside the box
                    horizontalAlignment = Alignment.CenterHorizontally,// Centers the text inside the box
                ) {
                    Image(
                        painter = painterResource("progss.png"),
                        contentDescription = null,
                        modifier = Modifier
                            .size(170.dp)
                    )
                    Text("Job Application Progress", fontSize = 14.sp)
                    Text("Track your job applications, follow-up emails, and responses", fontSize = 7.sp)
                }

            }
           Column(
               modifier = Modifier
                   //.fillMaxWidth() // Makes the box take the full width
                   //.padding(16.dp) // Adds padding around the box
                   .clickable(onClick = NavigateToWelcomePage2) // Makes the box clickable
                   .clip(RoundedCornerShape(16.dp))
                   .background(
                       color = Color(240 / 255f, 232 / 255f, 232 / 255f, 0.2f), // Background color
                   )
                   .height(220.dp)
                   .width(370.dp)
                   .border(BorderStroke(2.dp, Color.LightGray), RoundedCornerShape(16.dp)),
                   //.padding(16.dp), // Padding inside the box
               horizontalAlignment = Alignment.CenterHorizontally, // Centers the text inside the box
           ) {
               Image(
                   painter = painterResource("esendss.png"),
                   contentDescription = null,
                   modifier = Modifier
                       .size(170.dp)
               )
               Text("Send Cold Emails Directly", fontSize = 14.sp)
               Text("No need for external tools— manage your emails in one place.", fontSize = 7.sp)
           }

        }
    }
}