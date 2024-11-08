import ca.uwaterloo.controller.DocumentController
import integration.SupabaseClient
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File

class DocumentControllerTest {

    private val dbStorage = SupabaseClient()
    private val documentController = DocumentController(dbStorage.documentRepository)

    @Test
    fun testUploadDocument() = runBlocking {
        val bucket = "user_documents"
        val path = "test/resume/test-resume.pdf"
        val file = File(System.getProperty("user.home") + "/Desktop/test-resume.pdf")

        val result = documentController.uploadDocument(bucket, path, file)
        assertTrue(result.isSuccess, "Document upload failed: ${result.exceptionOrNull()?.message}")
    }

    @Test
    fun testDownloadDocument() = runBlocking {
        val bucket = "user_documents"
        val path = "test/resume/test-resume.pdf"

        val result = documentController.downloadDocument(bucket, path)
        assertTrue(result.isSuccess, "Document download failed: ${result.exceptionOrNull()?.message}")
        assertNotNull(result.getOrNull(), "Downloaded document content is null")
    }

    @Test
    fun testDeleteDocument() = runBlocking {
        val bucket = "user_documents"
        val path = "test/resume/test-resume.pdf"
        val file = File(System.getProperty("user.home") + "/Desktop/test-resume.pdf")

        // Upload the document first
        val uploadResult = documentController.uploadDocument(bucket, path, file)
        assertTrue(uploadResult.isSuccess, "Document upload failed: ${uploadResult.exceptionOrNull()?.message}")

        // Delete the document
        val deleteResult = documentController.deleteDocument(bucket, path)
        assertTrue(deleteResult.isSuccess, "Document deletion failed: ${deleteResult.exceptionOrNull()?.message}")

        // Verify the document is deleted
        val downloadResult = documentController.downloadDocument(bucket, path)
        assertTrue(downloadResult.isFailure, "Document still exists after deletion.")
    }
}
