package ca.uwaterloo.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import model.UserInput
import model.UserProfile

@Composable
fun EmailGenerationPage() {
    val client = HttpClient()
    var emailContent by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize()) {
        Text(text = emailContent, modifier = Modifier.padding(16.dp))
        Button(onClick = {
            coroutineScope(Dispatchers.IO).launch {
                val openAIClient = OpenAIClient(HttpClient(CIO))
                val userInput = UserInput(
                    jobDescription = "Software Engineer",
                    recruiterEmail = "recruiter@example.com",
                    jobTitle = "Software Engineer",
                    company = "Example Corp",
                    recruiterName = "Jane Doe"
                )

                val userProfile = UserProfile(
                    userId = "123",
                    firstName = "John",
                    lastName = "Doe",
                    skills = listOf("Java", "Kotlin", "SQL")
                )

                val generatedEmail = openAIClient.generateEmail(userInput, userProfile)
                emailContent = "Subject: ${generatedEmail.subject}\n\n${generatedEmail.body}"
            }
        }) {
            Text("Generate Email")
        }
    }