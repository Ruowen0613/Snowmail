import ca.uwaterloo.controller.DocumentController
import integration.SupabaseClient
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Paths

class DocumentControllerTest {

    private val dbStorage = SupabaseClient()
    private val documentController = DocumentController(dbStorage.documentRepository)

    @Test
    fun testUploadDocument() = runBlocking {
        val resource = this::class.java.classLoader.getResource("test-resume.pdf")
        assertNotNull(resource, "Resource file not found: test-resume.pdf")

        val file = Paths.get(resource!!.toURI()).toFile()
        val bucket = "user_documents"
        val userId = "test"
        val documentType = "resume"
        val documentName = "test-resume.pdf"

        val result = documentController.uploadDocument(bucket, userId, documentType, documentName, file)
        assertTrue(result.isSuccess, "Document upload failed: ${result.exceptionOrNull()?.message}")
    }

    @Test
    fun testDownloadDocument() = runBlocking {
        val bucket = "user_documents"
        val userId = "test"
        val documentType = "resume"
        val documentName = "test-resume.pdf"

        val result = documentController.downloadDocument(bucket, userId, documentType, documentName)
        assertTrue(result.isSuccess, "Document download failed: ${result.exceptionOrNull()?.message}")
        assertNotNull(result.getOrNull(), "Downloaded document content is null")
    }

    @Test
    fun testDeleteDocument() = runBlocking {
        val bucket = "user_documents"
        val userId = "test"
        val documentType = "resume"
        val documentName = "test-resume.pdf"
        val file = Paths.get(javaClass.classLoader.getResource("test-resume.pdf")!!.toURI()).toFile()

        // Upload the document first
        val uploadResult = documentController.uploadDocument(bucket, userId, documentType, documentName, file)
        assertTrue(uploadResult.isSuccess, "Document upload failed: ${uploadResult.exceptionOrNull()?.message}")

        // Delete the document
        val deleteResult = documentController.deleteDocument(bucket, userId, documentType, documentName)
        assertTrue(deleteResult.isSuccess, "Document deletion failed: ${deleteResult.exceptionOrNull()?.message}")

        // Verify the document is deleted
        val downloadResult = documentController.downloadDocument(bucket, userId, documentType, documentName)
        assertTrue(downloadResult.isFailure, "Document still exists after deletion.")
    }

    @Test
    fun testCreateSignedUrl() = runBlocking {
        val bucket = "user_documents"
        val userId = "test"
        val documentType = "resume"
        val documentName = "test-resume.pdf"

        val result = documentController.viewDocument(bucket, userId, documentType, documentName)
        assertTrue(result.isSuccess, "Creating signed URL failed: ${result.exceptionOrNull()?.message}")
        assertNotNull(result.getOrNull(), "Signed URL is null")
    }

    @Test
    fun testViewDocument() = runBlocking {
        val bucket = "user_documents"
        val userId = "test"
        val documentType = "resume"
        val documentName = "test-resume.pdf"

        val result = documentController.viewDocument(bucket, userId, documentType, documentName)
        assertTrue(result.isSuccess, "Viewing document failed: ${result.exceptionOrNull()?.message}")
        assertNotNull(result.getOrNull(), "Signed URL is null")
    }
}
