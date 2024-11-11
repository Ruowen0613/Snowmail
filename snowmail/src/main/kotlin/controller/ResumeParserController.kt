package ca.uwaterloo.controller

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import java.io.File

class ResumeParserController(private val resumeParserService: ResumeParserService) {

    // Function to parse resume without HTTP endpoint
    suspend fun parseResume(fileBytes: ByteArray): Map<String, Any> {
        return resumeParserService.parseResume(fileBytes)
    }
}


