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


@Composable
fun ProfilePage() {
    var email by remember { mutableStateOf("user@gmail.com") }
    var location by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("+1 ") }

    var showEducationDialog by remember { mutableStateOf(false) }
    var showExperienceDialog by remember { mutableStateOf(false) }
    var showSkillsDialog by remember { mutableStateOf(false) }
    var EditContactDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFFF8FAFC))
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

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
            Text(
                "Name",
                fontSize = 24.sp,
                style = MaterialTheme.typography.h5,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }

        // Outer Card
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
                // Edit button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = { EditContactDialog = true }, // Set to true to open the dialog
                        modifier = Modifier.size(14.dp) // Adjust size as needed
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit, // Choose the desired icon (Edit icon here)
                            contentDescription = "Edit",
                            tint = Color(0xFF487896) // Customize icon color
                        )
                    }
                }

                // Show the popup dialog when showEditDialog is true
                if (EditContactDialog) {
                    EditContactDialog(onDismiss = { EditContactDialog = false })
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Contact Information
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
                        // Contact Info
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            ProfileDetail(label = "Email Address", value = email)
                            ProfileDetail(label = "Location", value = location)
                            ProfileDetail(label = "Phone", value = phone)
                        }

                        // Resume Link
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

                    Text(
                        text = "No education records added",
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
                                onClick = { showEducationDialog = true },
                                modifier = Modifier.size(15.dp) // Adjust size as needed
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add",
                                    tint = Color(0xFF487896) // Edit icon color
                                )
                            }
                        }

                        // Show the popup dialog when showDialog is true
                        if (showEducationDialog) {
                            EditEducationDialog(onDismiss = { showEducationDialog = false })
                        }
                    }
                }

                SectionTitle("Work experience")
                Card(modifier = Modifier.fillMaxWidth().padding(8.dp).height(80.dp)) {
                    Text(
                        text = "No experiences added",
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
                                onClick = { showExperienceDialog = true },
                                modifier = Modifier.size(15.dp) // Adjust size as needed
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add",
                                    tint = Color(0xFF487896) // Edit icon color
                                )
                            }
                        }

                        // Show the popup dialog when showDialog is true
                        if (showExperienceDialog) {
                            EditExperienceDialog(onDismiss = { showExperienceDialog = false })
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

                        // Show the popup dialog when showDialog is true
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

                // Email Field
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    placeholder = { Text("         @gmail.com") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Location Field
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") },
                    placeholder = { Text("Type city") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Phone Field
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

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Button(onClick = {
                        // Handle save logic here if needed
                        onDismiss() // Close dialog after saving
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
fun EditEducationDialog(onDismiss: () -> Unit) {
    // State variables for each input field
    var schoolName by remember { mutableStateOf("") }
    var major by remember { mutableStateOf("") }
    var degreeType by remember { mutableStateOf("") }
    var gpa by remember { mutableStateOf("") }
    var startMonth by remember { mutableStateOf("") }
    var startYear by remember { mutableStateOf("") }
    var endMonth by remember { mutableStateOf("") }
    var endYear by remember { mutableStateOf("") }

    // Dropdown state for degree type
    var expanded by remember { mutableStateOf(false) }
    val degreeTypes = listOf( "Associate's/College Diploma","Bachelor's", "Doctorate",
                                 "High School Diploma/GED","Master's",  "Other")

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
                // Dialog header
                Text(
                    text = "Edit Education",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Input fields
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

                // Degree Type Dropdown with Icon Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.TopStart) // Align to the top start of the box for more control
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = true },
                        readOnly = true
                    )

                    // Drop down menu aligned with the text field
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
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


                // Other fields
                OutlinedTextField(
                    value = gpa,
                    onValueChange = { gpa = it },
                    label = { Text("GPA") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = startMonth,
                        onValueChange = { startMonth = it },
                        label = { Text("Start Month") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = startYear,
                        onValueChange = { startYear = it },
                        label = { Text("Start Year") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
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

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Button(onClick = {
                        // Handle save logic here if needed
                        onDismiss() // Close dialog after saving
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
fun EditExperienceDialog(onDismiss: () -> Unit) {
    // State variables for each input field
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

    // Dropdown options
    var experienceTypeExpanded by remember { mutableStateOf(false) }
    val experienceTypes = listOf("Full-time", "Part-time", "Internship", "Contract")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Wrapping the Column with verticalScroll for scrolling functionality
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()), // Added vertical scroll
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Dialog header
                Text(
                    text = "Add Work Experience",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Input fields with editable states
                OutlinedTextField(
                    value = company,
                    onValueChange = { company = it },
                    label = { Text("Company") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = positionTitle,
                    onValueChange = { positionTitle = it },
                    label = { Text("Position Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Experience Type Dropdown
                Box {
                    OutlinedTextField(
                        value = experienceType,
                        onValueChange = {},
                        label = { Text("Experience Type") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { experienceTypeExpanded = true },
                        readOnly = true
                    )
                    DropdownMenu(
                        expanded = experienceTypeExpanded,
                        onDismissRequest = { experienceTypeExpanded = false }
                    ) {
                        experienceTypes.forEach { type ->
                            DropdownMenuItem(onClick = {
                                experienceType = type
                                experienceTypeExpanded = false
                            }) {
                                Text(type)
                            }
                        }
                    }
                }

                // Date fields
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = startMonth,
                        onValueChange = { startMonth = it },
                        label = { Text("Start Month") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = startYear,
                        onValueChange = { startYear = it },
                        label = { Text("Start Year") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
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

                // Checkbox for "Currently Working Here"
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = isCurrentlyWorking,
                        onCheckedChange = { isCurrentlyWorking = it }
                    )
                    Text("I currently work here")
                }

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                )

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Button(onClick = {
                        // Handle save logic here if needed
                        onDismiss() // Close dialog after saving
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
fun EditSkillsDialog(onDismiss: () -> Unit) {
    // State variables for input fields
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
                // Dialog Header
                Text(
                    text = "Edit Skills",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))


                // Skill Search Dropdown
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

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            // Save action (if any logic is needed)
                            onDismiss() // Close dialog after saving
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
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = TextStyle(
            fontSize = 16.sp, // Adjust size as needed
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
        ProfilePage()
    }
}
