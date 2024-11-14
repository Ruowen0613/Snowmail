package ca.uwaterloo.service

import java.io.InputStream
import java.util.*
import javax.mail.*


// constants
val host = "imap.gmail.com"
val port = 993

data class email (
    val senderEmail: String,
    val subject: String,
    val text: String
)

fun searchEmails(userAccount: String, userPassword: String,
                 last_refresh_time: Date,
                 recruiterEmails: List<String>): List<email> {

    val properties = Properties().apply {
        put("mail.imap.host", host)
        put("mail.imap.port", port)
        put("mail.imap.ssl.enable", "true")
    }

    val session: Session = Session.getInstance(properties)
    var result = mutableListOf<email>()

    try {
        val store: Store = session.getStore("imap")
        store.connect(userAccount, userPassword)

        val inbox: Folder = store.getFolder("INBOX")
        inbox.open(Folder.READ_ONLY)

        val messages: Array<Message> = inbox.messages

        for (i in messages.size - 1 downTo 0) {

            val message = messages[i]

            // only search to last refresh time
            if (message.receivedDate.before(last_refresh_time)) {
                break
            }

            // only return those recruiters' emails
            val emailAddress = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}").find(message.from[0].toString())?.value
            if (recruiterEmails.contains(emailAddress!!)) {
                val content = message.content
                var text = ""
                val attachments = mutableListOf<InputStream>()

                if (content is String) {
                    text = content
                } else if (content is Multipart) {
                    for (j in 0 until content.count) {
                        val bodyPart = content.getBodyPart(j)
                        if (bodyPart.isMimeType("text/plain")) {
                            text += bodyPart.content
                        } else if (Part.ATTACHMENT.equals(bodyPart.disposition, ignoreCase = true)) {
                            attachments.add(bodyPart.inputStream)
                        }
                    }
                }
                val item = email(emailAddress, message.subject, text)
                result.add(item)
            }
        }

    } catch (e: Exception) {
        e.printStackTrace()
    }
    return result
}


fun createSpecificDateTime(year: Int, month: Int, day: Int, hour: Int, minute: Int, second: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.YEAR, year)
    calendar.set(Calendar.MONTH, month - 1) // Month is 0-based, so subtract 1
    calendar.set(Calendar.DAY_OF_MONTH, day)
    calendar.set(Calendar.HOUR_OF_DAY, hour) // 24-hour format
    calendar.set(Calendar.MINUTE, minute)
    calendar.set(Calendar.SECOND, second)
    calendar.set(Calendar.MILLISECOND, 0) // Optional: Set milliseconds to 0
    return calendar.time
}


fun main() {
    val username = "amy20041105@gmail.com"
    val password = "payy hcof nwom kqnr"
    val time = createSpecificDateTime(2024, 10, 15, 2, 3, 0)
    val recruiters = listOf("services@51job.com", "heretohelp.ca@custhelp.com")
    val result = searchEmails(username, password, time, recruiters)
    println(result)
}