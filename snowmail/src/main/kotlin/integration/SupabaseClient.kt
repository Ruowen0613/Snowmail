package integration

import ca.uwaterloo.persistence.AuthRepository
import ca.uwaterloo.persistence.UserProfileRepository
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.datetime.LocalDate

class SupabaseClient {
    private val supabase = createSupabaseClient(
        supabaseUrl = "https://gwnlngyvkxdpodenpyyj.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imd3bmxuZ3l2a3hkcG9kZW5weXlqIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mjc5MTAxNTEsImV4cCI6MjA0MzQ4NjE1MX0.olncAUMxSOjcr0YjssWXThtXDXC3q4zasdNYdwavt8g"
    ) {
        install(Postgrest)
        install(Auth)
    }
    val authRepository = AuthRepository(supabase)
    val userProfileRepository = UserProfileRepository(supabase)
}

// Testing SupabaseClient
suspend fun main() {
    val dbStorage = SupabaseClient()

    val email = "wrw040613@gmail.com"
    val password = "Wrw54321"
    val firstname = "Cherry"
    val lastname = "Wang"

    // Test education record addition
    val userId = "c9498eec-ac17-4a3f-8d91-61efba3f7277"
    val degreeId = 3
    val major = "Computer Science"
    val gpa = 3.8f
    val startDate = LocalDate(2019, 9, 1)
    val endDate = LocalDate(2023, 6, 1)
    val institutionName = "University of Waterloo"

    val result = dbStorage.userProfileRepository.addEducation(
        userId = userId,
        degreeId = degreeId,
        major = major,
        gpa = gpa,
        startDate = startDate,
        endDate = endDate,
        institutionName = institutionName
    )

    result.onSuccess {
        println("Education record added successfully.")
    }.onFailure { error ->
        println("Error adding education record: ${error.message}")
    }
}