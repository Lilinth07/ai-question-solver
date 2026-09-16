package com.lilinth.questionsolver.data.model

/**
 * 解题历史记录
 */
data class QuestionHistory(
    val id: Long,
    val imageUri: String,
    val question: String,
    val answer: String,
    val timestamp: Long,
    val model: String
)
