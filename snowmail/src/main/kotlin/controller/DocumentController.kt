package ca.uwaterloo.controller

import ca.uwaterloo.persistence.IDocumentRepository
import integration.SupabaseClient
import kotlinx.coroutines.runBlocking
import java.awt.Desktop
import java.io.File
import java.net.URI
import java.nio.file.Files
import java.nio.file.Paths

class DocumentController(private val documentRepository: IDocumentRepository) {

    suspend fun uploadDocument(
        userId: String,
        documentType: String,
        documentName: String,
        bucket: String,
        file: File
    ): Result<String> {
        return documentRepository.uploadDocument(bucket, path, file)
    }

    suspend fun downloadDocument(bucket: String, userId: String, documentType: String, documentName: String): Result<ByteArray> {
        val path = "$userId/$documentType/$documentName"
        return documentRepository.downloadDocument(bucket, path)
    }

    suspend fun deleteDocument(bucket: String, userId: String, documentType: String, documentName: String): Result<String> {
        val path = "$userId/$documentType/$documentName"
        return documentRepository.deleteDocument(bucket, path)
    }

    suspend fun viewDocument(bucket: String, userId: String, documentType: String, documentName: String): Result<String> {
        val path = "$userId/$documentType/$documentName"
        return documentRepository.createSignedUrl(bucket, path)
    }

    suspend fun listDocuments(bucket: String, userId: String, documentType: String): Result<List<String>> {
        val path = "$userId/$documentType"
        return documentRepository.listDocuments(bucket, path)
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
//
//    val bucket = "user_documents"
//    val path = "test/Q6-2.jpg"
//    val file = File(System.getProperty("user.home") + "/Desktop/Q6-2.jpg")
//
//    // Call uploadDocument and print the result
//    val result = documentController.uploadDocument(bucket, "test", "other", "Q6-2", file)
//    result.onSuccess {
//        println("Upload successful: $it")
//    }.onFailure { error ->
//        println("Error uploading document: ${error.message}")
//    }

//    val documentController = DocumentController(SupabaseClient().documentRepository)
//    val bucket = "user_documents"
//    val userId = "test"
//    val documentType = "other"
//    val documentName = "Q6-2"
//
//    val result = documentController.downloadAndViewDocument(bucket, userId, documentType, documentName)
//    result.onSuccess { file ->
//        println("Document downloaded and saved to: ${file.absolutePath}")
//        // Open the file using the default viewer
//        if (Desktop.isDesktopSupported()) {
//            Desktop.getDesktop().open(file)
//        } else {
//            println("Desktop is not supported. Please open the file manually.")
//        }
//    }.onFailure { error ->
//        println("Error downloading document: ${error.message}")
//    }


    val documentController = DocumentController(SupabaseClient().documentRepository)
    val bucket = "user_documents"
    val userId = "test"
    val documentType = "other"
    val documentName = "test-resume-2"
    val expiresInMinutes = 10
    val file = File(System.getProperty("user.home") + "/Desktop/test-resume.pdf")

    val result = documentController.uploadDocument(bucket, "test", "other", documentName, file)
    result.onSuccess {
        println("Upload successful: $it")
    }.onFailure { error ->
        println("Error uploading document: ${error.message}")
    }

//    val result = documentController.createSignedUrl(bucket, userId, documentType, documentName)
//    result.onSuccess { url ->
//        println("Signed URL: $url")
//    }.onFailure { error ->
//        println("Error creating signed URL: ${error.message}")
//    }

//    val result = documentController.listDocuments(bucket, userId, documentType)
//    result.onSuccess { documents ->
//        println("Documents in $bucket/$userId/$documentType:")
//        documents.forEach { document ->
//            println(document)
//        }
//    }.onFailure { error ->
//        println("Error listing documents: ${error.message}")
//    }

//    val result = documentController.viewDocument(bucket, userId, documentType, documentName)
//    result.onSuccess { url ->
//        println("Signed URL: $url")
//        // Open the URL in the default browser
//        if (Desktop.isDesktopSupported()) {
//            Desktop.getDesktop().browse(URI(url))
//        } else {
//            println("Desktop is not supported. Please open the URL manually.")
//        }
//    }.onFailure { error ->
//        println("Error creating signed URL: ${error.message}")
//    }




}

