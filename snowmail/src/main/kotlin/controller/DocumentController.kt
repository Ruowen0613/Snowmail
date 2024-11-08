package ca.uwaterloo.controller

import ca.uwaterloo.persistence.IDocumentRepository
import integration.SupabaseClient
import kotlinx.coroutines.runBlocking
import java.io.File

class DocumentController(private val documentRepository: IDocumentRepository) {

    suspend fun uploadDocument(bucket: String, path: String, file: File): Result<String> {
        return documentRepository.uploadDocument(bucket, path, file)
    }

    suspend fun downloadDocument(bucket: String, path: String): Result<ByteArray> {
        return documentRepository.downloadDocument(bucket, path)
    }

    suspend fun deleteDocument(bucket: String, path: String): Result<String> {
        return documentRepository.deleteDocument(bucket, path)
    }
}

fun main() = runBlocking<Unit> {
    val dbStorage = SupabaseClient()
    val documentUploadController = DocumentController(dbStorage.documentRepository)

    val bucket = "user_documents"
    val path = "test-resume.pdf"
    val file = File(System.getProperty("user.home") + "/Desktop/test-resume.pdf")

    // Call uploadDocument and print the result
    val result = documentUploadController.uploadDocument(bucket, path, file)
    result.onSuccess {
        println("Upload successful: $it")
    }.onFailure { error ->
        println("Error uploading document: ${error.message}")
    }
}
