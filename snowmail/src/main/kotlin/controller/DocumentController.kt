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
//    Bucket Structure:
//    - user_documents
//        - user_id (folder)
//            - resume
//                - resume.pdf
//            - cover_letter
//                - cover_letter.pdf
//            - transcript
//                - transcript.pdf
//            - other
//                - other.pdf

    val dbStorage = SupabaseClient()
    val documentController = DocumentController(dbStorage.documentRepository)

    val bucket = "user_documents"
    val path = "test/resume/test-resume.pdf"
    val file = File(System.getProperty("user.home") + "/Desktop/test-resume.pdf")

    // Call uploadDocument and print the result
    val result = documentController.uploadDocument(bucket, path, file)
    result.onSuccess {
        println("Upload successful: $it")
    }.onFailure { error ->
        println("Error uploading document: ${error.message}")
    }

    val deleteResult = documentController.deleteDocument(bucket, path)
    deleteResult.onSuccess {
        println("Delete successful: $it")
    }.onFailure { error ->
        println("Error deleting document: ${error.message}")
    }

}
