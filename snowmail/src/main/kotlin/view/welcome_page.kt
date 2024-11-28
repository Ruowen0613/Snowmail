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
import ca.uwaterloo.service.ParserService
import ca.uwaterloo.view.components.EmailGenerationButton
import ca.uwaterloo.view.theme.AppTheme
import model.GeneratedEmail
import controller.EmailGenerationController
import integration.OpenAIClient
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import model.UserInput
import model.UserProfile
import service.EmailGenerationService
import kotlinx.coroutines.runBlocking

@Composable
fun WebsitePageWelcome() {
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
fun WelcomePage(NavigateToSignup: () -> Unit, NavigateToLogin: () -> Unit, NavigateToWelcomePage1: () -> Unit) {
    AppTheme {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background)
                    .padding(50.dp)
                    //.border(BorderStroke(2.dp, Color.Gray)), // Add border here
            ) {
                Column (
                    modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.5f),
                    //.border(BorderStroke(2.dp, Color.Gray)), // Add border here
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Find your dream job",
                        fontWeight = FontWeight.Bold,
                        fontSize = 50.sp,
                        color = Color(30 / 255f, 30 / 255f, 30 / 255f, 0.65f)
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "with our help",
                        fontWeight = FontWeight.Bold,
                        //style = MaterialTheme.typography.h4,
                        //textAlign = TextAlign.Center,
                        fontSize = 50.sp,
                        color = Color(30 / 255f, 30 / 255f, 30 / 255f, 0.65f)
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "No longer spend hours writing emails to recruiters, instead spend that time on your personal development",
                        style = MaterialTheme.typography.body1,
                        color = Color(30 / 255f, 30 / 255f, 30 / 255f, 0.65f)
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Button(
                        onClick = NavigateToWelcomePage1,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(161 / 255f, 212 / 255f, 234 / 255f, 1f),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Learn More")
                    }
                }
                Column (
                    modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    //.border(BorderStroke(2.dp, Color.Gray)), // Add border here
                ) {
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, end = 16.dp)
                    ) {
                        Spacer(modifier = Modifier.width(80.dp))
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
                    Column (
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            //.height(400.dp)
                            .fillMaxHeight()
                            .fillMaxWidth()
                    ) {
                        val httpClient = HttpClient(CIO)
                        val openAIClient = OpenAIClient(httpClient)
                        val parserService = ParserService(openAIClient)
                        val emailGenerationService = EmailGenerationService(openAIClient, parserService)
                        val emailGenerationController = EmailGenerationController(emailGenerationService)
                        var emailtext by remember { mutableStateOf("Email") }


                        var userInput = UserInput(
                            jobDescription = "",
                            recruiterEmail = "",
                            jobTitle = "",
                            company = "",
                            recruiterName = "recruiting agent",
                            fileURLs = listOf(""),
                        )

                        val userProfile = UserProfile(
                            userId = "0",
                            firstName = "",
                            lastName = "",
                            //skills = listOf("Java", "Kotlin", "SQL")
                        )

                        TextField(
                            value = emailtext,
                            onValueChange = {   emailtext = it },
                            label = { Text("Test it out!") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .padding(16.dp),
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = Color.White,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            )

                                //.border(BorderStroke(2.dp, Color.LightGray))
                        )

                        Button (
                            onClick = {
                                runBlocking {
                                    val generatedEmail: GeneratedEmail? = emailGenerationController.generateEmail(
                                        informationSource = "profile",
                                        userInput = userInput,
                                        userProfile = userProfile,
                                        education = emptyList(),
                                        workExperience = emptyList(),
                                        skills = emptyList(),
                                        resumeFile = null
                                    )
                                    emailtext = generatedEmail?.body ?: "Failed to generate email"
                                }

                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text("Generate Email")
                        }
                    }
                }
            }
        //}
    }
}

//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .background(Color(176 / 255f, 212 / 255f, 213 / 255f, 0.2f))
//                    //.height(0.7f)
//                    .align(Alignment.Center),
//                horizontalArrangement = Arrangement.Center,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Column(
//                    modifier = Modifier
//                        .fillMaxHeight()
//                        .weight(1f)
//                        .padding(end = 16.dp),
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    verticalArrangement = Arrangement.Center
//                ) {
//
//                    Spacer(modifier = Modifier.height(40.dp))
//
//                    Row(
//
//                    ) {
//
//
//                        Spacer(modifier = Modifier.width(8.dp))
//
//                        Button(
//                            onClick = { },
//                            modifier = Modifier
//                                .alpha(0.5f),
//                            colors = ButtonDefaults.buttonColors(
//                                backgroundColor = Color(0xFF487896),
//                                contentColor = Color.White
//                            )
//                        ) {
//                            Text("Learn more")
//                        }
//                    }
//                }
//                Column(
//                    modifier = Modifier
//                        .fillMaxHeight()
//                        .weight(1f)
//                        .padding(end = 16.dp),
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    verticalArrangement = Arrangement.Center
//                ) {
////                val imagefile = File("gmail_icon.png")
////                val image = ImageIO.read(imagefile)
////                val imagebitmap = image.toComposeImageBitmap()
////                Image(
////                    bitmap = imagebitmap,
////                    contentDescription = "Welcome Image",
////                    modifier = Modifier.size(150.dp) // Set the size of the image
////                )
//                }
//            }
//        }
//    }
//}