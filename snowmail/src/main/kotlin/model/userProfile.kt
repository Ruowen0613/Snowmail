package ca.uwaterloo.model

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    var user_id: String,
    var first_name: String,
    var last_name: String,
    var skills: String? = null,
    var city_name: String? = null,
    val resume_url: String? = null,
    val linkedin_url: String? = null,
    val github_url: String? = null,
    val personal_website_url: String? = null,
) {}
