import controller.send_email
import junit.framework.TestCase.assertTrue
import org.junit.jupiter.api.Test


class EmailSendingControllerTest {

    @Test
    fun testWithAttachments() {
        assertTrue(
            runCatching { send_email(
                senderEmail = "cs346test@gmail.com",
                password = "qirk dyef rvbv bkka",
                recipient = "irishuang1105@gmail.com",
                subject = "Test Email",
                text = "This is a test email",
                fileURLs = listOf("https://gwnlngyvkxdpodenpyyj.supabase.co/storage/v1/object/sign/testEmailSending/CS341%20lec%2013.pdf?token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1cmwiOiJ0ZXN0RW1haWxTZW5kaW5nL0NTMzQxIGxlYyAxMy5wZGYiLCJpYXQiOjE3MzAzNTczNjUsImV4cCI6MTc2MTg5MzM2NX0.g03s4yqS5LP5EAtAPdNbsiPEBjsfvTs86M97vFj9XcE&t=2024-10-31T06%3A49%3A24.915Z"),
                fileNames = listOf("CS341 lec 13.pdf")
            ) }.isSuccess
        )
    }

    @Test
    fun testWithoutAttachments() {
        assertTrue(
            runCatching { send_email(
                senderEmail = "cs346test@gmail.com",
                password = "qirk dyef rvbv bkka",
                recipient = "irishuang1105@gmail.com",
                subject = "Test Email",
                text = "This is a test email",
                fileURLs = listOf(),
                fileNames = listOf()
            ) }.isSuccess
        )
    }


}