//package ca.uwaterloo.persistence
//import io.github.jan.supabase.auth.Auth
//import io.github.jan.supabase.auth.auth
//import io.github.jan.supabase.auth.providers.builtin.Email
//import io.github.jan.supabase.createSupabaseClient
//import io.github.jan.supabase.postgrest.Postgrest
//import io.github.jan.supabase.postgrest.from
//import ca.uwaterloo.model.UserProfile
//import io.github.jan.supabase.postgrest.query.Columns
//import kotlinx.coroutines.*
//import kotlinx.datetime.LocalDate
//
//
//    //test sign up
//    println (dbStorage.authRepository.signUpUser(email, password, firstname, lastname))
//
//    //test sign in
//    println (dbStorage.authRepository.signInUser(email, password))
//
//    //test sign out
//    println(dbStorage.authRepository.signOutUser())
//
//    val signInResult = dbStorage.authRepository.signInUser(email, password)
//    signInResult.onSuccess { userId ->
//        println("Sign-in successful. User ID: $userId")
//
//        //get user's name
//        val profileResult = dbStorage.userProfileRepository.getUserName(userId)
//        profileResult.onSuccess { fullName ->
//            println("User Profile Name: $fullName")
//        }.onFailure { error ->
//            println("Error fetching user profile: ${error.message}")
//        }
//    }.onFailure { error ->
//        println("Sign-in failed: ${error.message}")
//    }

    //test adding work exp
//    val userId = "c9498eec-ac17-4a3f-8d91-61efba3f7277"
//    val companyName = "Example Company"
//    val currentlyWorking = true
//    val title = "Software Engineer"
//    val startDate = LocalDate(2020, 1, 1)
//    val endDate = LocalDate(2022, 1, 1)
//    val description = "Worked on backend systems."
//    val result = dbStorage.userProfileRepository.addWorkExperience(
//        userId = userId,
//        companyName = companyName,
//        currentlyWorking = currentlyWorking,
//        title = title,
//        startDate = startDate,
//        endDate = endDate,
//        description = description
//    )
//
//    result.onSuccess {
//        println("Working experience added successfully.")
//    }.onFailure { error ->
//        println("Error adding working experience: ${error.message}")
//    }

    //test adding edu exp
//    val userId = "c9498eec-ac17-4a3f-8d91-61efba3f7277"
//    val degreeId = "3"
//    val major = "Computer Science"
//    val gpa = 3.8f
//    val startDate = LocalDate(2019, 9, 1)
//    val endDate = LocalDate(2023, 6, 1)
//    val institutionName = "University of Waterloo"
//
//    val result = dbStorage.userProfileRepository.addEducation(
//        userId = userId,
//        degreeId = degreeId,
//        major = major,
//        gpa = gpa,
//        startDate = startDate,
//        endDate = endDate,
//        institutionName = institutionName
//    )
//
//    result.onSuccess {
//        println("Education record added successfully.")
//    }.onFailure { error ->
//        println("Error adding education record: ${error.message}")
//    }
