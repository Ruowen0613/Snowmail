package  ca.uwaterloo.controller

import ca.uwaterloo.persistence.DocumentRepository
import ca.uwaterloo.persistence.IJobApplicationRepository
import ca.uwaterloo.persistence.IJobApplicationRepository.JobProgress
import ca.uwaterloo.persistence.IJobApplicationRepository.Progress
import integration.OpenAIClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import persistence.JobApplicationRepository
import service.email
import service.searchEmails

class ProgressController(private val jobApplicationRepository: IJobApplicationRepository, private val openAIClient: OpenAIClient) {

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
        val progress = jobApplicationRepository.getProgress(userId).getOrNull()!!
        jobApplicationRepository.updateRefreshTime(userId)
        return progress
    }

   // Determine the application status of the application using an LLM
    suspend fun classifyApplicationStatus(emailContent: String): String {
        return openAIClient.classifyStatusOfApplication(emailContent)
    }



    // return all new emails from last refresh time to now
    // return value is List<Email>
    suspend fun getNewEmails(userId: String, linkedEmail: String, appPassword: String, documentRepository: DocumentRepository): List<email> {
        // get all recruiter emails
        val recruiterEmails = jobApplicationRepository.getAllRecruiterEmailAddress(userId)
        // get last refresh time
        val lastRefreshTime = jobApplicationRepository.getRefreshTime(userId).getOrNull()!!
        // TO BE ADDED: REMOVE HARDCODE EMAIL AND GMAIL PASSWORD
        // search emails
        val emails = searchEmails(linkedEmail, appPassword, lastRefreshTime, recruiterEmails, documentRepository)
        return emails
    }


    // return all jobs with APPLIED status
    // intended to be called when prompting user to decide which job application status to change
    suspend fun getAllAppliedJobs(userId: String): List<Pair<JobProgress, String>> {
        return jobApplicationRepository.getAppliedJobs(userId).getOrNull()!!
    }

    // return all jobs with CORRESPONDING email address
    suspend fun getCorrespondJobs(userId: String, email: String): List<Pair<JobProgress, String>> {
        return jobApplicationRepository.getCorrespondJobs(userId, email).getOrNull()!!
    }


    // modifyStatus function for changing the status of a job application
    suspend fun modifyStatus(JobApplicationID: String, newStatus: Int) {
        jobApplicationRepository.updateJobApplicationStatus(JobApplicationID, newStatus)
    }

    suspend fun deleteAttachments(files: List<String>, documentRepository: DocumentRepository) {
        documentRepository.deleteAttachments(files)
    }


}

suspend fun main() {

    val supabase = createSupabaseClient(
        supabaseUrl = "https://gwnlngyvkxdpodenpyyj.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imd3bmxuZ3l2a3hkcG9kZW5weXlqIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mjc5MTAxNTEsImV4cCI6MjA0MzQ4NjE1MX0.olncAUMxSOjcr0YjssWXThtXDXC3q4zasdNYdwavt8g"
    ) {
        install(Postgrest)
        install(Auth)
        // install(Storage)
    }
    val JobApplicationRepository = JobApplicationRepository(supabase)

    // getProgress
    val openAIClient = OpenAIClient(HttpClient(CIO))
    val progressController = ProgressController(JobApplicationRepository, openAIClient)
//    val result: Progress = progressController.getProgress("ed52b6c4-2ae8-4b58-bacd-adc00082a505")
//    println(result)
//    println(result.appliedItemCount)
//    print(result.appliedJobs)

    // modifyStatus
    // val result2 = progressController.modifyStatus("788db112-76bd-4cb1-95ff-93a0e1d7e078", 3)
    // println(result2)

    // getAllAppliedJobs
    // val result3 = progressController.getAllAppliedJobs("ed52b6c4-2ae8-4b58-bacd-adc00082a505")
    // println(result3)

    // val result4 = progressController.getNewEmails("ed52b6c4-2ae8-4b58-bacd-adc00082a505")
    // println(result4)

    val result5 = progressController.getCorrespondJobs("c9498eec-ac17-4a3f-8d91-61efba3f7277", "y45zhang@uwtarloo.ca")
    for (i in result5) {
        println(i)
    }

}


