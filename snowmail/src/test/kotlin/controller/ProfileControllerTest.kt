//package ca.uwaterloo.controller
//
//import ca.uwaterloo.model.Education
//import ca.uwaterloo.model.EducationWithDegreeName
//import ca.uwaterloo.model.WorkExperience
//import ca.uwaterloo.persistence.IUserProfileRepository
//import kotlinx.coroutines.test.runTest
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import org.mockito.kotlin.mock
//import org.mockito.kotlin.whenever
//import kotlin.test.assertEquals
//import kotlin.test.assertTrue
//import kotlinx.datetime.LocalDate
//
//class ProfileControllerTest {
//
//    private lateinit var userProfileRepository: IUserProfileRepository
//    private lateinit var profileController: ProfileController
//
//    @BeforeEach
//    fun setUp() {
//        userProfileRepository = mock()
//        profileController = ProfileController(userProfileRepository)
//    }
//
//    @Test
//    fun `getUserName should return success when user name is found`() = runTest {
//        val userId = "c9498eec-ac17-4a3f-8d91-61efba3f7277"
//        val expectedName = "Cherry Wang"
//        whenever(userProfileRepository.getUserName(userId)).thenReturn(Result.success(expectedName))
//
//        val result = profileController.getUserName(userId)
//
//        assertTrue(result.isSuccess)
//        assertEquals(expectedName, result.getOrNull())
//    }
//
//    @Test
//    fun `getUserName should return failure when no user name found`() = runTest {
//        val userId = "c9498eec-ac17-4a3f-8d91-61efba3f727"
//        whenever(userProfileRepository.getUserName(userId)).thenReturn(Result.failure(Exception("User not found")))
//
//        val result = profileController.getUserName(userId)
//
//        assertTrue(result.isFailure)
//        assertEquals("User not found", result.exceptionOrNull()?.message)
//    }
//
//    @Test
//    fun `addEducation should return success when education record added successfully`() = runTest {
//        val userId = "c9498eec-ac17-4a3f-8d91-61efba3f7277"
//        val degreeId = 1
//        val major = "Computer Science"
//        val gpa = 3.8f
//        val startDate = LocalDate(2019, 9, 1)
//        val endDate = LocalDate(2023, 6, 1)
//        val institutionName = "MIT"
//        whenever(userProfileRepository.addEducation(userId, degreeId, major, gpa, startDate, endDate, institutionName))
//            .thenReturn(Result.success(true))
//
//        val result = profileController.addEducation(userId, degreeId, major, gpa, startDate, endDate, institutionName)
//
//        assertTrue(result.isSuccess)
//        assertEquals(true, result.getOrNull())
//    }
//
//    @Test
//    fun `getEducation should return list of education records`() = runTest {
//        val userId = "c9498eec-ac17-4a3f-8d91-61efba3f7277"
//        val educationList = listOf(
//            EducationWithDegreeName(1, userId, "Bachelors", "university of Waterloo","Computer Science", 3.8f, LocalDate(2019, 9, 1), LocalDate(2023, 6, 1))
//        )
//        whenever(userProfileRepository.getEducation(userId)).thenReturn(Result.success(educationList))
//
//        val result = profileController.getEducation(userId)
//
//        assertTrue(result.isSuccess)
//        assertEquals(educationList, result.getOrNull())
//    }
//
//    @Test
//    fun `addWorkExperience should return success when work experience record added`() = runTest {
//        val userId = "c9498eec-ac17-4a3f-8d91-61efba3f7277"
//        val workExperience = WorkExperience(1, userId, "Microsoft", false, "Software Engineer", LocalDate(2022, 1, 1), LocalDate(2023, 6, 1), "Worked on backend")
//        whenever(userProfileRepository.addWorkExperience(userId, workExperience.companyName, workExperience.currentlyWorking, workExperience.title, workExperience.startDate, workExperience.endDate, workExperience.description))
//            .thenReturn(Result.success(true))
//
//        val result = profileController.addWorkExperience(userId, workExperience.companyName, workExperience.currentlyWorking, workExperience.title, workExperience.startDate, workExperience.endDate, workExperience.description)
//
//        assertTrue(result.isSuccess)
//        assertEquals(true, result.getOrNull())
//    }
//
//    @Test
//    fun `getWorkExperience should return list of work experience records`() = runTest {
//        val userId = "c9498eec-ac17-4a3f-8d91-61efba3f7277"
//        val workExperienceList = listOf(
//            WorkExperience(1, userId, "Microsoft", false, "Software Engineer", LocalDate(2022, 1, 1), LocalDate(2023, 6, 1), "Worked on backend")
//        )
//        whenever(userProfileRepository.getWorkExperience(userId)).thenReturn(Result.success(workExperienceList))
//
//        val result = profileController.getWorkExperience(userId)
//
//        assertTrue(result.isSuccess)
//        assertEquals(workExperienceList, result.getOrNull())
//    }
//}
//
