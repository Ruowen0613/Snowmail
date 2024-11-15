package  ca.uwaterloo.controller

import ca.uwaterloo.persistence.IJobApplicationRepository
import ca.uwaterloo.persistence.IJobApplicationRepository.JobProgress
import ca.uwaterloo.persistence.IJobApplicationRepository.Progress
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import persistence.JobApplicationRepository
import service.email
import service.searchEmails

class ProgressController(private val jobApplicationRepository: IJobApplicationRepository) {

    // getProgress function for listing all information Progress page needs
    // what is returned in this function?
    // a struct Progress

    // what is in Progress?
    // data class Progress(
    //        val appliedItemCount: Int,
    //        val interviewedItemCount: Int,
    //        val offerItemCount: Int,
    //        val otherItemCount: Int,
    //        val appliedJobs: List<JobProgress> ,
    //        val interviewedJobs: List<JobProgress>,
    //        val offerJobs: List<JobProgress>,
    //        val otherJobs: List<JobProgress>
    //    )

    // so let result = getProgress(userID), example:
    // number of applied jobs = result.appliedItemCount
    // jobs information of applied jobs:
    // for job in result.appliedJobs:
    //     jobTitle = job.jobTitle
    //     companyName = job.companyName
    //     recruiterEmail = job.recruiterEmail

    suspend fun getProgress(userId: String): Progress {
        jobApplicationRepository.updateRefreshTime(userId)
        return jobApplicationRepository.getProgress(userId).getOrNull()!!
    }


    // return all new emails from last refresh time to now
    // return value is List<Email>
    suspend fun getNewEmails(userId: String, linkedEmail: String, appPassword: String): List<email> {
        // get all recruiter emails
        val recruiterEmails = jobApplicationRepository.getAllRecruiterEmailAddress(userId)
        // get last refresh time
        val lastRefreshTime = jobApplicationRepository.getRefreshTime(userId).getOrNull()!!
        // TO BE ADDED: REMOVE HARDCODE EMAIL AND GMAIL PASSWORD
        // search emails
        val emails = searchEmails(linkedEmail, appPassword, lastRefreshTime, recruiterEmails)
        return emails
    }


    // return all emails with APPLIED status
    // intended to be called when prompting user to decide which job application status to change
    suspend fun getAllAppliedJobs(userId: String): List<Pair<JobProgress, String>> {
        return jobApplicationRepository.getAppliedJobs(userId).getOrNull()!!
    }


    // modifyStatus function for changing the status of a job application
    suspend fun modifyStatus(JobApplicationID: String, newStatus: Int) {
        jobApplicationRepository.updateJobApplicationStatus(JobApplicationID, newStatus)
    }

}

//suspend fun main() {
//
//    val supabase = createSupabaseClient(
//        supabaseUrl = "https://gwnlngyvkxdpodenpyyj.supabase.co",
//        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imd3bmxuZ3l2a3hkcG9kZW5weXlqIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mjc5MTAxNTEsImV4cCI6MjA0MzQ4NjE1MX0.olncAUMxSOjcr0YjssWXThtXDXC3q4zasdNYdwavt8g"
//    ) {
//        install(Postgrest)
//        install(Auth)
//        install(Storage)
//    }
//    val JobApplicationRepository = JobApplicationRepository(supabase)
//
//    // getProgress
//    val progressController = ProgressController(JobApplicationRepository)
//    val result: Progress = progressController.getProgress("ed52b6c4-2ae8-4b58-bacd-adc00082a505")
//    println(result)
//    println(result.appliedItemCount)
//    print(result.appliedJobs)
//
//    // modifyStatus
//    val result2 = progressController.modifyStatus("788db112-76bd-4cb1-95ff-93a0e1d7e078", 3)
//    println(result2)
//
//    // getAllAppliedJobs
//    val result3 = progressController.getAllAppliedJobs("ed52b6c4-2ae8-4b58-bacd-adc00082a505")
//    println(result3)
//
//    val result4 = progressController.getNewEmails("ed52b6c4-2ae8-4b58-bacd-adc00082a505")
//    println(result4)
//
//}

