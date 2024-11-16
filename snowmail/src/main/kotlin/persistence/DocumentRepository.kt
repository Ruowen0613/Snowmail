package ca.uwaterloo.persistence

import ca.uwaterloo.model.Document
import ca.uwaterloo.model.Education
import ca.uwaterloo.view.UserSession.userId
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.io.File
import java.io.InputStream
import kotlin.time.Duration.Companion.minutes

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject


class DocumentRepository(private val supabase: SupabaseClient) : IDocumentRepository {

    private val storage = supabase.storage

    override suspend fun uploadDocument(
        userId: String,
        documentType: Int,
        documentName: String,
        bucket: String,
        uploadedAt: LocalDate,
        file: File
    ): Result<String> {
        return try {
            val path = "$userId/$documentType/$documentName"
            if (!file.exists()) {
                return Result.failure(Exception("File does not exist: ${file.path}"))
            }
            val fileContent = file.readBytes()
            storage.from(bucket).upload(path, fileContent)
            val insertResult = addDocument(userId, documentType, documentName, bucket, path, uploadedAt)
            if (insertResult.isFailure) {
                return Result.failure(Exception("Error inserting document metadata: ${insertResult.exceptionOrNull()?.message}"))
            }
            Result.success("Document uploaded and metadata inserted successfully.")
        } catch (e: Exception) {
            Result.failure(Exception("Error uploading document: ${e.message}"))
        }
    }

    private suspend fun addDocument(
        userId: String,
        documentType: Int,
        documentName: String,
        bucket: String,
        path: String,
        uploadedAt: LocalDate,
    ): Result<Boolean> {
        return try {
            val document = Document(
                userId = userId,
                documentType = documentType,
                documentName = documentName,
                bucket = bucket,
                path = path,
                uploadedAt = uploadedAt
            )
            supabase.from("document").insert(document)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to add education: ${e.message}"))
        }
    }

    override suspend fun downloadDocument(bucket: String, path: String): Result<ByteArray> {
        return try {
            val fileContent = storage.from(bucket).downloadAuthenticated(path)
            Result.success(fileContent)
        } catch (e: Exception) {
            Result.failure(Exception("Error downloading document: ${e.message}"))
        }
    }

    override suspend fun deleteDocument(bucket: String, path: String): Result<String> {
        return try {
            storage.from(bucket).delete(path)
            Result.success("Document deleted successfully.")
        } catch (e: Exception) {
            Result.failure(Exception("Error deleting document: ${e.message}"))
        }
    }

    override suspend fun createSignedUrl(bucket: String, path: String): Result<String> {
        return try {
            val url = storage.from(bucket).createSignedUrl(path, 5.minutes)
            Result.success(url)
        } catch (e: Exception) {
            Result.failure(Exception("Error creating signed URL: ${e.message}"))
        }
    }

    override suspend fun listDocuments(bucket: String, path: String): Result<List<String>> {
        return try {
            val files = storage.from(bucket).list(path)
            val fileNames = files.map { it.name }
            Result.success(fileNames)
        } catch (e: Exception) {
            Result.failure(Exception("Error listing documents: ${e.message}"))
        }
    }

}
