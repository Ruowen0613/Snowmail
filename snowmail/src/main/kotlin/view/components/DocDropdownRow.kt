package ca.uwaterloo.view.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
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
import ca.uwaterloo.view.UserSession
import ca.uwaterloo.view.components.DocumentUploadButton
import kotlinx.coroutines.launch
import java.awt.Desktop
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

// A function that is a row in the document page.
// The row has a type that is passed to it as a parameter like "Resume" or "Cover Letter".
// When clicked, it will dropdown and display a list of documents that are the same type as the row type.

@Composable
fun DocDropdownRow(documentType: String, documentController: DocumentController) {
    var expanded by remember { mutableStateOf(false) }
    var documentList by remember { mutableStateOf<List<String>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(expanded) {
        if (expanded) {
            coroutineScope.launch {
                val result = documentController.listDocuments("user_documents", UserSession.userId ?: "DefaultUserId", documentType)
                result.onSuccess { documents ->
                    documentList = documents
                }.onFailure { error ->
                    println("Error listing documents: ${error.message}")
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { expanded = !expanded }
    ) {
        Text(
            text = documentType,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        if (expanded) {
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
        }
    }
}