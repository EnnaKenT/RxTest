package com.vlad.rxtest.entity.response

data class UserResponse(
        val id: Int,
        val username: String,
        val about: String,
        val karma: Int,
        val created_at: String,
        val avg: Double,
        val delay: Any,
        val submitted: Int,
        val updated_at: String,
        val submission_count: Int,
        val comment_count: Int,
        val created_at_i: Int,
        val objectID: String
)