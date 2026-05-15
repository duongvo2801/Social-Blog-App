package com.example.socialblog.model

data class Post(

    val id: Int,

    val user_id: Int,

    val username: String,

    val avatar: String,

    val content: String,

    val feeling: String,

    val image: String?,

    val created_at: String
)