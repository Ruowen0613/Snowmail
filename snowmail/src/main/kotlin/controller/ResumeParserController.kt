package ca.uwaterloo.controller

import integration.OpenAIClient
import io.ktor.client.*
import io.ktor.client.engine.cio.*

import java.nio.file.Files
import java.nio.file.Paths
import kotlinx.coroutines.runBlocking

class ResumeParserController(private val openAIClient: OpenAIClient) {

    // Function to parse resume without HTTP endpoint
    suspend fun parseResume(fileBytes: ByteArray): Map<String, Any> {
        val resumeText = String(fileBytes)
        return openAIClient.parseResume(resumeText)
    }
}


suspend fun main() {
    val openAIClient = OpenAIClient(HttpClient(CIO))
    val resumeParserController = ResumeParserController(openAIClient)

    // Read the resume file
    val path = Paths.get("src/test/resources/test-resume.pdf")
    val fileBytes = Files.readAllBytes(path)

    // Parse the resume
    runBlocking {
        val parsedResume = resumeParserController.parseResume(fileBytes)
        println(parsedResume)
    }
}




