package model

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    var userId: String,
    var firstName: String,
    var lastName: String,
    var skills: List<String>? = null,
    var cityName: String? = null,
    val resumeUrl: String? = null,
    val linkedinUrl: String? = null,
    val githubUrl: String? = null,
    val personalWebsiteUrl: String? = null,
) {}