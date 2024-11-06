package ca.uwaterloo.persistence

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import java.io.File
import java.io.InputStream

class DocumentRepository(private val supabase: SupabaseClient) {

    private val storage = supabase.storage

    suspend fun uploadDocument(bucket: String, path: String, file: File): Result<String> {
        return try {
            val fileContent = file.readBytes()
            storage.from(bucket).upload(path, fileContent)
            Result.success("Document uploaded successfully.")
        } catch (e: Exception) {
            Result.failure(Exception("Error uploading document: ${e.message}"))
        }
    }

    suspend fun downloadDocument(bucket: String, path: String): Result<ByteArray> {
        return try {
            val fileContent = storage.from(bucket).downloadAuthenticated(path)
            Result.success(fileContent)
        } catch (e: Exception) {
            Result.failure(Exception("Error downloading document: ${e.message}"))
        }
    }

    suspend fun deleteDocument(bucket: String, path: String): Result<String> {
        return try {
            storage.from(bucket).delete(path)
            Result.success("Document deleted successfully.")
        } catch (e: Exception) {
            Result.failure(Exception("Error deleting document: ${e.message}"))
        }
    }
}