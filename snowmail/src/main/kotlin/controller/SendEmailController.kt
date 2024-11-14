package controller

import ca.uwaterloo.persistence.IJobApplicationRepository
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import model.email
import persistence.JobApplicationRepository
import service.sendEmail

// functions to send email
suspend fun send_email(
    senderEmail: String,
    password: String,
    recipient: String,
    subject: String,
    text: String,
    fileURLs: List<String>,
    fileNames: List<String>,
    // new parameters from Sprint 2:
    jobApplicationRepository: IJobApplicationRepository,
    userID: String,
    jobTitle: String,
    companyName: String

) {

        val Email = email(senderEmail, password, recipient, subject, text, fileURLs, fileNames)
        sendEmail(Email)

        // update last refresh time if necessary
        jobApplicationRepository.updateRefreshTime(userID)

        // save job application
        jobApplicationRepository.createJobApplication(
            userID,
            jobTitle,
            companyName,
            recipient
        )

}


suspend fun main() {
    val senderEmail = "cs346test@gmail.com"
    val password = "qirk dyef rvbv bkka"
    val recipient = "irishuang1105@gmail.com"
    val subject = "subject"
    val text = "text"
    val userID = "ed52b6c4-2ae8-4b58-bacd-adc00082a505"
    val jobTitle = "Software Developer"
    val companyName = "Google"
    val supabase = createSupabaseClient(
        supabaseUrl = "https://gwnlngyvkxdpodenpyyj.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imd3bmxuZ3l2a3hkcG9kZW5weXlqIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mjc5MTAxNTEsImV4cCI6MjA0MzQ4NjE1MX0.olncAUMxSOjcr0YjssWXThtXDXC3q4zasdNYdwavt8g"
    ) {
        install(Postgrest)
        install(Auth)
        install(Storage)
    }
    val JobApplicationRepository = JobApplicationRepository(supabase)
    send_email(senderEmail, password, recipient, subject, text, listOf(), listOf(), JobApplicationRepository, userID, jobTitle, companyName)
}









