package ca.uwaterloo.view.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import ca.uwaterloo.model.EducationWithDegreeName
import ca.uwaterloo.model.WorkExperience
import controller.EmailGenerationController
import model.GeneratedEmail
import model.UserInput
import model.UserProfile
import java.io.File
import kotlinx.coroutines.runBlocking

@Composable
fun EmailGenerationButton(
    emailGenerationController: EmailGenerationController,
    userInput: UserInput,
    userProfile: UserProfile,
    gotEducation: List<EducationWithDegreeName>,
    gotWorkExperience: List<WorkExperience>,
    gotSkills: List<String>,
    resumeFile: File?,
    onEmailGenerated: (String) -> Unit,
    onShowDialog: (Boolean) -> Unit,
    infoSource: String,
    enabled: Boolean
) {
    var emailContent by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    Button(
        onClick = {
            runBlocking {
                try {
                    if (infoSource == "profile") {
                        val generatedEmail: GeneratedEmail? = emailGenerationController.generateEmail(
                            informationSource = "profile",
                            userInput = userInput,
                            userProfile = userProfile,
                            education = gotEducation,
                            workExperience = gotWorkExperience,
                            skills = gotSkills
                            //resumeFile = resumeFile
                        )

                        println("Generated Email: ${generatedEmail?.body}")
                        emailContent = generatedEmail?.body ?: "Failed to generate email"
                        onEmailGenerated(emailContent)
                        onShowDialog(true)
                    } else {
                        val generatedEmail: GeneratedEmail? = emailGenerationController.generateEmail(
                            informationSource = "resume",
                            userInput = userInput,
                            userProfile = userProfile,
                            education = gotEducation,
                            workExperience = gotWorkExperience,
                            skills = gotSkills,
                            resumeFile = resumeFile
                        )

                        println("Generated Email: ${generatedEmail?.body}")
                        emailContent = generatedEmail?.body ?: "Failed to generate email"
                        onEmailGenerated(emailContent)
                        onShowDialog(true)
                    }
                } catch (e: Exception) {
                    println("Error: ${e.message}")
                }
            }
        },
        //modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color(0xFF487B96),
            contentColor = MaterialTheme.colors.onPrimary
        ),
        enabled = enabled
    ) {
        Text(if (infoSource == "profile") "Generate with Profile information" else "Generate with uploaded Resume")
    }
}