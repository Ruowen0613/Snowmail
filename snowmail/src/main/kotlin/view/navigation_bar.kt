package ca.uwaterloo.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ca.uwaterloo.controller.SignInController
import integration.SupabaseClient
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Composable
fun TopNavigationBar(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    NavigateToLogin: () -> Unit, // Add this parameter
    modifier: Modifier = Modifier,
    profileImage: @Composable (() -> Unit)? = null // Optional profile image composable
) {
    val coroutineScope = rememberCoroutineScope()
    val signInController = SignInController(SupabaseClient().authRepository)

    TopAppBar(
        backgroundColor = Color.White,
        elevation = 4.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tabs on the left
            TabRow(
                selectedTabIndex = selectedTabIndex,
                backgroundColor = Color.White, // Set background color to white
                contentColor = Color.Black,
                indicator = { },
                modifier = Modifier.weight(1f) // Take up remaining space
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { onTabSelected(0) },
                    text = { Text("Cold Email Generation") }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { onTabSelected(1) },
                    text = { Text("Job Application Progress") }
                )
                Tab(
                    selected = selectedTabIndex == 2,
                    onClick = { onTabSelected(2) },
                    text = { Text("Documents") }
                )
                Tab(
                    selected = selectedTabIndex == 3,
                    onClick = { onTabSelected(3) },
                    text = { Text("Profile") }
                )
            }

            Button(
                onClick = {
                    coroutineScope.launch {
                        runBlocking {
                            signInController.logoutUser()
                        }
                        NavigateToLogin() // Navigate to login after sign out
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.White, // White background
                    contentColor = Color(0xFF487896) // Text color
                ),
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp) // Add spacing from the edges
            ) {
                Text("Sign Out", fontWeight = FontWeight.Bold)
            }

            // Profile Image on the right (if provided)
            profileImage?.let {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE2E2E2))
                        .border(1.dp, Color.LightGray, CircleShape)
                ) {
                    it()
                }
            }
        }
    }
}
