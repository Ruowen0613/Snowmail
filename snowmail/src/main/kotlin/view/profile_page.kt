package ca.uwaterloo.view

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.foundation.text.ClickableText

import kotlinx.datetime.LocalDate
import androidx.compose.material.icons.filled.Delete
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

import ca.uwaterloo.controller.ProfileController
import ca.uwaterloo.controller.SignInController

import integration.SupabaseClient
import ca.uwaterloo.model.EducationWithDegreeName
import ca.uwaterloo.model.WorkExperience
import ca.uwaterloo.model.PersonalProject

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items

import ca.uwaterloo.view.components.EducationSection
import ca.uwaterloo.view.components.ProjectSection
import ca.uwaterloo.view.components.WorkExperienceSection

import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.TextDecoration
import ca.uwaterloo.service.EmailValidatingService
import java.awt.Desktop
import java.net.URI

@Composable
fun ProfilePage(userId: String,
                NavigateToDocuments: () -> Unit, NavigateToEmialGen: () -> Unit,
                NavigateToProgress: () -> Unit,  NavigateToLogin: () -> Unit) {
    val dbStorage = SupabaseClient()
    val profileController = ProfileController(dbStorage.userProfileRepository)
    val signInController = SignInController(dbStorage.authRepository)


    var showEducationDialog by remember { mutableStateOf(false) }
    var showExperienceDialog by remember { mutableStateOf(false) }
    var showSkillsDialog by remember { mutableStateOf(false) }
    var EditContactDialog by remember { mutableStateOf(false) }


    var userName by remember { mutableStateOf("") }
    var educationList by remember { mutableStateOf<List<EducationWithDegreeName>>(emptyList()) }
    var errorMessage by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }
    var userLocation by remember { mutableStateOf("") }
    var userPhone by remember { mutableStateOf("") }
    var skills by remember { mutableStateOf(listOf<String>()) }
    var userLinkedIn by remember { mutableStateOf("") }
    var userGithub by remember { mutableStateOf("") }
    var userPersonalWebsite by remember { mutableStateOf("") }

    var showEditEducationDialog by remember { mutableStateOf(false) }
    var selectedEducation by remember { mutableStateOf<EducationWithDegreeName?>(null) }
    var showEditExperienceDialog by remember { mutableStateOf(false) }
    var selectedExperience by remember { mutableStateOf<WorkExperience?>(null) }
    var showEditPortfolioDialog by remember { mutableStateOf(false) }


    var workExperienceList by remember { mutableStateOf<List<WorkExperience>>(emptyList()) }

    // Project State Variables
    var projectList by remember { mutableStateOf<List<PersonalProject>>(emptyList()) }
    var showProjectDialog by remember { mutableStateOf(false) }
    var showEditProjectDialog by remember { mutableStateOf(false) }
    var selectedProject by remember { mutableStateOf<PersonalProject?>(null) }


    var currentPage by remember { mutableStateOf("ProfilePage") }

    var selectedTabIndex by remember { mutableStateOf(3) }



    fun refreshEducationList() {
        runBlocking {
            val educationResult = profileController.getEducation(userId)
            educationResult.onSuccess { educationRecords ->
                educationList = educationRecords
            }.onFailure { error ->
                errorMessage = error.message ?: "Failed to retrieve education records"
            }
        }
    }

    fun refreshWorkExperienceList() {
        runBlocking {
            val result = profileController.getWorkExperience(userId)
            result.onSuccess { experiences ->
                workExperienceList = experiences
            }
        }
    }

    fun refreshProjectList() {
        runBlocking {
            val result = profileController.getProjects(userId)
            result.onSuccess { projects ->
                projectList = projects
            }.onFailure { error ->
                errorMessage = error.message ?: "Failed to retrieve projects"
            }
        }
    }


    fun refreshContactInfo() {
        runBlocking {
            val locationResult = profileController.getUserCity(userId)
            val phoneResult = profileController.getUserPhone(userId)

            locationResult.onSuccess { city ->
                userLocation = city
            }.onFailure { error ->
                errorMessage = error.message ?: "Failed to retrieve updated location."
            }

            phoneResult.onSuccess { updatedPhone ->
                userPhone = updatedPhone
            }.onFailure { error ->
                errorMessage = error.message ?: "Failed to retrieve updated phone."
            }
        }
    }

    fun refreshPortfolioInfo() {
        runBlocking {
            val linkedInResult = profileController.getUserLinkedIn(userId)
            val githubResult = profileController.getUserGithub(userId)
            val websiteResult = profileController.getUserPersonalWebsite(userId)

            linkedInResult.onSuccess { linkedIn ->
                userLinkedIn = linkedIn
            }.onFailure { error ->
                errorMessage = error.message ?: "Failed to retrieve updated LinkedIn URL."
            }

            githubResult.onSuccess { github ->
                userGithub = github
            }.onFailure { error ->
                errorMessage = error.message ?: "Failed to retrieve updated GitHub URL."
            }

            websiteResult.onSuccess { website ->
                userPersonalWebsite = website
            }.onFailure { error ->
                errorMessage = error.message ?: "Failed to retrieve updated personal website."
            }
        }
    }

    fun refreshSkills() {
        runBlocking {
            val getSkillsResult = profileController.getSkills(userId)
            getSkillsResult.onSuccess { loadedSkills ->
                skills = loadedSkills
            }.onFailure { error ->
                errorMessage = error.message ?: "Failed to load skills"
            }
        }
    }

    LaunchedEffect(userId) {

        val getNameResult = profileController.getUserName(userId)
        val educationResult = profileController.getEducation(userId)
        val experienceResult = profileController.getWorkExperience(userId)
        val getEmailResult = profileController.getUserEmail(userId)
        val getLocationResult = profileController.getUserCity(userId)
        val getPhoneResult = profileController.getUserPhone(userId)
        val getSkillsResult = profileController.getSkills(userId)
        val getLinkedInResult = profileController.getUserLinkedIn(userId)
        val getGithubResult = profileController.getUserGithub(userId)
        val getWebsiteResult = profileController.getUserPersonalWebsite(userId)

        val projectResult = profileController.getProjects(userId)

        projectResult.onSuccess { projects ->
            projectList = projects
        }.onFailure { error ->
            errorMessage = error.message ?: "Failed to retrieve projects"
        }

        getNameResult.onSuccess { name ->

            userName = name
        }.onFailure { error ->

            errorMessage = error.message ?: "Failed to retrieve user name"
        }

        educationResult.onSuccess { educationRecords ->

            educationList = educationRecords
        }
            .onFailure { error ->

                errorMessage = error.message ?: "Failed to retrieve education records"
            }

        experienceResult.onSuccess { experiences ->
            workExperienceList = experiences
        }
            .onFailure { error -> errorMessage = error.message ?: "Failed to retrieve work experience records" }

        getEmailResult.onSuccess { email ->

            userEmail = email
        }.onFailure { error ->

            errorMessage = error.message ?: "Failed to retrieve user email"
        }

        getLocationResult.onSuccess { city ->
            userLocation = city
        }.onFailure { error ->
            errorMessage = error.message ?: "Failed to retrieve user location"
        }


        getPhoneResult.onSuccess { phone ->
            userPhone = phone
        }.onFailure { error ->
            errorMessage = error.message ?: "Failed to retrieve user phone"
        }

        getSkillsResult.onSuccess { fetchedSkills ->
            skills = fetchedSkills
        }.onFailure { error ->
            errorMessage = error.message ?: "Failed to load skills"
        }

        getLinkedInResult.onSuccess { linkedIn ->
            userLinkedIn = linkedIn
        }.onFailure { error ->
            errorMessage = error.message ?: "Failed to retrieve LinkedIn URL"
        }

        getGithubResult.onSuccess { github ->
            userGithub = github
        }.onFailure { error ->
            errorMessage = error.message ?: "Failed to retrieve GitHub URL"
        }

        getWebsiteResult.onSuccess { website ->
            userPersonalWebsite = website
        }.onFailure { error ->
            errorMessage = error.message ?: "Failed to retrieve personal website"
        }
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFFF8FAFC))
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        TopNavigationBar(
            selectedTabIndex = selectedTabIndex,
            onTabSelected = { index ->
                selectedTabIndex = index
                when (index) {
                    0 -> navigateOtherPage(NavigateToEmialGen)
                    1 -> navigateOtherPage(NavigateToProgress)
                    2 -> navigateOtherPage(NavigateToDocuments)
                    3 -> {}
                }
            }
        )

        Button(
            onClick = {
                NavigateToLogin()
                runBlocking {
                    signInController.logoutUser()
                }
            },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.White, // White background
                contentColor = Color(0xFF487896) // Text color
            ),
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 16.dp, end = 16.dp) // Add spacing from the edges
        ) {
            Text("Sign Out", fontWeight = FontWeight.Bold)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
//            // Profile Picture
//            Box(
//                modifier = Modifier
//                    .size(80.dp)
//                    .clip(CircleShape)
//                    .background(Color(0xFFE2E2E2)),
//            )
//
//            Spacer(modifier = Modifier.width(16.dp))


            // Name
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    modifier = Modifier.padding(10.dp)
                )
            } else {
                Text(
                    text = userName.ifEmpty { "Loading..." },
                    fontSize = 24.sp,
                    style = MaterialTheme.typography.h5,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(10.dp),
            elevation = 8.dp
        ) {

            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    var showGmailLinkingDialog by remember { mutableStateOf(false) }

                    Button(
                        onClick = { showGmailLinkingDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFF487896),
                            contentColor = Color.White
                        )
                    ) {
                        Text("gmail linking")
                    }

                    if (showGmailLinkingDialog) {
                        GmailLinkingDialog(
                            onDismissRequest = { showGmailLinkingDialog = false },
                            userId = userId,
                            profileController = profileController
                        )
                    }

                    Spacer(modifier = Modifier.weight(0.1f))

                    IconButton(
                        onClick = { EditContactDialog = true },
                        modifier = Modifier.size(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color(0xFF487896)
                        )
                    }
                }


                Spacer(modifier = Modifier.height(4.dp))

                SectionTitle("Contact Information")
                Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        elevation = 4.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                ProfileDetail(label = "Email Address:", value = userEmail)
                                ProfileDetail(label = "Location:", value = userLocation ?: "Location not available")
                                ProfileDetail(label = "Phone: +1 ", value = userPhone ?: "Phone not available")
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Spacer(modifier = Modifier.width(8.dp))

//                                Text(
//                                    text = "Resume.pdf",
//                                    modifier = Modifier
//                                        .padding(end = 32.dp)
//                                        .clickable {
//                                            // Add the action to open the resume file
//                                        },
//                                    style = MaterialTheme.typography.body1.copy(
//                                        fontSize = 14.sp,
//                                        color = Color(0xFF2669A0)
//                                    )
//                                )
                            }
                        }
                    }
                }

                if (EditContactDialog) {
                    EditContactDialog(
                        userId = userId,
                        profileController = profileController,
                        userLocation = userLocation,
                        userPhone = userPhone,
                        onDismiss = { EditContactDialog = false },
                        onContactUpdated = {
                            refreshContactInfo()
                            EditContactDialog = false
                        }
                    )
                }

                SectionTitle("Portfolio")
                Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        elevation = 4.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                ProfileDetail(label = "LinkedIn URL:", value = userLinkedIn ?: "Not available")
                                ProfileDetail(label = "GitHub URL:", value = userGithub ?: "Not available")
                                ProfileDetail(label = "Portfolio URL:", value = userPersonalWebsite ?: "Not available")
                            }

                            IconButton(onClick = { showEditPortfolioDialog = true }) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Portfolio & Socials",
                                    tint = Color.Gray
                                )
                            }
                        }
                    }
                }

                if (showEditPortfolioDialog) {
                    EditPortfolioDialog(
                        userId = userId,
                        profileController = profileController,
                        linkedInUrl = userLinkedIn,
                        githubUrl = userGithub,
                        portfolioUrl = userPersonalWebsite,
                        onDismiss = { showEditPortfolioDialog = false },
                        onLinksUpdated = {
                            refreshPortfolioInfo()
                            showEditPortfolioDialog = false
                        }
                    )
                }


                EducationSection(
                    userId = userId,
                    profileController = profileController,
                    educationList = educationList,
                    showEducationDialog = showEducationDialog,
                    showEditEducationDialog = showEditEducationDialog,
                    selectedEducation = selectedEducation,
                    onEducationAdded = { refreshEducationList() },
                    onEducationEdited = { refreshEducationList() },
                    onEducationDeleted = { refreshEducationList() },
                    onShowEducationDialogChange = { showEducationDialog = it },
                    onShowEditEducationDialogChange = { showEditEducationDialog = it },
                    onSelectedEducationChange = { selectedEducation = it }
                )

                WorkExperienceSection(
                    userId = userId,
                    profileController = profileController,
                    workExperienceList = workExperienceList,
                    showWorkExperienceDialog = showExperienceDialog,
                    showEditWorkExperienceDialog = showEditExperienceDialog,
                    selectedWorkExperience = selectedExperience,
                    onWorkExperienceAdded = { refreshWorkExperienceList() },
                    onWorkExperienceEdited = { refreshWorkExperienceList() },
                    onWorkExperienceDeleted = { refreshWorkExperienceList() },
                    onShowWorkExperienceDialogChange = { showExperienceDialog = it },
                    onShowEditWorkExperienceDialogChange = { showEditExperienceDialog = it },
                    onSelectedWorkExperienceChange = { selectedExperience = it }
                )

                ProjectSection(
                    userId = userId,
                    profileController = profileController,
                    projectList = projectList,
                    showProjectDialog = showProjectDialog,
                    showEditProjectDialog = showEditProjectDialog,
                    selectedProject = selectedProject,
                    onProjectAdded = { refreshProjectList() },
                    onProjectEdited = { refreshProjectList() },
                    onProjectDeleted = { refreshProjectList() },
                    onShowProjectDialogChange = { showProjectDialog = it },
                    onShowEditProjectDialogChange = { showEditProjectDialog = it },
                    onSelectedProjectChange = { selectedProject = it }
                )


                SectionTitle("Skills")

                Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    IconButton(
                        onClick = { showSkillsDialog = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Skill",
                            tint = Color(0xFF487896)
                        )
                    }

                    if (skills.isEmpty()) {
                        Text("No items added", fontSize = 14.sp, color = Color.Gray)
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            skills.forEach { skill ->
                                SkillChip(skill = skill, onDelete = {
                                    runBlocking {
                                        profileController.deleteSkill(userId, skill)
                                        refreshSkills()
                                    }
                                })
                            }
                        }
                    }

                    if (showSkillsDialog) {
                        EditSkillsDialog(
                            onDismiss = { showSkillsDialog = false },
                            onSave = {
                                refreshSkills()
                                showSkillsDialog = false
                            },
                            userId = userId,
                            profileController = profileController,
                            initialSkills = skills
                        )
                    }

                    if (errorMessage.isNotEmpty()) {
                        Text(text = errorMessage, color = Color.Red)
                    }
                }

            }
        }
    }
}

@Composable
fun EditContactDialog(
    userId: String,
    profileController: ProfileController,
    userLocation: String?,
    userPhone: String?,
    onDismiss: () -> Unit,
    onContactUpdated: () -> Unit
) {
    var location by remember { mutableStateOf(userLocation ?: "") }
    var phone by remember { mutableStateOf(userPhone ?: "") }
    var errorMessage by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Edit Contact",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") },
                    placeholder = { Text("Enter city") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("+1", modifier = Modifier.padding(end = 8.dp))
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            runBlocking {
                                val result = profileController.updateCityPhone(
                                    userId = userId,
                                    cityName = location,
                                    phone = phone
                                )

                                result.onSuccess {
                                    onContactUpdated()
                                    onDismiss()
                                }.onFailure { error ->
                                    errorMessage = error.message ?: "Failed to update contact information."
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFF487896),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}


@Composable
fun EditPortfolioDialog(
    userId: String,
    profileController: ProfileController,
    linkedInUrl: String?,
    githubUrl: String?,
    portfolioUrl: String?,
    onDismiss: () -> Unit,
    onLinksUpdated: () -> Unit
) {
    var linkedInInput by remember { mutableStateOf(linkedInUrl ?: "") }
    var githubInput by remember { mutableStateOf(githubUrl ?: "") }
    var portfolioInput by remember { mutableStateOf(portfolioUrl ?: "") }
    var errorMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Edit Portfolio",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                OutlinedTextField(
                    value = linkedInInput,
                    onValueChange = { linkedInInput = it },
                    label = { Text("LinkedIn URL") },
                    placeholder = { Text("https://www.linkedin.com/in/...") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = githubInput,
                    onValueChange = { githubInput = it },
                    label = { Text("GitHub URL") },
                    placeholder = { Text("https://github.com/...") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = portfolioInput,
                    onValueChange = { portfolioInput = it },
                    label = { Text("Portfolio URL") },
                    placeholder = { Text("https://...") },
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            scope.launch {
                                val result = profileController.updateUserLinks(
                                    userId = userId,
                                    linkedinUrl = linkedInInput.ifBlank { null },
                                    githubUrl = githubInput.ifBlank { null },
                                    personalWebsiteUrl = portfolioInput.ifBlank { null }
                                )
                                result.onSuccess {
                                    onLinksUpdated()
                                    onDismiss()
                                }.onFailure { error ->
                                    errorMessage = error.message ?: "Failed to update portfolio links."
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFF487896),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}


@Composable
fun SkillChip(skill: String, onDelete: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFE0E0E0),
        modifier = Modifier
            .padding(4.dp)
            .height(32.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Text(text = skill, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(4.dp))
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Delete Skill",
                    tint = Color.Red
                )
            }
        }
    }
}

@Composable
fun EditSkillsDialog(
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    userId: String,
    profileController: ProfileController,
    initialSkills: List<String>
) {
    var skillInput by remember { mutableStateOf("") }
    var isDuplicateSkill by remember { mutableStateOf(false) }
    var isInvalidSkill by remember { mutableStateOf(false) }
    var selectedSkills = remember { mutableStateListOf<String>().apply { addAll(initialSkills) } }
    var errorMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Edit Skills", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)

                Text(
                    text = "Click a skill to delete it from your list.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = skillInput,
                    onValueChange = {
                        skillInput = it
                        isDuplicateSkill = false
                        isInvalidSkill = false
                    },
                    label = { Text("Add a new skill") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = isDuplicateSkill || isInvalidSkill
                )

                if (isDuplicateSkill) {
                    Text(
                        text = "This skill has already been added.",
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                if (isInvalidSkill) {
                    Text(
                        text = "Invalid skill. Please enter valid text.",
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Button(
                    onClick = {
                        if (skillInput.isBlank()) {
                            isInvalidSkill = true
                        } else if (skillInput in selectedSkills) {
                            isDuplicateSkill = true
                        } else {
                            selectedSkills.add(skillInput.trim())
                            skillInput = ""
                            isDuplicateSkill = false
                            isInvalidSkill = false
                        }
                    },
                    modifier = Modifier.align(Alignment.End),
                    enabled = skillInput.isNotBlank()
                ) {
                    Text("Add")
                }

                // Display skills in a wrapped layout using LazyVerticalGrid
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 100.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp) // Limit height for scrollable grid
                ) {
                    items(selectedSkills) { skill ->
                        SkillChip(skill = skill) {
                            selectedSkills.remove(skill)
                        }
                    }
                }

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            scope.launch {
                                try {
                                    selectedSkills.forEach { skill ->
                                        if (skill !in initialSkills) {
                                            profileController.addSkill(userId, skill)
                                        }
                                    }

                                    initialSkills.forEach { skill ->
                                        if (skill !in selectedSkills) {
                                            profileController.deleteSkill(userId, skill)
                                        }
                                    }

                                    onSave()
                                } catch (e: Exception) {
                                    errorMessage = e.message ?: "Failed to save skills."
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFF487896),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}



fun navigateOtherPage(NavigateOtherPage: () -> Unit) {
    NavigateOtherPage()
}


@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .padding(top = 16.dp, bottom = 8.dp),
        textAlign = TextAlign.Start
    )
}

@Composable
fun ProfileDetail(label: String, value: String) {
    Row {
        Text(
            "$label ",
            fontWeight = FontWeight.Bold,
            style = TextStyle(fontSize = 14.sp, color = Color.Black)
        )
        Text(
            value,
            style = TextStyle(fontSize = 14.sp, color = Color.Black)
        )
    }
}

@Composable
fun GmailLinkingDialog(
    onDismissRequest: () -> Unit,
    userId: String,
    profileController: ProfileController
) {
    var account by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(userId) {
        isLoading = true
        val accountResult = profileController.getUserLinkedGmailAccount(userId)
        val passwordResult = profileController.getUserGmailAppPassword(userId)

        accountResult.onSuccess { result ->
            account = result
        }.onFailure { error ->
            errorMessage = error.message ?: "Failed to retrieve linked Gmail account."
        }

        passwordResult.onSuccess { result ->
            password = result
        }.onFailure { error ->
            errorMessage = error.message ?: "Failed to retrieve App Password."
        }

        isLoading = false
    }

    if (successMessage) {
        Dialog(onDismissRequest = { successMessage = false }) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.White,
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Gmail linked successfully!",
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            successMessage = false
                            onDismissRequest()
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFF487896),
                            contentColor = Color.White
                        )
                    ) {
                        Text("OK")
                    }
                }
            }
        }
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.White,
            modifier = Modifier.padding(16.dp)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF487896))
                }
            } else {
                Column(
                    modifier = Modifier.padding(16.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Gmail Linking",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val annotatedString = buildAnnotatedString {
                        append("To use Snowmail to send emails through your Gmail account, you need to grant Snowmail access to your Google account and Gmail services. \n")
                        append("Please follow the steps below to securely link your Gmail account to Snowmail:\n\n")
                        append("1. Open Google Account Settings --> Manage Your Google Account\n")
                        append("2. Go to the \"Security\" section.\n3. Scroll down to the \"How you sign in to Google\" section.\n")
                        append("4. Enable 2-Step Verification if it’s not already turned on.\n5. Return to the \"Security\" section.\n")
                        append("6. Search for ")
                        pushStringAnnotation(
                            tag = "URL",
                            annotation = "https://myaccount.google.com/apppasswords?continue=https://myaccount.google.com/security?utm_source%3Dchrome-settings&pli=1&rapt=AEjHL4O7uSuEpGMELA6bQTszK_VubA2-GRY3rBunsnzdDciaH3BN__4TE6hCe1MGty9OrzcIv9Xn6Znzj1vOj63EGq8fi46UtvBZw6BQ32N0WHermYS-x9Q"
                        )
                        withStyle(style = SpanStyle(color = Color.Blue, textDecoration = TextDecoration.Underline)) {
                            append("App passwords")
                        }
                        append(" in the topmost search bar and hit Enter\n")

                        append("7. Follow these steps to create an app-specific password:\n")
                        append("   - Type \"Snowmail\" in the App name input box.\n")
                        append("   - Click Create to create an app password.\n")
                        append("   - Copy the app password provided by Google (e.g., abcd-efgh-ijkl-mnop).\n\n")
                        append("Enter your Gmail account and the app password below to securely connect your Gmail account.")
                    }

                    ClickableText(
                        text = annotatedString,
                        style = TextStyle(fontSize = 14.sp, color = Color.Gray),
                        onClick = { offset ->
                            annotatedString.getStringAnnotations(tag = "URL", start = offset, end = offset)
                                .firstOrNull()?.let { annotation ->
                                    val uri = URI(annotation.item)
                                    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                                        Desktop.getDesktop().browse(uri)
                                    }
                                } else {
                                    errorMessage = "Invalid email or App Password. Please try again."
                                }
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = account,
                            onValueChange = { account = it },
                            label = { Text("Gmail Account") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("App Password") },
                            modifier = Modifier.weight(1f),
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                if (account.isBlank() || !account.endsWith("@gmail.com")) {
                                    errorMessage = "Please enter a valid Gmail account ending with @gmail.com."
                                    return@Button
                                }

                                if (password.isBlank()) {
                                    errorMessage = "App Password cannot be empty."
                                    return@Button
                                }

                                val emailValidatingService = EmailValidatingService()
                                coroutineScope.launch {
                                    val isValid = emailValidatingService.verifyEmail(account, password)
                                    if (isValid) {
                                        errorMessage = ""
                                        successMessage = true

                                        val accountResult = profileController.editUserLinkedGmailAccount(userId, account)
                                        val passwordResult = profileController.editUserGmailAppPassword(userId, password)
                                        if (accountResult.isSuccess && passwordResult.isSuccess) {
                                            successMessage = true
                                        } else {
                                            errorMessage = "Failed to link Gmail account. Please try again."
                                        }
                                    } else {
                                        errorMessage = "Invalid email or App Password. Please try again."
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color(0xFF487896),
                                contentColor = Color.White
                            ),
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            Text("Link Gmail")
                        }

                        Button(
                            onClick = onDismissRequest,
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color(0xFF487896),
                                contentColor = Color.White
                            ),
                            modifier = Modifier.align(Alignment.BottomEnd)
                        ) {
                            Text("Close")
                        }
                    }
                }
            }
        }
    }
}

