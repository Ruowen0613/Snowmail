package controller

import ca.uwaterloo.persistence.IJobApplicationRepository
import model.email
import service.sendEmail

// functions to send email
suspend fun send_email(
    senderEmail: String,
    password: String,
    recipient: String,
    subject: String,
    text: String,
    fileURLs: List<String>,
    // new parameters from Sprint 2:
    fileNames: List<String>,
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










