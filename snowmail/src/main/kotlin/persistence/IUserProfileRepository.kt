package ca.uwaterloo.persistence

import ca.uwaterloo.model.Education
import ca.uwaterloo.model.WorkExperience
import model.UserProfile
import kotlinx.datetime.LocalDate

interface IUserProfileRepository {
    suspend fun getUserName(userId: String): Result<String>
    suspend fun getUserEmail(userId: String): Result<String>
    suspend fun updateUserProfile(userId: String, cityName: String?, phone: String?): Result<Boolean>
    suspend fun getEducation(userId: String): Result<List<Education>>
    suspend fun addEducation(
        userId: String,
        degreeId: Int,
        major: String,
        gpa: Float?,
        startDate: LocalDate,
        endDate: LocalDate,
        institutionName: String
    ): Result<Boolean>
    suspend fun getWorkExperience(userId: String): Result<List<WorkExperience>>
    suspend fun addWorkExperience(
        userId: String,
        companyName: String,
        currentlyWorking: Boolean,
        title: String,
        startDate: LocalDate,
        endDate: LocalDate,
        description: String?
    ): Result<Boolean>
}
