package ca.uwaterloo.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.unit.*
//import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import model.UserInput
import model.UserProfile
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
//import kotlinx.serialization.Serializable

import androidx.compose.ui.window.application
import androidx.compose.ui.window.Window
import service.EmailGenerationService
import integration.OpenAIClient
import androidx.compose.foundation.background
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import ca.uwaterloo.controller.DocumentController

import ca.uwaterloo.controller.ProfileController
import controller.send_email
import integration.SupabaseClient
import kotlinx.coroutines.runBlocking
import androidx.compose.ui.window.AwtWindow
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import androidx.compose.runtime.*
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.launch
import java.awt.Desktop
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


@Composable
fun DocumentPage(NavigateToEmialGen: () -> Unit, NavigateToProfile: () -> Unit,
                 NavigateToProgress: () -> Unit) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val documentController = DocumentController(SupabaseClient().documentRepository)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
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
                        0 -> navigateOtherPage(NavigateToEmialGen)
                        1 -> navigateOtherPage(NavigateToProgress)
                        2 -> {}
                        3 -> navigateOtherPage(NavigateToProfile)
                    }
                }
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                DocumentUploadRow("Resume", documentController)
                Spacer(modifier = Modifier.height(16.dp))
                DocumentUploadRow("Cover Letter", documentController)
                Spacer(modifier = Modifier.height(16.dp))
                DocumentUploadRow("Transcript", documentController)
                Spacer(modifier = Modifier.height(16.dp))
                DocumentUploadRow("Certificates", documentController)
                Spacer(modifier = Modifier.height(16.dp))
                DocumentUploadRow("Others", documentController)
            }
        }
    }
}

@Composable
fun DocumentUploadRow(documentType: String, documentController: DocumentController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = documentType,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Row {
            UploadButton("Upload", documentType, documentController)
            Spacer(modifier = Modifier.width(8.dp))
            UploadButton("View", documentType, documentController)
        }
    }
}

@Composable
fun UploadButton(text: String, documentType: String, documentController: DocumentController) {
    val coroutineScope = rememberCoroutineScope()
    var selectedFile by remember { mutableStateOf<File?>(null) }
    var showFileDialog by remember { mutableStateOf(false) }
    var showDocumentListDialog by remember { mutableStateOf(false) }

    Button(
        onClick = {
            if (text == "Upload") {
                showFileDialog = true
            } else {
                showDocumentListDialog = true
            }
        },
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.height(40.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color(0xFF487896),
            contentColor = Color.White
        )
    ) {
        Text(text = text, color = Color.White, fontSize = 14.sp)
    }

    if (showFileDialog) {
        AwtWindow(
            create = {
                FileDialog(Frame(), "Select a file", FileDialog.LOAD).apply {
                    isVisible = true
                    selectedFile = file?.let { File(directory, it) }
                    showFileDialog = false
                }
            },
            dispose = FileDialog::dispose
        )
    }

    if (showDocumentListDialog) {
        DocumentListDialog(
            documentType = documentType,
            documentController = documentController,
            onDismissRequest = { showDocumentListDialog = false }
        )
    }

    LaunchedEffect(selectedFile) {
        selectedFile?.let { file ->
            coroutineScope.launch {
                val result = documentController.uploadDocument(
                    bucket = "user_documents",
                    userId = UserSession.userId ?: "DefaultUserId",
                    documentType = documentType,
                    documentName = file.name,
                    file = file
                )
                result.onSuccess {
                    println("Upload successful: $it")
                }.onFailure { error ->
                    println("Error uploading document: ${error.message}")
                }
            }
        }
    }
}

@Composable
fun DocumentListDialog(
    documentType: String,
    documentController: DocumentController,
    onDismissRequest: () -> Unit
) {
    var documentList by remember { mutableStateOf<List<String>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val result = documentController.listDocuments("user_documents", UserSession.userId ?: "DefaultUserId", documentType)
            result.onSuccess { documents ->
                documentList = documents
            }.onFailure { error ->
                println("Error listing documents: ${error.message}")
            }
        }
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties()
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.White,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "Documents", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
                documentList.forEach { document ->
                    Text(
                        text = document,
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .clickable {
                                coroutineScope.launch {
                                    val encodedDocument = URLEncoder.encode(document, StandardCharsets.UTF_8.toString())
                                    val result = documentController.viewDocument("user_documents", UserSession.userId ?: "DefaultUserId", documentType, encodedDocument)
                                    result.onSuccess { url ->
                                        if (Desktop.isDesktopSupported()) {
                                            Desktop.getDesktop().browse(URI(url))
                                        } else {
                                            println("Desktop is not supported. Please open the URL manually: $url")
                                        }
                                    }.onFailure { error ->
                                        println("Error creating signed URL: ${error.message}")
                                    }
                                }
                            },
                        color = Color.Blue,
                        textDecoration = TextDecoration.Underline
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onDismissRequest,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF487896),
                        contentColor = Color.White
                    )) {
                    Text("Close")
                }
            }
        }
    }
}