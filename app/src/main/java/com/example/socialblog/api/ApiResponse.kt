package com.example.socialblog.api

import com.example.socialblog.model.Post
import com.example.socialblog.model.User

data class ApiResponse(

    val success: Boolean,

    val message: String,

    val user: User? = null,

    val posts: List<Post>? = null
)