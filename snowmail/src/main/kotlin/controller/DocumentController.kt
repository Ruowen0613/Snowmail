package ca.uwaterloo.controller

import ca.uwaterloo.model.Document
import ca.uwaterloo.persistence.IDocumentRepository
import integration.SupabaseClient
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import java.awt.Desktop
import java.io.File
import java.net.URI
import java.nio.file.Files
import java.nio.file.Paths

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class DocumentController(private val documentRepository: IDocumentRepository) {

    suspend fun uploadDocument(
        userId: String,
        documentType: Int,
        documentName: String,
        bucket: String,
        file: File
    ): Result<String> {
        return documentRepository.uploadDocument(userId, documentType, documentName, bucket, file)
    }

    suspend fun downloadDocument(bucket: String, path: String): Result<ByteArray> {
        return documentRepository.downloadDocument(bucket, path)
    }

    suspend fun deleteDocument(userId: String, documentType: Int, documentName: String, bucket: String): Result<String> {
        return documentRepository.deleteDocument(userId, documentType, documentName, bucket)
    }

    suspend fun viewDocument(bucket: String, path: String): Result<String> {
        return documentRepository.createSignedUrl(bucket, path)
    }

    // DO NOT USE THIS FUNCTION
//    suspend fun listDocuments(bucket: String, userId: String, documentType: String): Result<List<String>> {
//        val path = "$userId/$documentType"
//        return documentRepository.listDocuments(bucket, path)
//    }

    suspend fun getDocuments(userId: String, documentType: Int): Result<List<Document>> {
        return documentRepository.getDocuments(userId, documentType)
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
    val userId = "c9498eec-ac17-4a3f-8d91-61efba3f7277"
    val bucket = "user_documents"
    val documentType = 4
    val documentName = "Test Resume Novemeber 15"
    val uploadedAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val expiresInMinutes = 10
    val file = File(System.getProperty("user.home") + "/Desktop/test-resume.pdf")


//    val result_upload = documentController.uploadDocument(userId, documentType, documentName, bucket, file)
//    result_upload.onSuccess {
//        println("Upload successful: $it")
//    }.onFailure { error ->
//        println("Error uploading document: ${error.message}")
//    }
//
//
//    val result_get = documentController.getDocuments(userId, documentType)
//    result_get.onSuccess {
//        println("Getting documents successful: $it")
//    }.onFailure { error ->
//        println("Error getting document: ${error.message}")
//    }

//    val result_delete = documentController.deleteDocument(userId, documentType, documentName, bucket)
//    result_delete.onSuccess {
//        println("Delete successful: $it")
//    }.onFailure { error ->
//        println("Error deleting document: ${error.message}")
//    }

//    val result_view = documentController.viewDocument(bucket, "c9498eec-ac17-4a3f-8d91-61efba3f7277/other/test+resume+6")
//    result_view.onSuccess {
//        println("View successful: $it")
//    }.onFailure { error ->
//        println("Error viewing document: ${error.message}")
//    }

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

