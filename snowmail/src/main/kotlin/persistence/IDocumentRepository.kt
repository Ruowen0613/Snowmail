package ca.uwaterloo.persistence

import ca.uwaterloo.model.Document
import java.io.File

interface IDocumentRepository {
    suspend fun uploadDocument(userId: String,
                               documentType: Int,
                               documentName: String,
                               bucket: String,
                               file: File): Result<String>
    suspend fun downloadDocument(bucket: String, path: String): Result<ByteArray>
    suspend fun deleteDocument(
        userId: String,
        documentType: Int,
        documentName: String,
        bucket: String,
    ): Result<String>
    suspend fun removeDocumentRecord(userId: String, documentType: Int, documentName: String): Result<Boolean>
    suspend fun createSignedUrl(bucket: String, path: String): Result<String>
    suspend fun listDocuments(bucket: String, path: String): Result<List<String>>
    suspend fun getDocuments(bucket: String, documentType: Int): Result<List<Document>>
}
