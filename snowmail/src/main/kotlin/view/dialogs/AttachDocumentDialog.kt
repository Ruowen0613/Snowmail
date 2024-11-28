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
import ca.uwaterloo.controller.ProfileController
import controller.SendEmailController
import integration.SupabaseClient
import kotlinx.coroutines.runBlocking
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ca.uwaterloo.controller.DocumentController
import ca.uwaterloo.model.EducationWithDegreeName
import ca.uwaterloo.model.WorkExperience
import ca.uwaterloo.service.ParserService
import ca.uwaterloo.view.UserSession
import ca.uwaterloo.view.components.DocumentSelectionDropdownButton
import ca.uwaterloo.view.components.EmailGenInputField
import ca.uwaterloo.view.components.EmailGenerationButton
//import ca.uwaterloo.view.components.FetchUserProfileData
import ca.uwaterloo.view.dialogs.GeneratedEmailAlertDialog
import ca.uwaterloo.view.theme.AppTheme
import controller.EmailGenerationController
import integration.OpenAIClient
import io.ktor.client.*
//import io.ktor.client.engine.cio.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import model.GeneratedEmail
import model.UserInput
import model.UserProfile
import service.EmailGenerationService
import java.io.File


@Composable
fun AttachDocumentDialog(
    userId: String,
    onDismissRequest: () -> Unit,
    onConfirm: (List<Pair<String, String>>) -> Unit,
    documentController: DocumentController
) {
    val documentTypes = listOf("Resume", "Cover Letter", "Transcript", "Certificates", "Others")
    val selectedDocuments = remember { mutableStateListOf<Pair<String, String>>() }
    val documents = remember { mutableStateListOf<Pair<String, String>>() }

    val coroutineScope = rememberCoroutineScope()

    val allDocs = remember { mutableStateListOf<Pair<String, String>>() }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {

            documentTypes.forEach { documentType ->
                val result = documentController.listDocuments("user_documents", UserSession.userId ?: "DefaultUserId", documentType)
                result.onSuccess { docs ->
                    allDocs.addAll(docs.map { doc -> Pair(doc, documentType) })
                }.onFailure { error ->
                    println("Error listing documents: ${error.message}")
                }
            }
            documents.addAll(allDocs)
            isLoading = false
        }
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Attach Documents") },
        text = {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Column {
                    documents.forEach { document ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = selectedDocuments.contains(document),
                                onCheckedChange = { isChecked ->
                                    if (isChecked) {
                                        selectedDocuments.add(document)
                                    } else {
                                        selectedDocuments.remove(document)
                                    }
                                }
                            )
                            Text(document.first)
                            Text(" type: " + document.second)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(selectedDocuments)
                    onDismissRequest()
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF487B96),
                    contentColor = MaterialTheme.colors.onPrimary
                )
            ) {
                Text("Attach")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismissRequest,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF487B96),
                    contentColor = MaterialTheme.colors.onPrimary
                )
            ) {
                Text("Cancel")
            }
        }
    )
}