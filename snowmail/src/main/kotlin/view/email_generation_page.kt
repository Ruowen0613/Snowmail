package ca.uwaterloo.view



import androidx.compose.foundation.layout.*
import androidx.compose.material.*
//import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import model.UserInput
import model.UserProfile
//import kotlinx.serialization.Serializable

import androidx.compose.ui.window.application
import androidx.compose.ui.window.Window
import service.EmailGenerationService
import integration.OpenAIClient
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.border
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

import ca.uwaterloo.controller.ProfileController
import ca.uwaterloo.model.Education
import ca.uwaterloo.model.WorkExperience
import controller.send_email
import integration.SupabaseClient
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate


@Composable
fun EmailGenerationPage(NavigateToDocuments: () -> Unit, NavigateToProfile: () -> Unit,
                        NavigateToProgress: () -> Unit
) {
//    val client = HttpClient(CIO) {
//        install(ContentNegotiation) {
//            json()
//        }
//    }
    var emailContent by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()


    val httpClient = HttpClient(CIO)
    val openAIClient = OpenAIClient(httpClient)

    // Create an instance of EmailGenerationService
    val emailGenerationService = EmailGenerationService(openAIClient)

    // user input values
    var companyInput by remember { mutableStateOf("company name") }
    var descriptionInput by remember { mutableStateOf("description") }
    var recruiterNameInput by remember { mutableStateOf("recruiter name") }

    //var currentPage by remember { mutableStateOf("ProfilePage") }

    var selectedTabIndex by remember { mutableStateOf(3) }


    var userInput = UserInput(
        jobDescription = descriptionInput,
        recruiterEmail = "recruiter@example.com",
        jobTitle = "Software Engineer",
        company = "Example Corp",
        recruiterName = "Jane Doe",
        fileURLs = listOf("https://example.com/resume.pdf"),
    )
    val dbStorage = SupabaseClient()
    val profileController = ProfileController(dbStorage.userProfileRepository)
    //val userProfile = profileController.getUserProfile(UserSession.userId ?: "DefaultUserId")
    //val firstName = userProfile.firstName
    //val lastName = userProfile.lastName
    var gotName = ""
    runBlocking {
        val getName = profileController.getUserName(UserSession.userId ?: "DefaultUserId")
        getName.onSuccess { name ->

            gotName = name
        }.onFailure { error ->
            gotName = error.message ?: "Failed to retrieve user name"
        }
    }

    val userProfile = UserProfile(
        userId = UserSession.userId ?: "DefaultUserId",
        firstName = gotName,
        lastName = "",
        skills = listOf("Java", "Kotlin", "SQL")
    )

    val education = Education(
        id = 12,
        userId = "123",
        degreeId = 3,
        institutionName = "University of Waterloo",
        major = "Computer Science",
        gpa = 3.8f,
        startDate = LocalDate(2019, 9, 1),
        endDate = LocalDate(2023, 6, 1)
    )

    val workExperience = WorkExperience(
        userId = "123",
        currentlyWorking = false,
        startDate = LocalDate(2021, 5, 1),
        endDate = LocalDate(2021, 8, 1),
        companyName = "Example Corp",
        title = "Software Engineer",
        description = "Developed backend systems, deployed scalable solutions, and built efficient ETL pipelines for financial data processing."
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(Color(0xFFF8FAFC))
        ) {
            TopAppBar(
                backgroundColor = Color.White,
                elevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Tabs on the left
                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        backgroundColor = Color.White, // Set background color to white
                        contentColor = Color.Black,
                        indicator = { },
                        modifier = Modifier.weight(1f) // Take up remaining space
                    ) {
                        Tab(
                            selected = selectedTabIndex == 0,
                            onClick = { },
                            text = {
                                Text(
                                    "Cold Email Generation",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        )
                        Tab(
                            selected = selectedTabIndex == 1,
                            onClick = { navigateOtherPage(NavigateToProgress) },
                            text = { Text("Job Application Progress") }
                        )
                        Tab(
                            selected = selectedTabIndex == 2,
                            onClick = { navigateOtherPage(NavigateToDocuments) },
                            text = { Text("Documents") }
                        )
                        Tab(
                            selected = selectedTabIndex == 3,
                            onClick = { navigateOtherPage(NavigateToProfile) },
                            text = { Text("Profile") }
                        )
                    }

                    // Profile Image on the right
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE2E2E2))
                            .border(1.dp, Color.LightGray, CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(64.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) { // this is the row for user input forms and content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(32.dp)) // Rounded corners
                            .background(Color.White) // White background
                            .padding(32.dp)
                    ) {
                        TextField(
                            value = companyInput,
                            onValueChange = { companyInput = it },
                            label = { Text("Company Name") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedLabelColor = Color.Transparent,
                                unfocusedLabelColor = Color.Transparent,
                                disabledTextColor = Color.Transparent,

                                //disabledIndicatorColor = Color.Transparent
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            )
                        )
                    }

                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(32.dp)) // Rounded corners
                            .background(Color.White) // White background
                            .padding(32.dp)
                    ) {

                        OutlinedButton(
                            onClick = { },
                            modifier = Modifier
                                .clip(RoundedCornerShape(32.dp)) // Rounded corners
                                .background(Color.White) // White background
                                .padding(32.dp)
                                .fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(
                                backgroundColor = Color.Transparent
                            )
                        ) {
                            Text("Select Documents", color = Color.Black, textAlign = TextAlign.Left)
                        }

                        TextField(
                            value = descriptionInput,
                            onValueChange = { descriptionInput = it },
                            label = { Text("Job Description") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedLabelColor = Color.Transparent,
                                unfocusedLabelColor = Color.Transparent,
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            )
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Spacer(modifier = Modifier.weight(0.1f))

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(32.dp)) // Rounded corners
                            .background(Color.White) // White background
                            .padding(32.dp)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        TextField(
                            value = recruiterNameInput,
                            onValueChange = { recruiterNameInput = it },
                            label = { Text("Recruiter Name") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedLabelColor = Color.Transparent,
                                unfocusedLabelColor = Color.Transparent,
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            )
                        )
                    }
                        Spacer(modifier = Modifier.weight(0.1f))

                        Button(
                            onClick = {
                                coroutineScope.launch(Dispatchers.IO) {
                                    try {
                                        val generatedEmail =
                                            emailGenerationService.generateEmail(userInput, userProfile, listOf(education), listOf(workExperience))
                                        println("Generated Email: ${generatedEmail.body}")
                                        emailContent = generatedEmail.body ?: "Failed to generate email"
                                        showDialog = true
                                    } catch (e: Exception) {
                                        println("Error: ${e.message}")
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color(0xFF487B96),
                                contentColor = MaterialTheme.colors.onPrimary
                            )
                        ) {
                            Text("Generate Email")
                        }

                }
                if (showDialog) {
                    EditableAlertDialog(
                        onDismissRequest = { showDialog = false },
                       // title = { Text("Generated Email") },
                        title = "Generated Email",
                        initialText = emailContent,
                        onConfirm = { newText ->
                            emailContent = newText
                            showDialog = false
                        }
                        //text = { Text(emailContent) },
//                        confirmButton = {
//                            Button(onClick = { showDialog = false }) {
//                                Text("Close")
//                            }
//                        }
                    )
                }
            }
        }
    }
}

@Composable
fun EditableAlertDialog(
    title: String,
    initialText: String,
    onDismissRequest: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf(initialText) }
    var recipientEmailAddy by remember { mutableStateOf("") }
    var emailSubject by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(title) },
        text = {
            Spacer(modifier = Modifier.height(8.dp))
            Column {
                OutlinedTextField(
                    value = recipientEmailAddy,
                    onValueChange = { recipientEmailAddy = it },
                    label = { Text("Recipient Email") },
                    modifier = Modifier
                        .clip(RoundedCornerShape(32.dp)) // Rounded corners
                        .background(Color.White) // White background
                        .padding(32.dp)
                        .fillMaxWidth()
                        .background(Color.Transparent),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        //focusedLabelColor = Color.Transparent,
                        //unfocusedLabelColor = Color.Transparent,
                        //focusedBorderColor = Color.Transparent,
                        //unfocusedBorderColor = Color.Transparent
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = emailSubject,
                    onValueChange = { emailSubject = it },
                    label = { Text("Subject") },
                    modifier = Modifier
                        .clip(RoundedCornerShape(32.dp)) // Rounded corners
                        .background(Color.White) // White background
                        .padding(32.dp)
                        .fillMaxWidth()
                        .background(Color.Transparent),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        //focusedLabelColor = Color.Transparent,
                        //unfocusedLabelColor = Color.Transparent,
                        //focusedBorderColor = Color.Transparent,
                        //unfocusedBorderColor = Color.Transparent
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Transparent),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedLabelColor = Color.Transparent,
                        unfocusedLabelColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(text)
                send_email(
                    senderEmail = "cs346test@gmail.com",
                    password = "qirk dyef rvbv bkka",
                    recipient = recipientEmailAddy,
                    subject = emailSubject,
                    text = text,
                    fileURLs = listOf(),
                    fileNames = listOf()
                )},
                    colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF487B96),
                contentColor = MaterialTheme.colors.onPrimary
            )) {
                Text("Send")
            }
        },
        dismissButton = {
            Button(onClick = onDismissRequest,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF487B96),
                    contentColor = MaterialTheme.colors.onPrimary
                )) {
                Text("Cancel")
            }
        }
    )
}

fun main() {
    application {
        Window(onCloseRequest = ::exitApplication) {
            var currentPage by remember { mutableStateOf("") }
            EmailGenerationPage({ currentPage = "profilePage"}, { currentPage = "profilePage"}, { currentPage = "profilePage"})
        }
    }
}