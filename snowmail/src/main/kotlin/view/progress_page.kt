package ca.uwaterloo.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*

@Composable
fun JobProgressPage(NavigateToDocuments: () -> Unit, NavigateToProfile: () -> Unit,
                    NavigateToEmialGen: () -> Unit) {

    var selectedTabIndex by remember { mutableStateOf(1) }

    MaterialTheme {
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
                        1 -> {}
                        2 -> navigateOtherPage(NavigateToDocuments)
                        3 -> navigateOtherPage(NavigateToProfile)
                    }
                }
            )


            //SearchBar()

            Spacer(modifier = Modifier.height(16.dp))
            JobStatusColumns()
        }
    }
}

@Composable
fun SearchBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Search for roles or companies") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun JobStatusColumns() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        JobStatusColumn("APPLIED", 10, modifier = Modifier.weight(1f))
        JobStatusColumn("INTERVIEWING", 2, modifier = Modifier.weight(1f))
        JobStatusColumn("OFFER", 1, modifier = Modifier.weight(1f))
        JobStatusColumn("OTHER", 0, modifier = Modifier.weight(1f))
    }
}

@Composable
fun JobStatusColumn(title: String, itemCount: Int, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Text(
            text = "$title ($itemCount)",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxHeight()
        ) {
            if (itemCount > 0) {
                items(itemCount) { index ->
                    JobCard(
                        position = "Job Title",
                        company = "Company Name",
                        location = "Location"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            } else {
                // Placeholder if no job cards are present
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .background(Color(0xFFD3D3D3), shape = RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No applications",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun JobCard(position: String, company: String, location: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Text(text = position, fontWeight = FontWeight.Bold, fontSize = 14.sp)

        Spacer(modifier = Modifier.height(8.dp))

        // Row for company with business icon
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Home,
                contentDescription = "Company Icon",
                tint = Color.Gray,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = company, color = Color.Gray, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Row for location with location icon
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = "Location Icon",
                tint = Color.Gray,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = location, color = Color.Gray, fontSize = 12.sp)
        }
    }
}

// For Test Purpose
//fun main() = application {
//    Window(onCloseRequest = ::exitApplication, title = "Job Application Progress") {
//        JobProgressPage()
//    }
//}