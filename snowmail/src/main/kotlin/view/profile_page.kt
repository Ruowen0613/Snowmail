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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.foundation.text.ClickableText
import kotlinx.datetime.LocalDate
import androidx.compose.material.icons.filled.Delete
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

import ca.uwaterloo.controller.ProfileController

import integration.SupabaseClient
import ca.uwaterloo.model.Education
import ca.uwaterloo.model.WorkExperience
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.launch


@Composable
fun ProfilePage(userId: String,
                NavigateToDocuments: () -> Unit, NavigateToEmialGen: () -> Unit,
                NavigateToProgress: () -> Unit) {
    val dbStorage = SupabaseClient()
    val profileController = ProfileController(dbStorage.userProfileRepository)


    var showEducationDialog by remember { mutableStateOf(false) }
    var showExperienceDialog by remember { mutableStateOf(false) }
    var showSkillsDialog by remember { mutableStateOf(false) }
    var EditContactDialog by remember { mutableStateOf(false) }


    var userName by remember { mutableStateOf("") }
    var educationList by remember { mutableStateOf<List<Education>>(emptyList()) }
    var errorMessage by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }
    var userLocation by remember { mutableStateOf("") }
    var userPhone by remember { mutableStateOf("") }
    var skills by remember { mutableStateOf(listOf<String>()) }
    var userLinkedIn by remember { mutableStateOf("") }
    var userGithub by remember { mutableStateOf("") }
    var userPersonalWebsite by remember { mutableStateOf("") }

    var showEditEducationDialog by remember { mutableStateOf(false) }
    var selectedEducation by remember { mutableStateOf<Education?>(null) }
    var showEditExperienceDialog by remember { mutableStateOf(false) }
    var selectedExperience by remember { mutableStateOf<WorkExperience?>(null) }
    var showEditPortfolioDialog by remember { mutableStateOf(false) }


    var workExperienceList by remember { mutableStateOf<List<WorkExperience>>(emptyList()) }

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


        Row(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Picture
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE2E2E2)),
            )

            Spacer(modifier = Modifier.width(16.dp))

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

                                Text(
                                    text = "Resume.pdf",
                                    modifier = Modifier
                                        .padding(end = 32.dp)
                                        .clickable {
                                            // Add the action to open the resume file
                                        },
                                    style = MaterialTheme.typography.body1.copy(
                                        fontSize = 14.sp,
                                        color = Color(0xFF2669A0)
                                    )
                                )
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


                SectionTitle("Education")
                Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    if (educationList.isEmpty()) {
                        Text(
                            text = "No education records added",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(8.dp)
                        )
                    } else {
                        Column(modifier = Modifier.padding(8.dp)) {
                            educationList.forEach { education ->
                                // Clickable Box for each education record
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedEducation = education
                                            showEditEducationDialog = true
                                        }
                                        .padding(vertical = 8.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = "${education.institutionName}, ${education.degreeId} in ${education.major}",
                                            fontSize = 14.sp,
                                            color = Color.Black
                                        )
                                        Text(
                                            text = "GPA: ${education.gpa ?: "N/A"}",
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                        Text(
                                            text = "From ${education.startDate} to ${education.endDate}",
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(
                            onClick = {
                                selectedEducation = null
                                showEducationDialog = true
                            },
                            modifier = Modifier.size(15.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add",
                                tint = Color(0xFF487896)
                            )
                        }
                    }

                    if (showEducationDialog) {
                        AddEducationDialog(
                            onDismiss = { showEducationDialog = false },
                            userId = userId,
                            profileController = profileController,
                            onEducationAdded = { refreshEducationList() }
                        )
                    }

                    if (showEditEducationDialog) {
                        EditEducationDialog(
                            onDismiss = { showEditEducationDialog = false },
                            userId = userId,
                            profileController = profileController,
                            education = selectedEducation,
                            onEducationEdited = {
                                refreshEducationList()
                                selectedEducation = null
                                showEducationDialog = false
                            },
                            onEducationDeleted = {
                                refreshEducationList()
                                selectedEducation = null
                                showEditEducationDialog = false
                            }
                        )
                    }
                }



                SectionTitle("Work Experience")

                Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    if (workExperienceList.isEmpty()) {
                        Text(
                            text = "No experiences added",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(8.dp)
                        )
                    } else {
                        Column(modifier = Modifier.padding(8.dp)) {
                            workExperienceList.forEach { experience ->

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedExperience = experience
                                            showEditExperienceDialog = true
                                        }
                                        .padding(vertical = 8.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = "${experience.companyName} - ${experience.title}",
                                            fontSize = 14.sp,
                                            color = Color.Black
                                        )
                                        Text(
                                            text = "From ${experience.startDate} to ${experience.endDate ?: "Present"}",
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                        if (!experience.description.isNullOrEmpty()) {
                                            Text(
                                                text = experience.description,
                                                fontSize = 12.sp,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(
                            onClick = {
                                selectedExperience = null
                                showExperienceDialog = true
                            },
                            modifier = Modifier.size(15.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add",
                                tint = Color(0xFF487896)
                            )
                        }
                    }

                    if (showExperienceDialog) {
                        AddExperienceDialog(
                            onDismiss = { showExperienceDialog = false },
                            userId = userId,
                            profileController = profileController,
                            onWorkExperienceAdded = { refreshWorkExperienceList() }
                        )
                    }

                    // Show Edit Experience Dialog
                    if (showEditExperienceDialog) {
                        EditExperienceDialog(
                            onDismiss = { showEditExperienceDialog = false },
                            userId = userId,
                            profileController = profileController,
                            experience = selectedExperience,
                            onWorkExperienceEdited = {
                                refreshWorkExperienceList()
                                selectedExperience = null
                                showEditExperienceDialog = false
                            },
                            onWorkExperienceDeleted = {
                                refreshWorkExperienceList()
                                selectedExperience = null
                                showEditExperienceDialog = false
                            }
                        )
                    }
                }

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
fun AddEducationDialog(
    onDismiss: () -> Unit,
    userId: String,
    profileController: ProfileController,
    onEducationAdded: () -> Unit
) {

    var schoolName by remember { mutableStateOf("") }
    var major by remember { mutableStateOf("") }
    var degreeType by remember { mutableStateOf("") }
    var gpa by remember { mutableStateOf("") }
    var startMonth by remember { mutableStateOf("") }
    var startYear by remember { mutableStateOf("") }
    var endMonth by remember { mutableStateOf("") }
    var endYear by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    // Dropdown for degree type
    var expanded by remember { mutableStateOf(false) }
    val degreeTypes = listOf("Associate's/College Diploma", "Bachelor's", "Doctorate", "High School Diploma/GED", "Master's", "Other")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Text("Add Education", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)

                Spacer(modifier = Modifier.height(8.dp))


                OutlinedTextField(value = schoolName, onValueChange = { schoolName = it }, label = { Text("School Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = major, onValueChange = { major = it }, label = { Text("Major") }, modifier = Modifier.fillMaxWidth())


                Box(
                    modifier = Modifier.fillMaxWidth().wrapContentSize(Alignment.TopStart)
                ) {
                    OutlinedTextField(
                        value = degreeType,
                        onValueChange = {},
                        label = { Text("Degree Type") },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Filled.ArrowDropDown,
                                contentDescription = "Select Degree Type",
                                modifier = Modifier.clickable { expanded = true }
                            )
                        },
                        modifier = Modifier.fillMaxWidth().clickable { expanded = true },
                        readOnly = true
                    )
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        degreeTypes.forEach { type ->
                            DropdownMenuItem(onClick = {
                                degreeType = type
                                expanded = false
                            }) {
                                Text(type)
                            }
                        }
                    }
                }


                OutlinedTextField(value = gpa, onValueChange = { gpa = it }, label = { Text("GPA") }, modifier = Modifier.fillMaxWidth())

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = startMonth, onValueChange = { startMonth = it }, label = { Text("Start Month") }, modifier = Modifier.weight(1f), placeholder = { Text("(ex: 7 for July)") })
                    OutlinedTextField(value = startYear, onValueChange = { startYear = it }, label = { Text("Start Year") }, modifier = Modifier.weight(1f))
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = endMonth, onValueChange = { endMonth = it }, label = { Text("End Month") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = endYear, onValueChange = { endYear = it }, label = { Text("End Year") }, modifier = Modifier.weight(1f))
                }

                if (errorMessage.isNotEmpty()) {
                    Text(text = errorMessage, color = Color.Red, textAlign = TextAlign.Center, modifier = Modifier.padding(10.dp))
                }


                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Button(onClick = {

                        runBlocking {
                            val startDate = LocalDate(startYear.toInt(), startMonth.toInt(), 1)
                            val endDate = LocalDate(endYear.toInt(), endMonth.toInt(), 1)
                            val gpaValue = gpa.toFloatOrNull()
                            val degreeId = degreeTypes.indexOf(degreeType) + 1

                            val result = profileController.addEducation(
                                userId = userId,
                                degreeId = degreeId,
                                major = major,
                                gpa = gpaValue,
                                startDate = startDate,
                                endDate = endDate,
                                institutionName = schoolName
                            )

                            result.onSuccess {

                                onEducationAdded()
                                onDismiss()
                            }.onFailure { error ->

                                errorMessage = error.message ?: "Failed to add education record."
                            }
                        }
                    }, colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF487896), contentColor = Color.White)) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

@Composable
fun EditEducationDialog(
    education: Education? = null,
    onDismiss: () -> Unit,
    userId: String,
    profileController: ProfileController,
    onEducationEdited: () -> Unit,
    onEducationDeleted: () -> Unit
) {

    var schoolName by remember { mutableStateOf(education?.institutionName ?: "") }
    var major by remember { mutableStateOf(education?.major ?: "") }
    var degreeType by remember { mutableStateOf(education?.degreeId ?: "") }
    var gpa by remember { mutableStateOf(education?.gpa?.toString() ?: "") }
    //var startMonth by remember { mutableStateOf(education?.startDate?.month.toString() ?: "") }
    var startMonth by remember { mutableStateOf(education?.startDate?.month?.value?.toString() ?: "") }
    var startYear by remember { mutableStateOf(education?.startDate?.year.toString() ?: "") }
    //var endMonth by remember { mutableStateOf(education?.endDate?.month.toString() ?: "") }
    var endMonth by remember { mutableStateOf(education?.endDate?.month?.value?.toString() ?: "") }
    var endYear by remember { mutableStateOf(education?.endDate?.year.toString() ?: "") }
    var errorMessage by remember { mutableStateOf("") }


    var expanded by remember { mutableStateOf(false) }
    val degreeTypes = listOf("Associate's/College Diploma", "Bachelor's", "Doctorate", "High School Diploma/GED", "Master's", "Other")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (education != null) {
                        IconButton(
                            onClick = {
                                runBlocking {

                                    val result = education.id?.let { id ->
                                        profileController.deleteEducation(id.toString())
                                    } ?: Result.failure(Exception("Education ID is null"))

                                    result.onSuccess {
                                        onEducationDeleted()
                                        onDismiss()
                                    }.onFailure { error ->
                                        errorMessage = error.message ?: "Failed to delete education record."
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Education",
                                tint = Color(0xFFBE0303)
                            )
                        }
                    }
                }

                Text("Edit Education", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)


                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = schoolName,
                    onValueChange = { schoolName = it },
                    label = { Text("School Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = major,
                    onValueChange = { major = it },
                    label = { Text("Major") },
                    modifier = Modifier.fillMaxWidth()
                )


                OutlinedTextField(
                    value = gpa,
                    onValueChange = { gpa = it },
                    label = { Text("GPA") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = startMonth,
                        onValueChange = { startMonth = it },
                        label = { Text("Start Month") },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("(ex: 7 for July)") }
                    )
                    OutlinedTextField(
                        value = startYear,
                        onValueChange = { startYear = it },
                        label = { Text("Start Year") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = endMonth,
                        onValueChange = { endMonth = it },
                        label = { Text("End Month") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = endYear,
                        onValueChange = { endYear = it },
                        label = { Text("End Year") },
                        modifier = Modifier.weight(1f)
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
                                val startDate = LocalDate(startYear.toInt(), startMonth.toInt(), 1)
                                val endDate = LocalDate(endYear.toInt(), endMonth.toInt(), 1)
                                val gpaValue = gpa.toFloatOrNull()
                                val degreeId = degreeTypes.indexOf(degreeType) + 1

                                val result = if (education == null) {
                                    // Adding new education record
                                    profileController.addEducation(
                                        userId = userId,
                                        degreeId = degreeId,
                                        major = major,
                                        gpa = gpaValue,
                                        startDate = startDate,
                                        endDate = endDate,
                                        institutionName = schoolName
                                    )
                                } else {
                                    profileController.updateEducation(
                                        userId = userId,
                                        educationId = education.id.toString(),
                                        degreeId = 1,
                                        major = major,
                                        gpa = gpaValue,
                                        startDate = startDate,
                                        endDate = endDate,
                                        institutionName = schoolName
                                    )
                               }
                                result.onSuccess {
                                    onEducationEdited()
                                    onDismiss()
                                }.onFailure { error ->
                                    errorMessage = error.message ?: "Failed to save education record."
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
fun AddExperienceDialog(
    onDismiss: () -> Unit,
    userId: String,
    profileController: ProfileController,
    onWorkExperienceAdded: () -> Unit
) {

    var company by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var positionTitle by remember { mutableStateOf("") }
    var experienceType by remember { mutableStateOf("") }
    var startMonth by remember { mutableStateOf("") }
    var startYear by remember { mutableStateOf("") }
    var endMonth by remember { mutableStateOf("") }
    var endYear by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isCurrentlyWorking by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }


    var experienceTypeExpanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Add Work Experience", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.height(8.dp))


                OutlinedTextField(value = company, onValueChange = { company = it }, label = { Text("Company") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = positionTitle, onValueChange = { positionTitle = it }, label = { Text("Position Title") }, modifier = Modifier.fillMaxWidth())



                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = startMonth, onValueChange = { startMonth = it }, label = { Text("Start Month") }, modifier = Modifier.weight(1f), placeholder = { Text("(ex: 7 for July)") })
                    OutlinedTextField(value = startYear, onValueChange = { startYear = it }, label = { Text("Start Year") }, modifier = Modifier.weight(1f))
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = endMonth, onValueChange = { endMonth = it }, label = { Text("End Month") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = endYear, onValueChange = { endYear = it }, label = { Text("End Year") }, modifier = Modifier.weight(1f))
                }

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Checkbox(checked = isCurrentlyWorking, onCheckedChange = { isCurrentlyWorking = it })
                    Text("I currently work here")
                }

                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth().height(100.dp))

                if (errorMessage.isNotEmpty()) {
                    Text(text = errorMessage, color = Color.Red, modifier = Modifier.padding(10.dp))
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Button(onClick = {
                        runBlocking {
                            try {

                                val startDate = LocalDate(startYear.toInt(), startMonth.toInt(), 1)

                                val endDate = if (isCurrentlyWorking) LocalDate(endYear.toInt(), endMonth.toInt(), 1) else LocalDate(endYear.toInt(), endMonth.toInt(), 1)

                                val result = profileController.addWorkExperience(
                                    userId = userId,
                                    companyName = company,
                                    currentlyWorking = isCurrentlyWorking,
                                    title = positionTitle,
                                    startDate = startDate,
                                    endDate = endDate,
                                    description = description.takeIf { it.isNotEmpty() }
                                )

                                result.onSuccess {
                                    onWorkExperienceAdded()
                                    onDismiss()
                                }.onFailure { error ->
                                    errorMessage = error.message ?: "Failed to add work experience."
                                }
                            } catch (e: Exception) {
                                errorMessage = "Invalid date format"
                            }
                        }
                    }, colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF487896), contentColor = Color.White)) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

@Composable
fun EditExperienceDialog(
    experience: WorkExperience? = null,
    onDismiss: () -> Unit,
    userId: String,
    profileController: ProfileController,
    onWorkExperienceEdited: () -> Unit,
    onWorkExperienceDeleted: () -> Unit
) {

    var company by remember { mutableStateOf(experience?.companyName ?: "") }
    var positionTitle by remember { mutableStateOf(experience?.title ?: "") }
    var startMonth by remember { mutableStateOf(experience?.startDate?.month?.value?.toString() ?: "") }
    var startYear by remember { mutableStateOf(experience?.startDate?.year.toString() ?: "") }
    var endMonth by remember { mutableStateOf(experience?.endDate?.month?.value?.toString() ?: "") }
    var endYear by remember { mutableStateOf(experience?.endDate?.year.toString() ?: "") }
    var description by remember { mutableStateOf(experience?.description ?: "") }
    var isCurrentlyWorking by remember { mutableStateOf(experience?.currentlyWorking ?: false) }
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
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (experience != null) {
                        IconButton(
                            onClick = {
                                runBlocking {
                                    val result = experience.id?.let { id ->
                                        profileController.deleteWorkExperience(id.toString())
                                    } ?: Result.failure(Exception("Experience ID is null"))

                                    result.onSuccess {
                                        onWorkExperienceDeleted()
                                        onDismiss()
                                    }.onFailure { error ->
                                        errorMessage = error.message ?: "Failed to delete work experience."
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Work Experience",
                                tint = Color(0xFFBE0303)
                            )
                        }
                    }
                }

                Text("Edit Work Experience", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = company,
                    onValueChange = { company = it },
                    label = { Text("Company") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = positionTitle,
                    onValueChange = { positionTitle = it },
                    label = { Text("Position Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = startMonth,
                        onValueChange = { startMonth = it },
                        label = { Text("Start Month") },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("(ex: 7 for July)") }
                    )
                    OutlinedTextField(
                        value = startYear,
                        onValueChange = { startYear = it },
                        label = { Text("Start Year") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = endMonth,
                        onValueChange = { endMonth = it },
                        label = { Text("End Month") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = endYear,
                        onValueChange = { endYear = it },
                        label = { Text("End Year") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Checkbox(
                        checked = isCurrentlyWorking,
                        onCheckedChange = { isCurrentlyWorking = it }
                    )
                    Text("I currently work here")
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                )

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
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
                            // Handle save or update action
                            runBlocking {
                                try {
                                    val startDate = LocalDate(startYear.toInt(), startMonth.toInt(), 1)
                                    val endDate = if (isCurrentlyWorking) Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date else LocalDate(endYear.toInt(), endMonth.toInt(), 1)
                                    val result = if (experience == null) {

                                        profileController.addWorkExperience(
                                            userId = userId,
                                            companyName = company,
                                            title = positionTitle,
                                            currentlyWorking = isCurrentlyWorking,
                                            startDate = startDate,
                                            endDate = endDate,
                                            description = description.takeIf { it.isNotEmpty() }
                                        )
                                    } else {
                                        profileController.updateWorkExperience(
                                            userId = userId,
                                            workExperienceID = experience.id.toString(),
                                            companyName = company,
                                            currentlyWorking = isCurrentlyWorking,
                                            title = positionTitle,
                                            startDate = startDate,
                                            endDate = endDate,
                                            description = description.takeIf { it.isNotEmpty() }
                                        )
                                    }
                                    result.onSuccess {
                                        onWorkExperienceEdited()
                                        onDismiss()
                                    }.onFailure { error ->
                                        errorMessage = error.message ?: "Failed to save work experience."
                                    }
                                } catch (e: Exception) {
                                    errorMessage = "Invalid date format"
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
                    onValueChange = { skillInput = it },
                    label = { Text("Add a new skill") },
                    modifier = Modifier.fillMaxWidth()
                )


                Button(
                    onClick = {
                        if (skillInput.isNotBlank()) {
                            if (skillInput in selectedSkills) {
                                isDuplicateSkill = true
                            } else {

                                selectedSkills.add(skillInput)
                                skillInput = ""
                                isDuplicateSkill = false
                            }
                        }
                    },
                    modifier = Modifier.align(Alignment.End),
                    enabled = skillInput.isNotBlank()
                ) {
                    Text("Add")
                }

                if (isDuplicateSkill) {
                    Text(
                        text = "This skill has already been added.",
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    selectedSkills.forEach { skill ->
                        SkillChip(skill = skill) {
                            selectedSkills.remove(skill)
                        }
                    }
                }

                if (errorMessage.isNotEmpty()) {
                    Text(text = errorMessage, color = Color.Red)
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

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Profile Page") {
        var currentPage by remember { mutableStateOf("profilePage") }
        ProfilePage(UserSession.userId ?: "DefaultUserId", { currentPage = "profilePage"}, { currentPage = "profilePage"}, { currentPage = "profilePage"})
    }
}
