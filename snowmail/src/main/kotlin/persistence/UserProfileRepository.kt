package ca.uwaterloo.persistence

import ca.uwaterloo.model.Education
import ca.uwaterloo.model.WorkExperience
import model.UserProfile
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import kotlinx.serialization.Contextual
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE
import java.util.*

class UserProfileRepository(private val supabase: SupabaseClient) : IUserProfileRepository{

    override suspend fun getUserProfile(userId: String): Result<UserProfile> {
        return try {
            // fetch user's profile from db based on userid
            val userProfile = supabase.from("user_profile")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeSingle<UserProfile>()
            Result.success(userProfile)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to fetch profile: ${e.message}"))
        }
    }

    override suspend fun getUserName(userId: String): Result<String> {
        return try {
            // fetch user's name from db based on userid
            val userProfile = supabase.from("user_profile")
                .select(columns = Columns.list("user_id", "first_name", "last_name")) {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeSingle<UserProfile>()

            Result.success("${userProfile.firstName} ${userProfile.lastName}")
        } catch (e: Exception) {
            Result.failure(Exception("Failed to fetch profile: ${e.message}"))
        }
    }

    override suspend fun getUserEmail(userId: String): Result<String> {
        return try {
            // fetch user's email from db based on userid
            val emailResult = supabase.from("user_profile")
                .select(columns = Columns.list("email")) {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeSingle<Map<String, String>>()

            val email = emailResult["email"] ?: throw Exception("Email not found")
            Result.success(email)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to fetch profile: ${e.message}"))
        }
    }

    override suspend fun getUserCity(userId: String): Result<String> {
        return try {
            // fetch user's city from db based on userid
            val cityResult = supabase.from("user_profile")
                .select(columns = Columns.list("city_name")) {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeSingle<Map<String, String>>()

            val city = cityResult["city_name"] ?: throw Exception("City not found")
            Result.success(city)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to fetch profile: ${e.message}"))
        }
    }

    override suspend fun getUserPhone(userId: String): Result<String> {
        return try {
            // fetch user's phone from db based on userid
            val phoneResult = supabase.from("user_profile")
                .select(columns = Columns.list("phone")) {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeSingle<Map<String, String>>()

            val phone = phoneResult["phone"] ?: throw Exception("Phone not found")
            Result.success(phone)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to fetch profile: ${e.message}"))
        }
    }

    override suspend fun updateCityPhone(userId: String, cityName: String?, phone: String?): Result<Boolean> {
        return try {
            withContext(Dispatchers.IO) {
                supabase.from("user_profile")
                    .update(mapOf("city_name" to cityName, "phone" to phone)){
                        filter {
                            eq("user_id", userId)
                        }
                    }
                Result.success(true)
            }
        } catch (e: Exception) {
            Result.failure(Exception("Failed to update user profile: ${e.message}"))
        }
    }

    override suspend fun getSkills(userId: String): Result<List<String>> {
        return try {
            // get user's skills from db based on userid
            val skillsResult = supabase.from("user_skills")
                .select(columns = Columns.list("skill")) {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeList<Map<String, String>>()

            val skills = skillsResult.mapNotNull { it["skill"] }
            Result.success(skills)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to fetch skills: ${e.message}"))
        }
    }

    override suspend fun addSkill(userId: String, skill: String): Result<Boolean> {
        return try {
            // check if the skill already exists
            val existingSkill = supabase.from("user_skills")
                .select(columns = Columns.list("skill")) {
                    filter {
                        eq("user_id", userId)
                        eq("skill", skill)
                    }
                }
                .decodeList<Map<String, String>>()

            // if the skill already exists, return success
            if (existingSkill.isNotEmpty()) {
                return Result.success(true)
            }

            // insert the skill into the database
            supabase.from("user_skills")
                .insert(mapOf("user_id" to userId, "skill" to skill))
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to add skill: ${e.message}"))
        }
    }

    override suspend fun deleteSkill(userId: String, skill: String): Result<Boolean> {
        return try {
            // check if the skill exists
            val existingSkill = supabase.from("user_skills")
                .select(columns = Columns.list("skill")) {
                    filter {
                        eq("user_id", userId)
                        eq("skill", skill)
                    }
                }
                .decodeList<Map<String, String>>()

            // if the skill does not exist, return failure
            if (existingSkill.isEmpty()) {
                return Result.failure(Exception("Skill not found for user: $userId"))
            }

            // delete the skill from the database
            supabase.from("user_skills")
                .delete {
                    filter {
                        eq("user_id", userId)
                        eq("skill", skill)
                    }
                }

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to delete skill: ${e.message}"))
        }
    }




    override suspend fun getEducation(userId: String): Result<List<Education>> {
        return try {
            val education = supabase.from("education")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeList<Education>()
            Result.success(education)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to fetch education: ${e.message}"))
        }
    }

    override suspend fun addEducation(
        userId: String,
        degreeId: Int,
        major: String,
        gpa: Float?,
        startDate: LocalDate,
        endDate: LocalDate,
        institutionName: String
    ): Result<Boolean> {
        return try {
            val education = Education(
                userId = userId,
                degreeId = degreeId,
                institutionName = institutionName,
                major = major,
                gpa = gpa,
                startDate = startDate,
                endDate = endDate
            )
            supabase.from("education").insert(education)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to add education: ${e.message}"))
        }
    }

    override suspend fun updateEducation(
        userId: String,
        educationID: String,
        degreeId: Int,
        major: String,
        gpa: Float?,
        startDate: LocalDate,
        endDate: LocalDate,
        institutionName: String
    ): Result<Boolean> {
        return try {
            // check if education exists
            val existingEducation = supabase.from("education")
                .select {
                    filter {
                        eq("id", educationID)
                    }
                }
                .decodeSingleOrNull<Education>()

            if (existingEducation != null) {
                // if exists, update it

                supabase.from("education")
                    .update(mapOf(
                        "degree_id" to degreeId.toString(),
                        "major" to major,
                        "gpa" to gpa.toString(),
                        "start_date" to startDate.toString(),
                        "end_date" to endDate.toString(),
                        "institution_name" to institutionName
                    )) {
                        filter {
                            eq("id", educationID)
                        }
                    }

                Result.success(true)
            } else {
                // if not exists, return failure
                Result.failure(Exception("Education record with ID $educationID not found"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Failed to update education: ${e.message}"))
        }
    }

    override suspend fun deleteEducation(educationID: String): Result<Boolean> {
        return try {
            // check if education exists
            val existingEducation = supabase.from("education")
                .select {
                    filter {
                        eq("id", educationID)
                    }
                }
                .decodeSingleOrNull<Education>()

            if (existingEducation != null) {
                // if exists, delete it
                supabase.from("education")
                    .delete {
                        filter {
                            eq("id", educationID)
                        }
                    }
                Result.success(true)
            } else {
                // if not exists, return failure
                Result.failure(Exception("Education record with ID $educationID not found"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Failed to delete education: ${e.message}"))
        }
    }


    override suspend fun getWorkExperience(userId: String): Result<List<WorkExperience>> {
        return try {
            val workExperience = supabase.from("work_experience")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeList<WorkExperience>()
            Result.success(workExperience)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to fetch work experience: ${e.message}"))
        }
    }

    override suspend fun addWorkExperience(
        userId: String,
        companyName: String,
        currentlyWorking: Boolean,
        title: String,
        startDate: LocalDate,
        endDate: LocalDate,
        description: String?
    ) : Result<Boolean>{
        return try {
            val workExperience = WorkExperience(
                userId = userId,
                companyName = companyName,
                currentlyWorking = currentlyWorking,
                title = title,
                startDate = startDate,
                endDate = endDate,
                description = description
            )
            supabase.from("work_experience").insert(workExperience)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to add working experience: ${e.message}"))
        }
    }

    override suspend fun updateWorkExperience(
        userId: String,
        workExperienceID: String,
        companyName: String,
        currentlyWorking: Boolean,
        title: String,
        startDate: LocalDate,
        endDate: LocalDate,
        description: String?
    ): Result<Boolean> {
        return try {
            // check if work experience exists
            val existingWorkExperience = supabase.from("work_experience")
                .select {
                    filter {
                        eq("id", workExperienceID)
                    }
                }
                .decodeSingleOrNull<WorkExperience>()

            if (existingWorkExperience != null) {
                // if exists, update it
                supabase.from("work_experience")
                    .update(mapOf(
                        "company_name" to companyName,
                        "currently_working" to currentlyWorking.toString(),
                        "title" to title,
                        "start_date" to startDate.toString(),
                        "end_date" to endDate.toString(),
                        "description" to description
                    )) {
                        filter {
                            eq("id", workExperienceID)
                        }
                    }
                Result.success(true)
            } else {
                // if not exists, return failure
                Result.failure(Exception("Work experience with ID $workExperienceID not found"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Failed to update work experience: ${e.message}"))
        }
    }

    override suspend fun deleteWorkExperience(workExperienceID: String): Result<Boolean> {
        return try {
            // check if work experience exists
            val existingWorkExperience = supabase.from("work_experience")
                .select {
                    filter {
                        eq("id", workExperienceID)
                    }
                }
                .decodeSingleOrNull<WorkExperience>()

            if (existingWorkExperience != null) {
                // if exists, delete it
                supabase.from("work_experience")
                    .delete {
                        filter {
                            eq("id", workExperienceID)
                        }
                    }
                Result.success(true)
            } else {
                // if not exists, return failure
                Result.failure(Exception("Work experience with ID $workExperienceID not found"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Failed to delete work experience: ${e.message}"))
        }
    }


}
