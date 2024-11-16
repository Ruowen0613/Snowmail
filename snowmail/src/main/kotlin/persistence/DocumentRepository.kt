package ca.uwaterloo.persistence

import ca.uwaterloo.model.Document
import ca.uwaterloo.model.Education
import ca.uwaterloo.model.WorkExperience
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
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class DocumentRepository(private val supabase: SupabaseClient) : IDocumentRepository {

    private val storage = supabase.storage

    private fun mapIntToDocumentType(documentType: Int): String {
        return when (documentType) {
            1 -> "resume"
            2 -> "cover_letter"
            3 -> "transcript"
            4 -> "other"
            else -> "unknown"
        }
    }

    override suspend fun uploadDocument(
        userId: String,
        documentType: Int,
        documentName: String,
        bucket: String,
        file: File
    ): Result<String> {
        return try {
            val documentTypeString = mapIntToDocumentType(documentType)
            val encodedDocumentName = URLEncoder.encode(documentName, StandardCharsets.UTF_8.toString())

            println("Document name: $encodedDocumentName")

            val path = "$userId/$documentTypeString/$encodedDocumentName"
            if (!file.exists()) {
                return Result.failure(Exception("File does not exist: ${file.path}"))
            }
            val fileContent = file.readBytes()
            storage.from(bucket).upload(path, fileContent)
            val insertResult = addDocumentRecord(userId, documentType, documentName, bucket, path)
            if (insertResult.isFailure) {
                return Result.failure(Exception("Error inserting document metadata: ${insertResult.exceptionOrNull()?.message}"))
            }
            Result.success("Document uploaded and metadata inserted successfully.")
        } catch (e: Exception) {
            Result.failure(Exception("Error uploading document: ${e.message}"))
        }
    }

    private suspend fun addDocumentRecord(
        userId: String,
        documentType: Int,
        documentName: String,
        bucket: String,
        path: String,
    ): Result<Boolean> {
        return try {
            val document = Document(
                userId = userId,
                documentType = documentType,
                documentName = documentName,
                bucket = bucket,
                path = path,
            )
            supabase.from("document").insert(document)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to add document: ${e.message}"))
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

    override suspend fun deleteDocument(
        userId: String,
        documentType: Int,
        documentName: String,
        bucket: String,
    ): Result<String> {
        return try {
            // Remove the document record first
            val removeResult = removeDocumentRecord(userId, documentType, documentName)
            if (removeResult.isFailure) {
                return Result.failure(Exception("Error removing document record: ${removeResult.exceptionOrNull()?.message}"))
            }

            // If record removal is successful, delete the document from storage
            val documentTypeString = mapIntToDocumentType(documentType)
            val encodedDocumentName = URLEncoder.encode(documentName, StandardCharsets.UTF_8.toString())
            val path = "$userId/$documentTypeString/$encodedDocumentName"
            storage.from(bucket).delete(path)
            Result.success("Document record removed and document deleted successfully.")
        } catch (e: Exception) {
            Result.failure(Exception("Error deleting document: ${e.message}"))
        }
    }

    override suspend fun removeDocumentRecord(userId: String, documentType: Int, documentName: String): Result<Boolean> {
        return try {
            // Check if the document exists
            val existingDocument = supabase.from("document")
                .select {
                    filter {
                        eq("user_id", userId)
                        eq("document_type", documentType)
                        eq("document_name", documentName)
                    }
                }
                .decodeSingleOrNull<Document>()

            // If exists, delete it
            if (existingDocument != null) {
                supabase.from("document")
                    .delete {
                        filter {
                            eq("user_id", userId)
                            eq("document_name", documentName)
                        }
                    }
                Result.success(true)
            } else {
                // If not exists, return failure
                Result.failure(Exception("Document not found"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Failed to delete document: ${e.message}"))
        }
    }



    override suspend fun createSignedUrl(bucket: String, path: String): Result<String> {
        return try {
            val url = storage.from(bucket).createSignedUrl(path, 30.minutes)
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

    override suspend fun getDocuments(userId: String, documentType: Int): Result<List<Document>> {
        return try {
            val documents = supabase.from("document")
                .select {
                    filter {
                        eq("user_id", userId)
                        eq("document_type", documentType)
                    }
                }
                .decodeList<Document>()
            Result.success(documents)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to fetch documents: ${e.message}"))
        }
    }

}
