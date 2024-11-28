package ca.uwaterloo.view

//import androidx.compose.material3.*
//import kotlinx.serialization.Serializable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.AwtWindow
import ca.uwaterloo.controller.DocumentController
import ca.uwaterloo.view.components.DocDropdownRow
import ca.uwaterloo.view.components.DocumentUploadButton
//import ca.uwaterloo.view.components.DocumentUploadButton2
import integration.SupabaseClient
import java.awt.FileDialog
import java.awt.Frame


@Composable
fun DocumentPage(NavigateToEmailGen: () -> Unit, NavigateToProfile: () -> Unit,
                 NavigateToProgress: () -> Unit, NavigateToLogin: () -> Unit) {
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
                        0 -> navigateOtherPage(NavigateToEmailGen)
                        1 -> navigateOtherPage(NavigateToProgress)
                        2 -> {}
                        3 -> navigateOtherPage(NavigateToProfile)
                    }
                },
                NavigateToLogin = NavigateToLogin
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
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    item { Spacer(modifier = Modifier.height(25.dp)) }
                    item { DocDropdownRow("Resume", documentController) }
                    item { Spacer(modifier = Modifier.height(20.dp)) }
                    item { DocDropdownRow("Cover Letter", documentController) }
                    item { Spacer(modifier = Modifier.height(20.dp)) }
                    item { DocDropdownRow("Transcript", documentController) }
                    item { Spacer(modifier = Modifier.height(20.dp)) }
                    item { DocDropdownRow("Certificates", documentController) }
                    item { Spacer(modifier = Modifier.height(20.dp)) }
                    item { DocDropdownRow("Others", documentController) }
                    item { Spacer(modifier = Modifier.height(20.dp)) }
                }
            }
        }
    }
}
