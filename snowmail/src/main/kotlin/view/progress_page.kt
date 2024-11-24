package ca.uwaterloo.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch

import ca.uwaterloo.controller.ProgressController
import ca.uwaterloo.persistence.DocumentRepository
import ca.uwaterloo.persistence.IJobApplicationRepository
import ca.uwaterloo.view.theme.AppTheme
import integration.OpenAIClient
import integration.SupabaseClient
import androidx.compose.foundation.lazy.items
import kotlinx.coroutines.runBlocking
import model.JobApplication
import service.email
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import ca.uwaterloo.controller.ProfileController
import ca.uwaterloo.view.theme.AppTheme
import integration.OpenAIClient
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import service.email
import java.awt.Desktop
import java.net.URI


@Composable
fun JobProgressPage(
    userId: String,
    NavigateToDocuments: () -> Unit,
    NavigateToProfile: () -> Unit,
    NavigateToEmialGen: () -> Unit
) {
    val dbStorage = SupabaseClient()
    val openAIClient = OpenAIClient(HttpClient(CIO))
    val progressController = ProgressController(dbStorage.jobApplicationRepository, openAIClient)
    val profileController = ProfileController(dbStorage.userProfileRepository)

    var selectedTabIndex by remember { mutableStateOf(1) }
    var showDialog by remember { mutableStateOf(false) }
    var emailIndex by remember { mutableStateOf(0) }
    var emails by remember { mutableStateOf<List<email>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var progress by remember { mutableStateOf<IJobApplicationRepository.Progress?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    var refreshTrigger by remember { mutableStateOf(false) }
    var linkedEmail by remember { mutableStateOf<String>("") }
    var appPassword by remember { mutableStateOf<String>("") }

    LaunchedEffect(userId, refreshTrigger) {
        isLoading = true
        val getLinkedEmailResult = profileController.getUserLinkedGmailAccount(userId)
        val getAppPasswardResult = profileController.getUserGmailAppPassword(userId)

        getLinkedEmailResult.onSuccess { result ->
            linkedEmail = result
        }.onFailure { error ->
            errorMessage = error.message ?: "Failed to retrieve user linked email"
        }

        getAppPasswardResult.onSuccess { result ->
            appPassword = result
        }.onFailure { error ->
            errorMessage = error.message ?: "Failed to retrieve user app password"
        }

        val getEmailResult = runCatching { progressController.getNewEmails(userId, linkedEmail, appPassword) }
        val getProgressResult = runCatching { progressController.getProgress(userId) }

        getEmailResult.onSuccess { emailList ->
            emails = emailList
            if (emails.isNotEmpty()) showDialog = true
        }.onFailure { error ->
            errorMessage = error.message ?: "Failed to retrieve user emails"
        }

        getProgressResult.onSuccess { result ->
            progress = result
        }.onFailure { error ->
            errorMessage = error.message ?: "Failed to retrieve user progress"
        }
        isLoading = false
    }



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
                            0 -> NavigateToEmialGen()
                            1 -> {}
                            2 -> NavigateToDocuments()
                            3 -> NavigateToProfile()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))


                if (isLoading || progress == null) {
                    JobStatusColumnsPlaceholder()
                } else {
                    JobStatusColumns(progress!!)
                }

                if (showDialog && emails.isNotEmpty()) {
                    EmailDialog(
                        emails = emails,
                        emailIndex = emailIndex,
                        userId = userId,
                        progressController = progressController,
                        onNextEmail = { emailIndex = (emailIndex + 1) % emails.size },
                        onPreviousEmail = {
                            emailIndex = if (emailIndex == 0) emails.size - 1 else emailIndex - 1
                        },
                        documentRepository = dbStorage.documentRepository,
                        onClose = {
                            showDialog = false
                            emailIndex = 0
                            refreshTrigger = !refreshTrigger
                        }
                    )
                }

                errorMessage?.let {
                    Text(
                        text = it,
                        color = Color.Red,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun JobStatusColumns(progress: IJobApplicationRepository.Progress) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        JobStatusColumn("APPLIED", progress.appliedItemCount, progress.appliedJobs, modifier = Modifier.weight(1f))
        JobStatusColumn("INTERVIEWING", progress.interviewedItemCount, progress.interviewedJobs, modifier = Modifier.weight(1f))
        JobStatusColumn("OFFER", progress.offerItemCount, progress.offerJobs, modifier = Modifier.weight(1f))
        JobStatusColumn("OTHER", progress.otherItemCount, progress.otherJobs, modifier = Modifier.weight(1f))
    }
}

@Composable
fun JobStatusColumnsPlaceholder() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        JobStatusColumnPlaceholder("APPLIED", modifier = Modifier.weight(1f))
        JobStatusColumnPlaceholder("INTERVIEWING", modifier = Modifier.weight(1f))
        JobStatusColumnPlaceholder("OFFER", modifier = Modifier.weight(1f))
        JobStatusColumnPlaceholder("OTHER", modifier = Modifier.weight(1f))
    }
}

@Composable
fun JobStatusColumnPlaceholder(title: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(8.dp))
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "$title (0)",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color(0xFFD3D3D3), shape = RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Loading...",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}



@Composable
fun JobStatusColumn(
    title: String,
    itemCount: Int,
    jobs: List<IJobApplicationRepository.JobProgress>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {

        Text(
            text = "$title ($itemCount)",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxHeight()
        ) {
            if (jobs.isNotEmpty()) {

                items(jobs) { job ->
                    JobCard(
                        position = job.jobTitle,
                        company = job.companyName,
                        recruiterEmail = job.recruiterEmail
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            } else {

                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .background(Color(0xFFD3D3D3), shape = RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No applications",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun JobCard(position: String, company: String, recruiterEmail: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Text(text = position, fontWeight = FontWeight.Bold, fontSize = 14.sp)

        Spacer(modifier = Modifier.height(8.dp))


        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Home,
                contentDescription = "Company Icon",
                tint = Color.Gray,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = company, color = Color.Gray, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(4.dp))


        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Email,
                contentDescription = "Email Icon",
                tint = Color.Gray,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = recruiterEmail, color = Color.Gray, fontSize = 12.sp)
        }
    }
}


@Composable
fun EmailDialog(
    emails: List<email>,
    emailIndex: Int,
    userId: String,
    progressController: ProgressController,
    documentRepository: DocumentRepository,
    onNextEmail: () -> Unit,
    onPreviousEmail: () -> Unit,
    onClose: () -> Unit
) {
    var selectedJobId by remember { mutableStateOf<String?>(null) }
    var selectedStatus by remember { mutableStateOf<Int?>(null) }
    var appliedJobs by remember { mutableStateOf<List<Pair<IJobApplicationRepository.JobProgress, String>>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    var attachLinks by remember { mutableStateOf<List<String>>(emptyList()) }


    LaunchedEffect(userId) {
        appliedJobs = progressController.getAllAppliedJobs(userId, )
    }

    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onClose,
        title = {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Job Application Update: Email ${emailIndex + 1} of ${emails.size}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.Gray
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(8.dp)
            ) {
                Text(
                    text = emails[emailIndex].subject,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(text = emails[emailIndex].text)

                Spacer(modifier = Modifier.height(16.dp))

                attachLinks = emails[emailIndex].attachLink

                if (attachLinks.isNotEmpty()) {
                    Button(
                        onClick = { showDialog = true },
                        modifier = Modifier.padding(vertical = 8.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF487896))
                    ) {
                        Text(text = "View Attached Files",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold)
                    }
                }

                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = {
                            Text(
                                text = "Attached Files",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        },
                        text = {
                            Column {
                                if (attachLinks.isNotEmpty()) {
                                    attachLinks.forEach { link ->
                                        ClickableText(
                                            text = AnnotatedString(
                                                text = link,
                                                spanStyle = SpanStyle(textDecoration = TextDecoration.Underline) // Underline for hyperlink
                                            ),
                                            onClick = {
                                                // Open the link in the default browser
                                                if (Desktop.isDesktopSupported()) {
                                                    try {
                                                        Desktop.getDesktop().browse(URI(link))
                                                    } catch (e: Exception) {
                                                        e.printStackTrace() // Handle invalid URLs or errors
                                                    }
                                                }
                                            },
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        )
                                    }
                                } else {
                                    Text(
                                        text = "No attached files available.",
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        style = MaterialTheme.typography.body2
                                    )
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    showDialog = false
                                }
                            ) {
                                Text(text = "Close")
                            }
                        }
                    )
                }


                // Job Status Selection
                Text("If there has been a change in the status of your job application, please select the appropriate options below:", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val statuses = listOf("APPLIED", "INTERVIEWING", "OFFER", "OTHER", "REJECTED")
                    statuses.forEachIndexed { index, status ->
                        Button(
                            onClick = { selectedStatus = index },
                            colors = ButtonDefaults.buttonColors(backgroundColor = if (selectedStatus == index) Color(0xFF487896) else Color.LightGray)
                        ) {
                            Text(status, color = if (selectedStatus == index) Color.White else Color.Black)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Job Title Selection with scrolling support
                Text("Please select the title for this job:", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp) // Set a max height with scrolling if overflow
                ) {
                    items(appliedJobs) { (job, jobId) ->
                        Button(
                            onClick = { selectedJobId = jobId },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = if (selectedJobId == jobId) Color(0xFF487896) else Color.LightGray)
                        ) {
                            Text("${job.jobTitle} - ${job.companyName}", color = if (selectedJobId == jobId) Color.White else Color.Black)
                        }
                    }
                }
            }
        },
        buttons = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                if (emailIndex > 0) {
                    Button(
                        onClick = onPreviousEmail,
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF487896))
                    ) {
                        Text("Back", color = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Button(
                    onClick = {
                        if (selectedJobId != null && selectedStatus != null) {
                            coroutineScope.launch {
                                progressController.modifyStatus(selectedJobId!!, selectedStatus!! + 1)
                                if (emailIndex + 1 < emails.size) {
                                    onNextEmail()
                                } else {
                                    onClose()
                                }
                                runBlocking {
                                    println(emails)
                                    println(emails[emailIndex].fileNames)
                                    progressController.deleteAttachments(emails[emailIndex].fileNames, documentRepository)
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = if (selectedJobId != null && selectedStatus != null) Color(0xFF487896) else Color.Gray),
                    enabled = selectedJobId != null && selectedStatus != null
                ) {
                    Text("Save", color = Color.White)
                }
            }
        }
    )
}

