package ca.uwaterloo.view.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
                        //.padding(32.dp)
                        .fillMaxWidth()
                        .background(Color.Transparent),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        //focusedLabelColor = Color.Transparent,
                        //unfocusedLabelColor = Color.Transparent,
                        //focusedBorderColor = Color.Transparent,
                        //unfocusedBorderColor = Color.Transparent
                    )
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
                //Spacer(modifier = Modifier.height(8.dp))
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
                //Spacer(modifier = Modifier.height(8.dp))
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
                //Spacer(modifier = Modifier.height(8.dp))

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
                            buckets = listOf("user_documents"),
                            documentsType = attachedDocuments.map { it.second },
                            documentsName = attachedDocuments.map { it.first },
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