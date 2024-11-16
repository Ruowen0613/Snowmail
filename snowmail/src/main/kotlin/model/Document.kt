package ca.uwaterloo.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Document(
    @SerialName("id") val id: String? = null, //can be null and handled by db when insert
    @SerialName("user_id") val userId: String,
    @SerialName("document_type") val documentType: Int,
    @SerialName("document_name") val documentName: String,
    @SerialName("bucket") val bucket: String,
    @SerialName("path") val path: String,
    @SerialName("uploaded_at") val uploadedAt: String? = null, //can be null and handled by db when insert
)
