package ca.uwaterloo.persistence

import kotlinx.datetime.LocalDate
import java.io.File

interface IDocumentRepository {
    suspend fun uploadDocument(userId: String,
                               documentType: Int,
                               documentName: String,
                               bucket: String,
                               uploadedAt: LocalDate,
                               file: File): Result<String>
    suspend fun downloadDocument(bucket: String, path: String): Result<ByteArray>
    suspend fun deleteDocument(bucket: String, path: String): Result<String>
    suspend fun createSignedUrl(bucket: String, path: String): Result<String>
    suspend fun listDocuments(bucket: String, path: String): Result<List<String>>
}
