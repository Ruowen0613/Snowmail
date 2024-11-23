package ca.uwaterloo.view.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.uwaterloo.controller.ProfileController
import ca.uwaterloo.view.SectionTitle
import ca.uwaterloo.view.dialogs.EditSkillsDialog
import kotlinx.coroutines.runBlocking

@Composable
fun SkillsSection(
    userId: String,
    profileController: ProfileController,
    skills: List<String>,
    showSkillsDialog: Boolean,
    onSkillsAdded: () -> Unit,
    onSkillsDeleted: () -> Unit,
    onShowSkillsDialogChange: (Boolean) -> Unit
) {
    Column {
        SectionTitle("Skills")

        Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            IconButton(
                onClick = { onShowSkillsDialogChange(true) },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Skill",
                    tint = Color(0xFF487896)
                )
            }

            if (skills.isEmpty()) {
                Text("No items added", fontSize = 14.sp, color = Color.Gray)
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    skills.forEach { skill ->
                        SkillChip(skill = skill, onDelete = {
                            runBlocking {
                                profileController.deleteSkill(userId, skill)
                                onSkillsDeleted()
                            }
                        })
                    }
                }
            }

            if (showSkillsDialog) {
                EditSkillsDialog(
                    onDismiss = { onShowSkillsDialogChange(false) },
                    onSave = {
                        onSkillsAdded()
                        onShowSkillsDialogChange(false)
                    },
                    userId = userId,
                    profileController = profileController,
                    initialSkills = skills
                )
            }
        }
    }
}
