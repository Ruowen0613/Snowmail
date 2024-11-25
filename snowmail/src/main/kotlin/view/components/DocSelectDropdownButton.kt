package ca.uwaterloo.view.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import ca.uwaterloo.controller.DocumentController
import ca.uwaterloo.view.UserSession
import integration.SupabaseClient

// for selecting resume document for email generation
@Composable
fun DocumentSelectionDropdownButton(
    userId: String,
    selectedDocument: String?,
    onDocumentSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val documentController = DocumentController(SupabaseClient().documentRepository)
    var documentList by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(Unit) {
        val result = documentController.listDocuments("user_documents", userId, "Resume")
        result.onSuccess { documents ->
            documentList = documents
        }.onFailure { error ->
            println("Error listing documents: ${error.message}")
        }
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFF487B96),
                contentColor = MaterialTheme.colors.onPrimary
            )
        ) {
            Text(selectedDocument ?: "Choose one resume you would like to include for generating your cold email")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            documentList.forEach { document ->
                DropdownMenuItem(onClick = {
                    onDocumentSelected(document)
                    expanded = false
                }) {
                    Text(document)
                }
            }
        }
    }
}