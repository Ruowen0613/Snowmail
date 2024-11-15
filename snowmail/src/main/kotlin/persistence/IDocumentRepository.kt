package ca.uwaterloo.persistence

import java.io.File

interface IDocumentRepository {
    suspend fun uploadDocument(bucket: String, path: String, file: File): Result<String>
    suspend fun downloadDocument(bucket: String, path: String): Result<ByteArray>
    suspend fun deleteDocument(bucket: String, path: String): Result<String>
    suspend fun createSignedUrl(bucket: String, path: String): Result<String>
    suspend fun listDocuments(bucket: String, path: String): Result<List<String>>
}
