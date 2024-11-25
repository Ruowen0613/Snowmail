package ca.uwaterloo.view



//import androidx.compose.material3.*
//import kotlinx.serialization.Serializable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import ca.uwaterloo.view.components.DocumentSelectionDropdownButton
import ca.uwaterloo.view.components.EmailGenInputField
import ca.uwaterloo.view.components.EmailGenerationButton
//import ca.uwaterloo.view.components.FetchUserProfileData
import ca.uwaterloo.view.dialogs.GeneratedEmailAlertDialog
import ca.uwaterloo.view.theme.AppTheme
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
    var selectedTabIndex by remember { mutableStateOf(0) }
    var emailContent by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var selectedDocument by remember { mutableStateOf<String?>(null) }
  //  val coroutineScope = rememberCoroutineScope()
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
    var userInput = UserInput(
        jobDescription = descriptionInput,
        recruiterEmail = recruiterEmailInput,
        jobTitle = jobtitleInput,
        company = companyInput,
        recruiterName = recruiterNameInput,
        fileURLs = listOf(selectedDocument?:""),
    )

    val resumeFile = selectedDocument?.let { File(it) }

//======================================================================================================
    val dbStorage = SupabaseClient()
    val profileController = ProfileController(dbStorage.userProfileRepository)
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
    //val (userProfile, userInput, emailGenerationController) = EmailGenVariables(userId)
    //val userProfile = FetchUserProfileData(userId)
//======================================================================================================
    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
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
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth(0.65f)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            EmailGenInputField(
                                value = companyInput,
                                onValueChange = { companyInput = it },
                                label = "Company Name"
                            )
                            Spacer(modifier = Modifier.width(20.dp))
                            EmailGenInputField(
                                value = jobtitleInput,
                                onValueChange = { jobtitleInput = it },
                                label = "Job Title"
                            )

                        }
                        Spacer(modifier = Modifier.height(25.dp))
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            EmailGenInputField(
                                value = recruiterNameInput,
                                onValueChange = { recruiterNameInput = it },
                                label = "Recruiter Name"
                            )
                            Spacer(modifier = Modifier.width(20.dp))
                            EmailGenInputField(
                                value = recruiterEmailInput,
                                onValueChange = { recruiterEmailInput = it },
                                label = "Recruiter Email"
                            )
                        }
                        Spacer(modifier = Modifier.height(15.dp))
                        Row(
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            DocumentSelectionDropdownButton(
                                userId = userId,
                                selectedDocument = selectedDocument,
                                onDocumentSelected = { document ->
                                    selectedDocument = document
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(15.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
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
                            .height(100.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedLabelColor = Color.Transparent,
                            //unfocusedLabelColor = Color.Transparent,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                }
                Spacer(modifier = Modifier.height(64.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    EmailGenerationButton(
                        emailGenerationController = emailGenerationController,
                        userInput = userInput,
                        userProfile = userProfile,
                        gotEducation = gotEducation,
                        gotWorkExperience = gotWorkExperience,
                        gotSkills = gotSkills,
                        resumeFile = resumeFile,
                        onEmailGenerated = { emailContent = it },
                        onShowDialog = { showDialog = it },
                        infoSource = "profile",
                        enabled = true
                    )

                    EmailGenerationButton(
                        emailGenerationController = emailGenerationController,
                        userInput = userInput,
                        userProfile = userProfile,
                        gotEducation = gotEducation,
                        gotWorkExperience = gotWorkExperience,
                        gotSkills = gotSkills,
                        resumeFile = resumeFile,
                        onEmailGenerated = { emailContent = it },
                        onShowDialog = { showDialog = it },
                        infoSource = "resume",
                        enabled = selectedDocument != null
                    )


                    if (showDialog) {
                        GeneratedEmailAlertDialog(
                            userId = userId,
                            onDismissRequest = { showDialog = false },
                            title = "Generated Email",
                            initialText = emailContent,
                            reciepientAddress = recruiterEmailInput,
                            jobTitle = jobtitleInput,
                            companyName = companyInput,
                            onConfirm = { newText ->
                                emailContent = newText
                                showDialog = false
                            }
                        )
                    }
                }
            }
        }
    }
}



fun main() {
    application {
        Window(onCloseRequest = ::exitApplication) {
            var currentPage by remember { mutableStateOf("") }
            EmailGenerationPage("a365a4c4-6427-4461-8cb4-2850fab8ac8c",{ currentPage = "profilePage"}, { currentPage = "profilePage"}, { currentPage = "profilePage"})
        }
    }
}
