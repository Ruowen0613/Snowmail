package ca.uwaterloo.view.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import ca.uwaterloo.controller.DocumentController
import ca.uwaterloo.controller.ProfileController
import controller.SendEmailController
import integration.SupabaseClient
import kotlinx.coroutines.runBlocking

@Composable
fun GeneratedEmailAlertDialog(
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
    var showAttachDialog by remember { mutableStateOf(false) }
    val attachedDocuments = remember { mutableStateListOf<Pair<String, String>>() }

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

    if (showAttachDialog) {
        AttachDocumentDialog(
            userId = userId,
            onDismissRequest = { showAttachDialog = false },
            onConfirm = { selectedDocuments ->
                attachedDocuments.clear()
                attachedDocuments.addAll(selectedDocuments)
                showAttachDialog = false
            },
            documentController = DocumentController(SupabaseClient().documentRepository)
        )
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colors.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                //Text(text = title, style = MaterialTheme.typography.h6)
                //Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = recipientEmailAddy,
                    onValueChange = { recipientEmailAddy = it },
                    label = { Text("Recipient Email") },
                    modifier = Modifier
                        .clip(RoundedCornerShape(5.dp))
                        .fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors()
                )
                OutlinedTextField(
                    value = emailSubject,
                    onValueChange = { emailSubject = it },
                    label = { Text("Subject") },
                    modifier = Modifier
                        .clip(RoundedCornerShape(5.dp))
                        .fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors()
                )
                Button(
                    onClick = { showAttachDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF487B96),
                        contentColor = MaterialTheme.colors.onPrimary
                    )
                ) {
                    Text("Attach Document")
                }
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                        //.verticalScroll(rememberScrollState())
                        .background(Color.Transparent),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedLabelColor = Color.Transparent,
                        unfocusedLabelColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = onDismissRequest,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFF487B96),
                            contentColor = MaterialTheme.colors.onPrimary
                        )
                    ) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            runBlocking {
                                try {
                                    val emailsendingController = SendEmailController(SupabaseClient().jobApplicationRepository, SupabaseClient().documentRepository)
                                    val returnMessage = emailsendingController.send_email(
                                        recipient = recipientEmailAddy,
                                        subject = emailSubject,
                                        text = text,
                                        buckets = List(attachedDocuments.size) { "user_documents" },
                                        documentsType = attachedDocuments.map { it.second },
                                        documentsName = attachedDocuments.map { it.first },
                                        userID = userId,
                                        jobTitle = jobTitle,
                                        companyName = companyName
                                    )
                                    if (returnMessage == "Success") {
                                        // show success message
                                    } else if (returnMessage == "Missing Gmail Account or Password, please go to profile page and finish linking gmail account") {
                                        // show error message
                                    } else {
                                        // show error message
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
                        )
                    ) {
                        Text("Send")
                    }
                }
            }
        }
    }
}