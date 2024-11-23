package ca.uwaterloo.view



//import androidx.compose.material3.*
//import kotlinx.serialization.Serializable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ca.uwaterloo.controller.DocumentController
import ca.uwaterloo.controller.ProfileController
import ca.uwaterloo.model.EducationWithDegreeName
import ca.uwaterloo.model.WorkExperience
import ca.uwaterloo.service.ParserService
import controller.EmailGenerationController
import controller.SendEmailController
import integration.OpenAIClient
import integration.SupabaseClient
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import kotlinx.coroutines.runBlocking
import model.GeneratedEmail
import model.UserInput
import model.UserProfile
import service.EmailGenerationService
import java.io.File


@Composable
fun EmailGenerationPage(userId: String, NavigateToDocuments: () -> Unit, NavigateToProfile: () -> Unit,
                        NavigateToProgress: () -> Unit
) {
//    val client = HttpClient(CIO) {
//        install(ContentNegotiation) {
//            json()
//        }
//    }
    var emailContent by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var showDocumentDialog by remember { mutableStateOf(false) }
    var selectedDocument by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()


    val httpClient = HttpClient(CIO)
    val openAIClient = OpenAIClient(httpClient)

    // Create an instance of EmailGenerationService
    val parserService = ParserService(openAIClient)
    val emailGenerationService = EmailGenerationService(openAIClient, parserService)
    val emailGenerationController = EmailGenerationController(emailGenerationService)

    // user input values
    var companyInput by remember { mutableStateOf("") }
    var descriptionInput by remember { mutableStateOf("") }
    var recruiterNameInput by remember { mutableStateOf("") }
    var recruiterEmailInput by remember { mutableStateOf("") }
    var jobtitleInput by remember { mutableStateOf("") }

    //var currentPage by remember { mutableStateOf("ProfilePage") }

    var selectedTabIndex by remember { mutableStateOf(0) }


    var userInput = UserInput(
        jobDescription = descriptionInput,
        recruiterEmail = recruiterEmailInput,
        jobTitle = jobtitleInput,
        company = companyInput,
        recruiterName = recruiterNameInput,
        fileURLs = listOf(selectedDocument?:""),
    )
    val resumeFile = selectedDocument?.let { File(it) }
    val dbStorage = SupabaseClient()
    val profileController = ProfileController(dbStorage.userProfileRepository)
    //val userProfile = profileController.getUserProfile(UserSession.userId ?: "DefaultUserId")
    //val firstName = userProfile.firstName
    //val lastName = userProfile.lastName
    var gotName by remember { mutableStateOf("") }
    var gotSkills by remember { mutableStateOf(listOf<String>()) }
    var gotEducation by remember { mutableStateOf(listOf<EducationWithDegreeName>()) }
    var gotWorkExperience by remember { mutableStateOf(listOf<WorkExperience>()) }


    LaunchedEffect(Unit) {
        val getName = profileController.getUserName(userId)
        val getEducationResult = profileController.getEducation(userId)
        val getSkills = profileController.getSkills(userId)
        val getWorkExperienceResult = profileController.getWorkExperience(userId)

        getName.onSuccess { name ->

            gotName = name
        }.onFailure { error ->
            gotName = error.message ?: "Failed to retrieve user name"
        }

        getSkills.onSuccess { skills ->
            gotSkills = skills
        }.onFailure { error ->
            println("Error retrieving user skills: ${error.message}")
            gotSkills = emptyList()
        }

        getEducationResult.onSuccess { educationList ->
            gotEducation = educationList
        }.onFailure { error ->
            println("Error retrieving user education: ${error.message}")
            gotEducation = emptyList()
        }

        getWorkExperienceResult.onSuccess { workExperienceList ->
            gotWorkExperience = workExperienceList
        }.onFailure { error ->
            println("Error retrieving user work experience: ${error.message}")
            gotWorkExperience = emptyList() // 返回空列表或根据需要处理错误
        }
    }


    val userProfile = UserProfile(
        userId = userId,
        firstName = gotName,
        lastName = "",
        //skills = listOf("Java", "Kotlin", "SQL")
    )


//    val education = Education( //call getEducation
//        id = 12,
//        userId = "123",
//        degreeId = 3,
//        institutionName = "University of Waterloo",
//        major = "Computer Science",
//        gpa = 3.8f,
//        startDate = LocalDate(2019, 9, 1),
//        endDate = LocalDate(2023, 6, 1)
//    )





//    val workExperience = WorkExperience( //call getWorkExperience
//        userId = "123",
//        currentlyWorking = false,
//        startDate = LocalDate(2021, 5, 1),
//        endDate = LocalDate(2021, 8, 1),
//        companyName = "Example Corp",
//        title = "Software Engineer",
//        description = "Developed backend systems, deployed scalable solutions, and built efficient ETL pipelines for financial data processing."
//    )




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
            TopNavigationBar(
                selectedTabIndex = selectedTabIndex,
                onTabSelected = { index ->
                    selectedTabIndex = index
                    when (index) {
                        0 -> {}
                        1 -> navigateOtherPage(NavigateToProgress)
                        2 -> navigateOtherPage(NavigateToDocuments)
                        3 -> navigateOtherPage(NavigateToProfile)
                    }
                }
            )

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
//                        OutlinedButton(
//                            onClick = { showDocumentDialog = true },
//                            modifier = Modifier
//                                .clip(RoundedCornerShape(32.dp))
//                                .background(Color.White)
//                                .padding(32.dp)
//                                .fillMaxWidth(),
//                            colors = ButtonDefaults.outlinedButtonColors(
//                                backgroundColor = Color.Transparent
//                            )
//                        ) {
//                            Text("Select Documents", color = Color.Black, textAlign = TextAlign.Left)
//                        }
                        DocumentSelectionDropdown(
                            userId = userId,
                            selectedDocument = selectedDocument,
                            onDocumentSelected = { document ->
                                selectedDocument = document
                            }
                        )
                    }

                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(32.dp)) // Rounded corners
                            .background(Color.White) // White background
                            .padding(32.dp)
                    ) {

                        TextField(
                            value = descriptionInput,
                            onValueChange = { descriptionInput = it },
                            label = { Text("Job Description") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedLabelColor = Color.Transparent,
                                //unfocusedLabelColor = Color.Transparent,
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
                            value = companyInput,
                            onValueChange = { companyInput = it },
                            label = { Text("Company Name") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(32.dp)) // Rounded corners
                                .background(Color(0x6B727F80)),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedLabelColor = Color.Transparent,
                                //unfocusedLabelColor = Color.Transparent,
                                disabledTextColor = Color.Transparent,

                                //disabledIndicatorColor = Color.Transparent
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            )
                        )

                        Spacer(modifier = Modifier.weight(0.1f))

                        TextField(
                            value = jobtitleInput,
                            onValueChange = { jobtitleInput = it },
                            label = { Text("Job Title") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(32.dp)) // Rounded corners
                                .background(Color(0x6B727F80)),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedLabelColor = Color.Transparent,
                                //unfocusedLabelColor = Color.Transparent,
                                disabledTextColor = Color.Transparent,

                                //disabledIndicatorColor = Color.Transparent
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            )
                        )

                        Spacer(modifier = Modifier.weight(0.1f))

                        TextField(
                            value = recruiterNameInput,
                            onValueChange = { recruiterNameInput = it },
                            label = { Text("Recruiter Name") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(32.dp)) // Rounded corners
                                .background(Color(0x6B727F80)),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedLabelColor = Color.Transparent,
                                //unfocusedLabelColor = Color.Transparent,
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            )
                        )

                        Spacer(modifier = Modifier.weight(0.1f))

                        TextField(
                            value = recruiterEmailInput,
                            onValueChange = { recruiterEmailInput = it },
                            label = { Text("Recruiter Email") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(32.dp)) // Rounded corners
                                .background(Color(0x6B727F80)),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedLabelColor = Color.Transparent,
                                //unfocusedLabelColor = Color.Transparent,
                                disabledTextColor = Color.Transparent,

                                //disabledIndicatorColor = Color.Transparent
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            )
                        )


                    }
                        Spacer(modifier = Modifier.weight(0.1f))

                        Button(
                            onClick = {
                                runBlocking {
                                    try {
                                        val generatedEmail: GeneratedEmail? = emailGenerationController.generateEmail(
                                            informationSource = "profile",
                                            userInput = userInput,
                                            userProfile = userProfile,
                                            education = gotEducation,
                                            workExperience = gotWorkExperience,
                                            skills = gotSkills,
                                            resumeFile = resumeFile
                                        )
                                        println("Generated Email: ${generatedEmail?.body}")
                                        emailContent = generatedEmail?.body ?: "Failed to generate email"
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
                        userId = userId,
                        onDismissRequest = { showDialog = false },
                       // title = { Text("Generated Email") },
                        title = "Generated Email",
                        initialText = emailContent,
                        reciepientAddress = recruiterEmailInput,
                        jobTitle = jobtitleInput,
                        companyName = companyInput,
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

//                if (showDocumentDialog) {
//                    DocumentSelectionDialog(
//                        onDismissRequest = { showDocumentDialog = false },
//                        onDocumentSelected = { document ->
//                            // Handle document selection
//                            showDocumentDialog = false
//                        }
//                    )
//                }
            }
        }
    }
}

@Composable
fun EditableAlertDialog(
    userId: String,
    title: String,
    initialText: String,
    reciepientAddress: String,
    jobTitle: String,
    companyName: String,
    onDismissRequest: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf(initialText) }
    var recipientEmailAddy by remember { mutableStateOf(reciepientAddress) }
    var emailSubject by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val profileController = ProfileController(SupabaseClient().userProfileRepository)
    var senderEmail by remember { mutableStateOf("") }
    var senderPassword by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val emailResult = profileController.getUserLinkedGmailAccount(userId)
        emailResult.onSuccess { email ->
            senderEmail = email
        }.onFailure { error ->
            println("Error fetching linked Gmail account: ${error.message}")
        }

        val passwordResult = profileController.getUserGmailAppPassword(userId)
        passwordResult.onSuccess { password ->
            senderPassword = password
        }.onFailure { error ->
            println("Error fetching Gmail app password: ${error.message}")
        }
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        //title = { Text(title) },
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
                //onConfirm(text)
                /*
                send_email(
                    senderEmail = "cs346test@gmail.com", //call getEmail to get user's email
                    senderEmail =
                    password = "qirk dyef rvbv bkka",
                    recipient = recipientEmailAddy,
                    subject = emailSubject,
                    text = text,
                    fileURLs = listOf(),
                    fileNames = listOf()
                    jobApplicationRepository = JobApplicationRepository(supabase),
                    userID = UserSession.userId ?: "DefaultUserId",
                    jobTitle = jobTitle,
                    companyName = companyName
                )*/

                runBlocking {
                    try {
                        var emailsendingController = SendEmailController(SupabaseClient().jobApplicationRepository, SupabaseClient().documentRepository)
                        val returnMessage = emailsendingController.send_email(
                            recipient = recipientEmailAddy,
                            subject = emailSubject,
                            text = text,
                            // TO BE MODIFIED
                            buckets = listOf(),
                            documentsType = listOf<String>(),
                            documentsName = listOf<String>(),
                            // -----------
                            userID = userId,
                            jobTitle = jobTitle,
                            companyName = companyName
                        )
                        // if success
                        if (returnMessage == "Success") {
                            // show success message
                        } else if (returnMessage == "Missing Gmail Account or Password, please go to profile page and finish linking gmail account") {
                            // show error message
                        } else {
                            // show error message Failed to send the email. Please verify that your linked email address and password are correct.
                        }
                    } catch (e: Exception) {
                        println("Error sending email: ${e.message}")
                    }

                }


                onConfirm(text)
            },
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

@Composable
fun DocumentSelectionDropdown(
    userId: String,
    selectedDocument: String?,
    onDocumentSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val documentController = DocumentController(SupabaseClient().documentRepository)
    //var documents by remember { mutableStateOf(listOf<String>()) }
    var documentList by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(Unit) {
//        val documentResult = documentController.get()
//        documentResult.onSuccess { docList ->
//            documents = docList
//        }.onFailure { error ->
//            println("Error fetching documents: ${error.message}")
//        }
        val result = documentController.listDocuments("user_documents", userId, "Resume")
        result.onSuccess { documents ->
            documentList = documents
        }.onFailure { error ->
            println("Error listing documents: ${error.message}")
        }
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFF487B96),
                contentColor = MaterialTheme.colors.onPrimary
            )
        ) {
            Text(selectedDocument ?: "Select Document")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            documentList.forEach { document ->
                DropdownMenuItem(onClick = {
                    onDocumentSelected(document)
                    expanded = false
                }) {
                    Text(document)
                }
            }
        }
    }
}

//@Composable
//fun DocumentSelectionDialog(
//    onDismissRequest: () -> Unit,
//    onDocumentSelected: (String) -> Unit
//) {
//    AlertDialog(
//        onDismissRequest = onDismissRequest,
//        title = { Text("Select Document") },
//        text = {
//            Column {
//                Text("Document 1", modifier = Modifier.clickable { onDocumentSelected("Document 1") })
//                Text("Document 2", modifier = Modifier.clickable { onDocumentSelected("Document 2") })
//                Text("Document 3", modifier = Modifier.clickable { onDocumentSelected("Document 3") })
//            }
//        },
//        confirmButton = {
//            Button(onClick = onDismissRequest) {
//                Text("Close")
//            }
//        }
//    )
//}


fun main() {
    application {
        Window(onCloseRequest = ::exitApplication) {
            var currentPage by remember { mutableStateOf("") }
            EmailGenerationPage("a365a4c4-6427-4461-8cb4-2850fab8ac8c",{ currentPage = "profilePage"}, { currentPage = "profilePage"}, { currentPage = "profilePage"})
        }
    }
}