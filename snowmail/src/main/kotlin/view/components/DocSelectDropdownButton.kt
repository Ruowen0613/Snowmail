package ca.uwaterloo.view.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ca.uwaterloo.controller.DocumentController
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

    Row(
        horizontalArrangement = Arrangement.Center,
        //modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.width(650.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = if (selectedDocument == null) Color(0xFFD5D7E0) else MaterialTheme.colors.primary,
                contentColor = if (selectedDocument == null) Color(0xFF8E97A5) else Color.White
            )
        ) {
            Text(selectedDocument ?: "Your Resume's")
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
