package integration

import ca.uwaterloo.persistence.AuthRepository
import ca.uwaterloo.persistence.DocumentRepository
import ca.uwaterloo.persistence.UserProfileRepository
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.status.SessionSource
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Bucket
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import io.ktor.http.ContentDisposition.Companion.File
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import java.io.File

class SupabaseClient {
    private val supabase = createSupabaseClient(
        supabaseUrl = "https://gwnlngyvkxdpodenpyyj.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imd3bmxuZ3l2a3hkcG9kZW5weXlqIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mjc5MTAxNTEsImV4cCI6MjA0MzQ4NjE1MX0.olncAUMxSOjcr0YjssWXThtXDXC3q4zasdNYdwavt8g"
    ) {
        install(Postgrest)
        install(Auth)
        install(Storage)
    }
    val authRepository = AuthRepository(supabase)
    val userProfileRepository = UserProfileRepository(supabase)
    val documentRepository = DocumentRepository(supabase)

    suspend fun retrieveBuckets(): List<Bucket> {
        return supabase.storage.retrieveBuckets()
    }
}

//
// just for testing,
// uncomment any of them when you want to test a single func
//
fun main() = runBlocking<Unit> {
    val dbStorage = SupabaseClient()

    // val buckets = dbStorage.storage.retrieveBuckets()

    val bucket = "user_documents"
    val path = "test-resume.pdf"
    val file = File(System.getProperty("user.home") + "/Desktop/test-resume.pdf")

    // Test uploading a document
    val uploadResult = dbStorage.documentRepository.uploadDocument(bucket, path, file)
    uploadResult.onSuccess {
        println("Upload successful: $it")
    }.onFailure { error ->
        println("Error uploading document: ${error.message}")
    }

//    // Test downloading a document
//    val downloadResult = dbStorage.documentRepository.downloadDocument(bucket, path)
//    downloadResult.onSuccess { fileContent ->
//        println("Download successful, file size: ${fileContent.size} bytes")
//    }.onFailure { error ->
//        println("Error downloading document: ${error.message}")
//    }
//
//    // Test deleting a document
//    val deleteResult = dbStorage.documentRepository.deleteDocument(bucket, path)
//    deleteResult.onSuccess {
//        println("Delete successful: $it")
//    }.onFailure { error ->
//        println("Error deleting document: ${error.message}")
//    }
}