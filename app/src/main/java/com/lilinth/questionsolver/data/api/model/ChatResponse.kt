package com.lilinth.questionsolver.data.api.model

import com.google.gson.annotations.SerializedName

data class ChatResponse(
    @SerializedName("id")
    val id: String,

    @SerializedName("object")
    val objectType: String,

    @SerializedName("created")
    val created: Long,

    @SerializedName("model")
    val model: String,

    @SerializedName("choices")
    val choices: List<Choice>,

    @SerializedName("usage")
    val usage: Usage? = null
)

data class Choice(
    @SerializedName("index")
    val index: Int,

    @SerializedName("message")
    val message: ResponseMessage? = null,

    @SerializedName("delta")
    val delta: Delta? = null,

    @SerializedName("finish_reason")
    val finishReason: String? = null
)

data class ResponseMessage(
    @SerializedName("role")
    val role: String,

    @SerializedName("content")
    val content: String
)

data class Delta(
    @SerializedName("role")
    val role: String? = null,

    @SerializedName("content")
    val content: String? = null
)

data class Usage(
    @SerializedName("prompt_tokens")
    val promptTokens: Int,

    @SerializedName("completion_tokens")
    val completionTokens: Int,

    @SerializedName("total_tokens")
    val totalTokens: Int
)
