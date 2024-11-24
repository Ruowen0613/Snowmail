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
import ca.uwaterloo.view.components.DocDropdownRow
import ca.uwaterloo.view.components.DocumentUploadButton
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
    var documentType by remember { mutableStateOf("Resume") }

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
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier= Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(Color.Transparent),
                horizontalArrangement = Arrangement.End,
            ) {
                DocumentUploadButton(documentController)
            }
            Spacer(modifier = Modifier.height(15.dp))
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(25.dp))
                DocDropdownRow("Resume", documentController)
                Spacer(modifier = Modifier.height(15.dp))
                DocDropdownRow("Cover Letter", documentController)
                Spacer(modifier = Modifier.height(15.dp))
                DocDropdownRow("Transcript", documentController)
                Spacer(modifier = Modifier.height(15.dp))
                DocDropdownRow("Certificates", documentController)
                Spacer(modifier = Modifier.height(15.dp))
                DocDropdownRow("Others", documentController)
                Spacer(modifier = Modifier.height(15.dp))
            }
        }
    }
}