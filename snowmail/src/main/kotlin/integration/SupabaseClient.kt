package integration

import ca.uwaterloo.persistence.AuthRepository
import ca.uwaterloo.persistence.UserProfileRepository
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate

class SupabaseClient {
    private val supabase = createSupabaseClient(
        supabaseUrl = "https://gwnlngyvkxdpodenpyyj.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imd3bmxuZ3l2a3hkcG9kZW5weXlqIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mjc5MTAxNTEsImV4cCI6MjA0MzQ4NjE1MX0.olncAUMxSOjcr0YjssWXThtXDXC3q4zasdNYdwavt8g"
    ) {
        install(Postgrest)
        install(Auth)
    }
    val authRepository = AuthRepository(supabase)
    val userProfileRepository = UserProfileRepository(supabase)
}


//
// just for testing,
// uncomment any of them when you want to test a single func
//
fun main() = runBlocking<Unit> {
    val dbStorage = SupabaseClient()

    val email = "wrw040613@gmail.com"
    val password = "Wrw54321"
    val firstname = "Cherry"
    val lastname = "Wang"


    val userId = "c9498eec-ac17-4a3f-8d91-61efba3f7277"
//
//    //test sign up
//    println (dbStorage.authRepository.signUpUser(email, password, firstname, lastname))
//
//    //test sign in
//    println (dbStorage.authRepository.signInUser(email, password))
//
//    //test sign out
//    println(dbStorage.authRepository.signOutUser())
//
    //test returning user's name
//    val profileResult = dbStorage.userProfileRepository.getUserName(userId)
//        profileResult.onSuccess { fullName ->
//            println("User Profile Name: $fullName")
//        }.onFailure { error ->
//            println("Error fetching user profile: ${error.message}")
//        }

//    val emailResult = dbStorage.userProfileRepository.getUserEmail(userId)
//    emailResult.onSuccess { email ->
//        println("User email: $email")
//    }.onFailure { error ->
//        println("Error fetching user email: ${error.message}")
//    }


    //test adding work exp
//    val userId = "c9498eec-ac17-4a3f-8d91-61efba3f7277"
//    val companyName = "Example Company"
//    val currentlyWorking = true
//    val title = "Software Engineer"
//    val startDate = LocalDate(2020, 1, 1)
//    val endDate = LocalDate(2022, 1, 1)
//    val description = "Worked on backend systems."
//    val result = dbStorage.userProfileRepository.addWorkExperience(
//        userId = userId,
//        companyName = companyName,
//        currentlyWorking = currentlyWorking,
//        title = title,
//        startDate = startDate,
//        endDate = endDate,
//        description = description
//    )
//
//    result.onSuccess {
//        println("Working experience added successfully.")
//    }.onFailure { error ->
//        println("Error adding working experience: ${error.message}")
//    }

    //test adding edu exp
//    val degreeId = "3"
//    val major = "Computer Science"
//    val gpa = 3.8f
//    val startDate = LocalDate(2019, 9, 1)
//    val endDate = LocalDate(2023, 6, 1)
//    val institutionName = "University of Waterloo"
//
//    val result = dbStorage.userProfileRepository.addEducation(
//        userId = userId,
//        degreeId = degreeId,
//        major = major,
//        gpa = gpa,
//        startDate = startDate,
//        endDate = endDate,
//        institutionName = institutionName
//    )
//
//    result.onSuccess {
//        println("Education record added successfully.")
//    }.onFailure { error ->
//        println("Error adding education record: ${error.message}")
//    }

    // test getting edu exp
//    val educationResult = dbStorage.userProfileRepository.getEducation(userId)
//    educationResult.onSuccess { educationList ->
//        println("Education records:")
//        educationList.forEach { education ->
//            println(education)
//        }
//    }.onFailure { error ->
//        println("Error fetching education records: ${error.message}")
//    }

    // test getting work exp
//    val workExperienceResult = dbStorage.userProfileRepository.getWorkExperience(userId)
//    workExperienceResult.onSuccess { workExperienceList ->
//        println("Work experience records:")
//        workExperienceList.forEach { workExperience ->
//            println(workExperience)
//        }
//    }.onFailure { error ->
//        println("Error fetching work experience records: ${error.message}")
//    }
}