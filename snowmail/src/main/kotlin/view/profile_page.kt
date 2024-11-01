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


import ca.uwaterloo.controller.ProfileController

import integration.SupabaseClient
import ca.uwaterloo.model.Education
import ca.uwaterloo.model.WorkExperience
import kotlinx.coroutines.runBlocking


@Composable
fun ProfilePage(userId: String,
                NavigateToDocuments: () -> Unit, NavigateToEmialGen: () -> Unit,
                NavigateToProgress: () -> Unit) {
    val dbStorage = SupabaseClient()
    val profileController = ProfileController(dbStorage)

    var email by remember { mutableStateOf("user@gmail.com") }
    var location by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("+1 ") }

    var showEducationDialog by remember { mutableStateOf(false) }
    var showExperienceDialog by remember { mutableStateOf(false) }
    var showSkillsDialog by remember { mutableStateOf(false) }
    var EditContactDialog by remember { mutableStateOf(false) }

    var userName by remember { mutableStateOf("") }
    var educationList by remember { mutableStateOf<List<Education>>(emptyList()) }
    var errorMessage by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }

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

    LaunchedEffect(userId) {

        val getNameResult = profileController.getUserName(userId)
        val educationResult = profileController.getEducation(userId)
        val experienceResult = profileController.getWorkExperience(userId)
        val getEmailResult = profileController.getUserEmail(userId)

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
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFFF8FAFC))
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
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
                        onClick = { navigateOtherPage(NavigateToEmialGen) },
                        text = { Text("Cold Email Generation") }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { navigateOtherPage(NavigateToProgress)},
                        text = { Text("Job Application Progress") }
                    )
                    Tab(
                        selected = selectedTabIndex == 2,
                        onClick = {navigateOtherPage(NavigateToDocuments)},
                        text = { Text("Documents") }
                    )
                    Tab(
                        selected = selectedTabIndex == 3,
                        onClick = {},
                        text = { Text("Profile",
                            fontWeight = FontWeight.Bold)}
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


                if (EditContactDialog) {
                    EditContactDialog(onDismiss = { EditContactDialog = false })
                }

                Spacer(modifier = Modifier.height(4.dp))


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
                            ProfileDetail(label = "Email Address", value = userEmail)
                            ProfileDetail(label = "Location", value = location)
                            ProfileDetail(label = "Phone", value = phone)
                        }


                        Row(verticalAlignment = Alignment.CenterVertically) {

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "Resume.pdf",
                                modifier = Modifier
                                    .padding(end = 32.dp)
                                    .clickable {
                                        // Add the action you want to perform on click here, such as opening the PDF
                                    },
                                style = MaterialTheme.typography.body1.copy(
                                    fontSize = 14.sp,
                                    color = Color(0xFF2669A0)
                                )
                            )
                        }

                    }
                }

                SectionTitle("Education")
                Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    if (educationList.isEmpty()) {
                        Text(
                            text = "No education records added",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    } else {
                        Column(modifier = Modifier.padding(8.dp)) {
                            educationList.forEach { education ->
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
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            IconButton(
                                onClick = { showEducationDialog = true },
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
                            EditEducationDialog(
                                onDismiss = { showEducationDialog = false },
                                userId = userId,
                                profileController = profileController,
                                onEducationAdded = { refreshEducationList() }
                            )
                        }
                    }
                }

                SectionTitle("Work experience")
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
                                Column(modifier = Modifier.padding(bottom = 8.dp)) {
                                    Text(
                                        text = "${experience.companyName} - ${experience.title}",
                                        fontSize = 14.sp,
                                        color = Color.Black
                                    )
                                    Text(
                                        text = "From ${experience.startDate} to ${
                                            experience.endDate ?: "Present"
                                        }",
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
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            IconButton(
                                onClick = { showExperienceDialog = true },
                                modifier = Modifier.size(15.dp) // Adjust size as needed
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add",
                                    tint = Color(0xFF487896) // Customize icon color
                                )
                            }
                        }


                        if (showExperienceDialog) {
                            EditExperienceDialog(
                                onDismiss = { showExperienceDialog = false },
                                userId = userId,  // Pass userId here
                                profileController = profileController,  // Pass profileController here
                                onWorkExperienceAdded = { refreshWorkExperienceList() }
                            )
                        }
                    }
                }

                SectionTitle("Skills")
                Card(modifier = Modifier.fillMaxWidth().padding(8.dp).height(80.dp)) {
                    Text(
                        text = "No items added",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            IconButton(
                                onClick = { showSkillsDialog = true },
                                modifier = Modifier.size(15.dp) // Adjust size as needed
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add",
                                    tint = Color(0xFF487896) // Edit icon color
                                )
                            }
                        }

                        if (showSkillsDialog) {
                            EditSkillsDialog(onDismiss = { showSkillsDialog = false })
                        }
                    }
                }


            }
        }
    }
}

@Composable
fun EditContactDialog(onDismiss: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }

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
                // Header
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
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    placeholder = { Text("         @gmail.com") },
                    modifier = Modifier.fillMaxWidth()
                )


                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") },
                    placeholder = { Text("Type city") },
                    modifier = Modifier.fillMaxWidth()
                )


                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Replace this with a country code picker if available
                    Text("+1", modifier = Modifier.padding(end = 8.dp))
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone") },
                        modifier = Modifier.fillMaxWidth()
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
                    Button(onClick = {
                        onDismiss()
                    }, colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF487896),
                        contentColor = Color.White
                    )) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

@Composable
fun EditEducationDialog(
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

                Text("Edit Education", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)

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
                    OutlinedTextField(value = startMonth, onValueChange = { startMonth = it }, label = { Text("Start Month") }, modifier = Modifier.weight(1f))
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
                            val degreeId = degreeTypes.indexOf(degreeType) + 1 // Example for degreeId

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
fun EditExperienceDialog(
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
                    OutlinedTextField(value = startMonth, onValueChange = { startMonth = it }, label = { Text("Start Month") }, modifier = Modifier.weight(1f))
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
fun EditSkillsDialog(onDismiss: () -> Unit) {

    var skillSearchText by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val skillsList = listOf("Kotlin", "Java", "Python", "Swift", "JavaScript") // Example skill options

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
                    text = "Edit Skills",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))



                Box {
                    OutlinedTextField(
                        value = skillSearchText,
                        onValueChange = { skillSearchText = it },
                        label = { Text("Skill (ex: Project Management)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = true },
                        readOnly = true
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        skillsList.forEach { skill ->
                            DropdownMenuItem(onClick = {
                                skillSearchText = skill
                                expanded = false
                            }) {
                                Text(skill)
                            }
                        }
                    }
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

                            onDismiss()
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
            "$label: ",
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
