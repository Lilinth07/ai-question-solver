package com.lilinth.questionsolver.data.api

import com.google.gson.annotations.SerializedName

data class ChatRequest(
    @SerializedName("model")
    val model: String,
    @SerializedName("messages")
    val messages: List<Message>,
    @SerializedName("max_tokens")
    val maxTokens: Int = 1000
)

data class Message(
    @SerializedName("role")
    val role: String,
    @SerializedName("content")
    val content: List<Content>
)

data class Content(
    @SerializedName("type")
    val type: String,
    @SerializedName("text")
    val text: String? = null,
    @SerializedName("image_url")
    val imageUrl: ImageUrl? = null
)

data class ImageUrl(
    @SerializedName("url")
    val url: String
)

data class ChatResponse(
    @SerializedName("choices")
    val choices: List<Choice>
)

data class Choice(
    @SerializedName("message")
    val message: MessageContent
)

data class MessageContent(
    @SerializedName("content")
    val content: String
)
