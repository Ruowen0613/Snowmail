package ca.uwaterloo.controller

import ca.uwaterloo.model.Education
import model.UserProfile
// import ca.uwaterloo.persistence.DBStorage

import integration.SupabaseClient
import ca.uwaterloo.model.WorkExperience
import ca.uwaterloo.persistence.IUserProfileRepository
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate

class ProfileController(private val userProfileRepository: IUserProfileRepository) {

    // get user's name and display it on profile page
    suspend fun getUserName(userId: String): Result<String> {
        return userProfileRepository.getUserName(userId)
    }

    // get user's email and display it on profile page
    suspend fun getUserEmail(userId: String): Result<String> {
        return userProfileRepository.getUserEmail(userId)
    }

    // get user's city
    suspend fun getUserCity(userId: String): Result<String> {
        return userProfileRepository.getUserCity(userId)
    }

    // get user's phone
    suspend fun getUserPhone(userId: String): Result<String> {
        return userProfileRepository.getUserPhone(userId)
    }

    suspend fun updateCityPhone(userId: String, cityName: String?, phone: String?): Result<Boolean> {
        return userProfileRepository.updateCityPhone(userId, cityName, phone)
    }

    // get user's skills
    suspend fun getSkills(userId: String): Result<List<String>> {
        return userProfileRepository.getSkills(userId)
    }

    // add skill to db
    suspend fun addSkill(userId: String, skill: String): Result<Boolean> {
        return userProfileRepository.addSkill(userId, skill)
    }

    // delete skill from db
    suspend fun deleteSkill(userId: String, skill: String): Result<Boolean> {
        return userProfileRepository.deleteSkill(userId, skill)
    }

    //
    //education exp
    //

    // get education exp by user id
    suspend fun getEducation(userId: String): Result<List<Education>> {
        return userProfileRepository.getEducation(userId)
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
        return userProfileRepository.addEducation(
            userId = userId,
            degreeId = degreeId,
            major = major,
            gpa = gpa,
            startDate = startDate,
            endDate = endDate,
            institutionName = institutionName
        )
    }

    //update education record in db
    suspend fun updateEducation(
        userId: String,
        educationId: String,
        degreeId: Int,
        major: String,
        gpa: Float?,
        startDate: LocalDate,
        endDate: LocalDate,
        institutionName: String
    ): Result<Boolean> {
        return userProfileRepository.updateEducation(
            userId = userId,
            educationID = educationId,
            degreeId = degreeId,
            major = major,
            gpa = gpa,
            startDate = startDate,
            endDate = endDate,
            institutionName = institutionName
        )
    }

    // delete education record from db
    suspend fun deleteEducation(educationID: String): Result<Boolean> {
        return userProfileRepository.deleteEducation(educationID)
    }

    //
    //working exp
    //
    // get work exp by user id
    suspend fun getWorkExperience(userId: String): Result<List<WorkExperience>> {
        return userProfileRepository.getWorkExperience(userId)
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
        return userProfileRepository.addWorkExperience(
            userId = userId,
            companyName = companyName,
            currentlyWorking = currentlyWorking,
            title = title,
            startDate = startDate,
            endDate = endDate,
            description = description
        )
    }

    //update work exp record in db
    suspend fun updateWorkExperience(
        userId: String,
        workExperienceID: String,
        companyName: String,
        currentlyWorking: Boolean,
        title: String,
        startDate: LocalDate,
        endDate: LocalDate,
        description: String?
    ): Result<Boolean> {
        return userProfileRepository.updateWorkExperience(
            userId = userId,
            workExperienceID = workExperienceID,
            companyName = companyName,
            currentlyWorking = currentlyWorking,
            title = title,
            startDate = startDate,
            endDate = endDate,
            description = description
        )
    }

    // delete work exp record from db
    suspend fun deleteWorkExperience(workExperienceID: String): Result<Boolean> {
        return userProfileRepository.deleteWorkExperience(workExperienceID)
    }
}

fun main() = runBlocking<Unit> {
    val dbStorage = SupabaseClient()
    val profileController = ProfileController(dbStorage.userProfileRepository)

    val userId = "c9498eec-ac17-4a3f-8d91-61efba3f7277"
    //val userId = "a365a4c4-6427-4461-8cb4-2850fab8ac8c"

    //test get user's name
//    val profileResult = profileController.getUserName(userId)
//    profileResult.onSuccess { fullName ->
//        println("User Profile Name: $fullName")
//    }.onFailure { error ->
//        println("Error fetching user profile: ${error.message}")
//    }

    //test get user's email
//    val profileResult = profileController.getUserEmail(userId)
//    profileResult.onSuccess { email ->
//        println("email: $email")
//    }.onFailure { error ->
//        println("Error fetching user profile: ${error.message}")
//    }

//
//    //test get user's city
//    val profileResult = profileController.getUserCity(userId)
//    profileResult.onSuccess { city ->
//        println("city: $city")
//    }.onFailure { error ->
//        println("Error fetching user profile: ${error.message}")
//    }

    //test get user's phone
//    val profileResult = profileController.getUserPhone(userId)
//    profileResult.onSuccess { phone ->
//        println("phone: $phone")
//    }.onFailure { error ->
//        println("Error fetching user profile: ${error.message}")
//    }


    //test editing city and phone
//    val cityName = "NYC"
//    val phone = "+1234567890"
//    val result = profileController.updateCityPhone(userId, cityName, phone)
//    result.onSuccess {
//        println("User profile updated successfully.")
//    }.onFailure { error ->
//        println("Error updating user profile: ${error.message}")
//    }


    //test get skills
//    val skillsResult = profileController.getSkills(userId)
//    skillsResult.onSuccess { skills ->
//        println("Skills: $skills")
//    }.onFailure { error ->
//        println("Error fetching skills: ${error.message}")
//    }

    //test add skill
//    val skill = "SQL"
//    val result = profileController.addSkill(userId, skill)
//    result.onSuccess {
//        println("Skill added successfully.")
//    }.onFailure { error ->
//        println("Error adding skill: ${error.message}")
//    }

    //test delete skill
//    val skill = "SQL"
//    val result = profileController.deleteSkill(userId, skill)
//    result.onSuccess {
//        println("Skill deleted successfully.")
//    }.onFailure { error ->
//        println("Error deleting skill: ${error.message}")
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

    // test deleting education record
//    val educationId = "100"
//    val result = profileController.deleteEducation(educationId)
//    result.onSuccess {
//        println("Education deleted successfully.")
//    }.onFailure { error ->
//        println("Error deleting education: ${error.message}")
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

    // test getting work exp
//    val workExperienceResult = profileController.getWorkExperience(userId)
//    workExperienceResult.onSuccess { workExperienceList ->
//        println("Work experience records:")
//        workExperienceList.forEach { workExperience ->
//            println(workExperience)
//        }
//    }.onFailure { error ->
//        println("Error fetching work experience records: ${error.message}")
//    }

    // test deleting work exp record
//    val workExperienceId = "11"
//    val result = profileController.deleteWorkExperience(workExperienceId)
//    result.onSuccess {
//        println("Work experience deleted successfully.")
//    }.onFailure { error ->
//        println("Error deleting work experience: ${error.message}")
//    }
}