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

//    @Test
//    fun testUploadDocument() = runBlocking {
//        val resource = this::class.java.classLoader.getResource("test-resume.pdf")
//        println(resource) // Should print the URL if found
//
//        if (resource != null) {
//            val file = Paths.get(resource.toURI()).toFile()
//
//            println(file)
//            val bucket = "user_documents"
//            val path = "test-resume.pdf"
//
//            val uploadResult = documentController.uploadDocument(bucket, path, file)
//            assertTrue(uploadResult.isSuccess, "Document upload failed: ${uploadResult.exceptionOrNull()?.message}")
//        } else {
//            fail("Resource file not found: test-resume.pdf")
//        }
//    }

//    @Test
//    fun testDownloadDocument() = runBlocking {
//        val bucket = "user_documents"
//        val path = "test/resume/test-resume.pdf"
//
//        val result = documentController.downloadDocument(bucket, path)
//        assertTrue(result.isSuccess, "Document download failed: ${result.exceptionOrNull()?.message}")
//        assertNotNull(result.getOrNull(), "Downloaded document content is null")
//    }
//
//    @Test
//    fun testDeleteDocument() = runBlocking {
//        val bucket = "user_documents"
//        val path = "test/resume/test-resume.pdf"
//        val file = Paths.get(javaClass.classLoader.getResource("test-resume.pdf")!!.toURI()).toFile()
//
//        // Upload the document first
//        val uploadResult = documentController.uploadDocument(bucket, path, file)
//        assertTrue(uploadResult.isSuccess, "Document upload failed: ${uploadResult.exceptionOrNull()?.message}")
//
//        // Delete the document
//        val deleteResult = documentController.deleteDocument(bucket, path)
//        assertTrue(deleteResult.isSuccess, "Document deletion failed: ${deleteResult.exceptionOrNull()?.message}")
//
//        // Verify the document is deleted
//        val downloadResult = documentController.downloadDocument(bucket, path)
//        assertTrue(downloadResult.isFailure, "Document still exists after deletion.")
//    }
}
