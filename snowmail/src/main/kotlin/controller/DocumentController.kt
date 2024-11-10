package ca.uwaterloo.controller

import ca.uwaterloo.persistence.IDocumentRepository
import integration.SupabaseClient
import kotlinx.coroutines.runBlocking
import java.io.File
import java.nio.file.Paths

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

//    val bucket = "user_documents"
//    val path = "Q6-2.jpg"
//    val file = File(System.getProperty("user.home") + "/Desktop/Q6-2.jpg")
//
//    // Call uploadDocument and print the result
//    val result = documentController.uploadDocument(bucket, path, file)
//    result.onSuccess {
//        println("Upload successful: $it")
//    }.onFailure { error ->
//        println("Error uploading document: ${error.message}")
//    }


//    val bucket = "user_documents"
//    val path = "test-resume.pdf"
//    // val resource = DocumentController::class.java.classLoader.getResource("test-resume.pdf")
//    val resource = this::class.java.classLoader.getResource("test-resume.pdf")
//
//
//    println(resource)
//
//    if (resource != null) {
//        val file = Paths.get(resource.toURI()).toFile()
//
//        // Call uploadDocument and print the result
//        val uploadResult = documentController.uploadDocument(bucket, path, file)
//        uploadResult.onSuccess {
//            println("Upload successful: $it")
//        }.onFailure { error ->
//            println("Error uploading document: ${error.message}")
//        }
//    } else {
//        println("Resource file not found: test-resume.pdf")
//    }

}

//    val deleteResult = documentController.deleteDocument(bucket, path)
//    deleteResult.onSuccess {
//        println("Delete successful: $it")
//    }.onFailure { error ->
//        println("Error deleting document: ${error.message}")
//    }

