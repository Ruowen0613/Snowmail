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

    suspend fun retrieveBuckets(): Result<List<Bucket>> {
        return try {
            val buckets = supabase.storage.retrieveBuckets()
            Result.success(buckets)
        } catch (e: Exception) {
            Result.failure(Exception("Error retrieving buckets: ${e.message}"))
        }
    }
}

fun main() = runBlocking<Unit> {
    val dbStorage = SupabaseClient()

    val email = "wrw040613@gmail.com"
    val password = "Wrw54321"
    val firstname = "Cherry"
    val lastname = "Wang"


    val userId = "c9498eec-ac17-4a3f-8d91-61efba3f7277"

//    val bucketResult = dbStorage.retrieveBuckets()
//    bucketResult.onSuccess { buckets ->
//        println("Buckets: $buckets")
//    }.onFailure { error ->
//        println("Error retrieving buckets: ${error.message}")
//    }
//
//    val bucket = "user_documents"
//    val path = "Q6-2.jpg"
//    val file = File(System.getProperty("user.home") + "/Desktop/Q6-2.jpg")

    // Test uploading a document
    // val uploadResult = dbStorage.documentRepository.uploadDocument(bucket, path, file)

//    val deleteResult = dbStorage.documentRepository.deleteDocument(bucket, path)
//    deleteResult.onSuccess {
//        println("Delete successful: $it")
//
//        // Verify deletion
//        val verifyResult = dbStorage.documentRepository.downloadDocument(bucket, path)
//        verifyResult.onSuccess {
//            println("Document still exists after deletion.")
//        }.onFailure { error ->
//            println("Document successfully deleted: ${error.message}")
//        }
//    }.onFailure { error ->
//        println("Error deleting document: ${error.message}")
//    }

//    val uploadResult = dbStorage.documentRepository.uploadDocument(bucket, path, file)
//    uploadResult.onSuccess {
//        println("Upload successful: $it")
//
//        // Delete the document after uploading
//        val deleteResult = dbStorage.documentRepository.deleteDocument(bucket, path)
//        deleteResult.onSuccess {
//            println("Delete successful: $it")
//        }.onFailure { error ->
//            println("Error deleting document: ${error.message}")
//        }
//    }.onFailure { error ->
//        println("Error uploading document: ${error.message}")
//    }

    //test get user's skills
//    val profileResult = dbStorage.userProfileRepository.getSkills(userId)
//    profileResult.onSuccess { skills ->
//        println("skills: $skills")
//    }.onFailure { error ->
//        println("Error fetching user profile: ${error.message}")
//    }

    //test update user's skills
//    val skills = listOf("Java", "Kotlin", "Python")
//    val result = dbStorage.userProfileRepository.updateSkills(userId, skills)
//    result.onSuccess {
//        println("User profile updated successfully.")
//    }.onFailure { error ->
//        println("Error updating user profile: ${error.message}")
//    }

    //test delete education
//    val educationId = "10"
//    val result = dbStorage.userProfileRepository.deleteEducation(educationId)
//    result.onSuccess {
//        println("Education deleted successfully.")
//    }.onFailure { error ->
//        println("Error deleting education: ${error.message}")
//    }

    //test delete work experience
//    val workExperienceId = "100"
//    val result = dbStorage.userProfileRepository.deleteWorkExperience(workExperienceId)
//    result.onSuccess {
//        println("Work experience deleted successfully.")
//    }.onFailure { error ->
//        println("Error deleting work experience: ${error.message}")
//    }

    //test get user profile
//    val profileResult = dbStorage.userProfileRepository.getUserProfile(userId)
//    profileResult.onSuccess { profile ->
//        println("User profile: $profile")
//    }.onFailure { error ->
//        println("Error fetching user profile: ${error.message}")
//    }

    //test add skill
//    val skill = "C++"
//    val result = dbStorage.userProfileRepository.addSkill(userId, skill)
//    result.onSuccess {
//        println("Skill added successfully.")
//    }.onFailure { error ->
//        println("Error adding skill: ${error.message}")
//    }

    //test delete skill
//    val skill = "C++"
//    val result = dbStorage.userProfileRepository.deleteSkill(userId, skill)
//    result.onSuccess {
//        println("Skill deleted successfully.")
//    }.onFailure { error ->
//        println("Error deleting skill: ${error.message}")
//    }


    //test update education
//    val educationId = "1"
//    val degreeId = 1
//    val major = "Computer Science"
//    val gpa = 3.9f
//    val startDate = LocalDate(2019, 9, 1)
//    val endDate = LocalDate(2023, 6, 1)
//    val institutionName = "University of Waterloo"
//    val result = dbStorage.userProfileRepository.updateEducation(
//        userId,
//        educationId,
//        degreeId,
//        major,
//        gpa,
//        startDate,
//        endDate,
//        institutionName
//    )
//    result.onSuccess {
//        println("Education updated successfully.")
//    }.onFailure { error ->
//        println("Error updating education: ${error.message}")
//    }

    //test update work experience
    val workExperienceId = "1"
    val companyName = "Google"
    val currentlyWorking = false
    val title = "Software Engineer"
    val startDate = LocalDate(2019, 9, 1)
    val endDate = LocalDate(2023, 6, 1)
    val description = "Worked on Android development."
    val result = dbStorage.userProfileRepository.updateWorkExperience(
        userId,
        workExperienceId,
        companyName,
        currentlyWorking,
        title,
        startDate,
        endDate,
        description
    )
    result.onSuccess {
        println("Work experience updated successfully.")
    }.onFailure { error ->
        println("Error updating work experience: ${error.message}")
    }

}


//
// just for testing,
// uncomment any of them when you want to test a single func
//

