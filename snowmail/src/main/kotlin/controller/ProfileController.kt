package ca.uwaterloo.controller

import ca.uwaterloo.model.Education
import model.UserProfile
// import ca.uwaterloo.persistence.DBStorage

import integration.SupabaseClient
import ca.uwaterloo.model.WorkExperience
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate

class ProfileController(private val dbStorage: SupabaseClient) {

    // get user's name and display it on profile page
    suspend fun getUserName(userId: String): Result<String> {
        return dbStorage.userProfileRepository.getUserName(userId)
    }

    //
    //education exp
    //

    // get education exp by user id
    suspend fun getEducation(userId: String): Result<List<Education>> {
        return dbStorage.userProfileRepository.getEducation(userId)
    }

    // add education record to db
    suspend fun addEducation(
        userId: String,
        degreeId: Int,
        major: String,
        gpa: Float?,
        startDate: LocalDate,
        endDate: LocalDate,
        institutionName: String
    ): Result<Boolean> {
        return dbStorage.userProfileRepository.addEducation(
            userId = userId,
            degreeId = degreeId,
            major = major,
            gpa = gpa,
            startDate = startDate,
            endDate = endDate,
            institutionName = institutionName
        )
    }

    //
    //working exp
    //
    // get work exp by user id
    suspend fun getWorkExperience(userId: String): Result<List<WorkExperience>> {
        return dbStorage.userProfileRepository.getWorkExperience(userId)
    }

    // add work exp record to db
    suspend fun addWorkExperience(
        userId: String,
        companyName: String,
        currentlyWorking: Boolean,
        title: String,
        startDate: LocalDate,
        endDate: LocalDate,
        description: String?
    ): Result<Boolean> {
        return dbStorage.userProfileRepository.addWorkExperience(
            userId = userId,
            companyName = companyName,
            currentlyWorking = currentlyWorking,
            title = title,
            startDate = startDate,
            endDate = endDate,
            description = description
        )
    }


}

fun main() = runBlocking<Unit> {
    val dbStorage = SupabaseClient()
    val profileController = ProfileController(dbStorage)

    val userId = "c9498eec-ac17-4a3f-8d91-61efba3f7277"
//    val profileResult = profileController.getUserName(userId)
//
//    profileResult.onSuccess { fullName ->
//        println("User Profile Name: $fullName")
//    }.onFailure { error ->
//        println("Error fetching user profile: ${error.message}")
//    }


    //test add edu exp to db
//    val educationResult = profileController.addEducation(
//        userId = "c9498eec-ac17-4a3f-8d91-61efba3f7277",
//        degreeId = "4",
//        major = "Linguistic",
//        gpa = 4.0f,
//        startDate = LocalDate(2023, 9, 1),
//        endDate = LocalDate(2027, 6, 1),
//        institutionName = "MIT"
//    )
//
//    educationResult.onSuccess {
//        println("Education record added successfully.")
//    }.onFailure { error ->
//        println("Error adding education record: ${error.message}")
//    }


    // test add work exp to db
//    val workExperienceResult = profileController.addWorkExperience(
//        userId = "c9498eec-ac17-4a3f-8d91-61efba3f7277",
//        companyName = "Microsoft",
//        currentlyWorking = false,
//        title = "Software Engineer",
//        startDate = LocalDate(2024, 1, 1),
//        endDate = LocalDate(2024, 10, 1),
//        description = "Worked on frontend."
//    )
//
//    workExperienceResult.onSuccess {
//        println("Work experience record added successfully.")
//    }.onFailure { error ->
//        println("Error adding work experience record: ${error.message}")
//    }

    // test getting edu exp
//    val educationResult = profileController.getEducation(userId)
//    educationResult.onSuccess { educationList ->
//        println("Education records:")
//        educationList.forEach { education ->
//            println(education)
//        }
//    }.onFailure { error ->
//        println("Error fetching education records: ${error.message}")
//    }
//
//    // test getting work exp
//    val workExperienceResult = profileController.getWorkExperience(userId)
//    workExperienceResult.onSuccess { workExperienceList ->
//        println("Work experience records:")
//        workExperienceList.forEach { workExperience ->
//            println(workExperience)
//        }
//    }.onFailure { error ->
//        println("Error fetching work experience records: ${error.message}")
//    }
}