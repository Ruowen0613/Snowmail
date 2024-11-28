package ca.uwaterloo.view.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ca.uwaterloo.controller.DocumentController
import ca.uwaterloo.controller.ProfileController
import controller.SendEmailController
import integration.SupabaseClient
import kotlinx.coroutines.runBlocking
//import com.google.accompanist.flowlayout.FlowRow
//import com.google.accompanist.flowlayout.MainAxisAlignment


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
    var emailSubject by remember { mutableStateOf("Application for $jobTitle at $companyName") }
    var showAttachDialog by remember { mutableStateOf(false) }
    val attachedDocuments = remember { mutableStateListOf<Pair<String, String>>() }
    var errorMessage by remember { mutableStateOf("") }
    var sendEmailTrigger by remember { mutableStateOf(false) }

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

    if (sendEmailTrigger) {
        LaunchedEffect(sendEmailTrigger) {
            try {
                val emailsendingController = SendEmailController(
                    SupabaseClient().jobApplicationRepository,
                    SupabaseClient().documentRepository
                )
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
                    println("Setting showSuccessDialog = true")
                    errorMessage = ""
                    onConfirm(text)
                    onDismissRequest()
                } else {
                    errorMessage = returnMessage
                }
            } catch (e: Exception) {
                errorMessage = "Error sending email: ${e.message}"
                println("Error sending email: ${e.message}")
            } finally {
                sendEmailTrigger = false
            }
        }
    }



    Dialog(onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(16.dp)
        ) {
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
                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )
                    }

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
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                    ) {
                        var text = "Attached Files:"
                        attachedDocuments.forEach { (name, type) ->
                            text += " $name"
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = text,
                            color = Color.DarkGray,
                            fontWeight = FontWeight.Bold,
                            //fontStyle = FontStyle.Italic,
                            fontSize = 12.sp,

                        )
//                        OutlinedTextField(
//                            value = text,
//                            onValueChange = { },
//                            label = { Text("Attached Files") },
//                            modifier = Modifier
//                                .clip(RoundedCornerShape(5.dp))
//                                .fillMaxWidth(),
//                            colors = TextFieldDefaults.outlinedTextFieldColors(
//                                //focusedLabelColor = Color.Transparent,
//                                //unfocusedLabelColor = Color.Transparent,
//                                //focusedBorderColor = Color.Transparent,
//                                //unfocusedBorderColor = Color.Transparent
//                            ),
//                            textStyle = LocalTextStyle.current.copy(fontSize = 10.sp)
//                        )

                    }
                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        label = { Text("Editable email content") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                            .background(Color.Transparent),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedLabelColor = Color.Transparent,
                            //unfocusedLabelColor = Color.Transparent,
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
                                when {
                                    recipientEmailAddy.isBlank() -> {
                                        errorMessage = "Recipient email cannot be empty."
                                    }
                                    else -> {
                                        errorMessage = ""
                                        sendEmailTrigger = true
                                    }
                                }
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
}
