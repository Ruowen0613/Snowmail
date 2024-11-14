package ca.uwaterloo.controller

import integration.OpenAIClient
import io.ktor.client.*
import io.ktor.client.engine.cio.*

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper

import java.nio.file.Files
import java.nio.file.Paths
import kotlinx.coroutines.runBlocking
import java.io.File

class ResumeParserController(private val openAIClient: OpenAIClient) {

    // Function to parse resume without HTTP endpoint
    suspend fun parseResume(fileBytes: ByteArray): Map<String, Any> {
        val resumeText = String(fileBytes)
        return openAIClient.parseResume(resumeText)
    }

    fun extractTextFromPDF(file: File): String {
        PDDocument.load(file).use { document ->
            return PDFTextStripper().getText(document)
        }
    }
}

suspend fun main() {
    val openAIClient = OpenAIClient(HttpClient(CIO))
    val resumeParserController = ResumeParserController(openAIClient)

    // Read the resume file
    // val path = Paths.get("src/test/resources/test-resume.pdf")

    val path = Paths.get(System.getProperty("user.home") + "/Desktop/resume-external.pdf")
    val file = path.toFile()

    // Extract text from the PDF
    val resumeText = resumeParserController.extractTextFromPDF(file)

    println(resumeText)

    // Parse the resume
    runBlocking {
        val parsedResume = resumeParserController.parseResume(resumeText.toByteArray())
        println(parsedResume)
    }
}




