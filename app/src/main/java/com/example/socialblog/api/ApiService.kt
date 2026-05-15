package com.example.socialblog.api

import com.example.socialblog.model.Post
import com.example.socialblog.model.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {

    @POST("api/auth/register")
    fun register(
        @Body user: User
    ): Call<ApiResponse>

    @POST("api/auth/login")
    fun login(
        @Body user: User
    ): Call<ApiResponse>

    @GET("api/auth/user/{id}")
    fun getUser(
        @Path("id") id: Int
    ): Call<ApiResponse>

    @PUT("api/auth/update/{id}")
    fun updateUser(
        @Path("id") id: Int,
        @Body user: User
    ): Call<ApiResponse>

    @Multipart
    @POST("api/auth/upload/{id}")
    fun uploadAvatar(

        @Path("id") id: Int,

        @Part avatar: MultipartBody.Part

    ): Call<ApiResponse>

    @Multipart
    @POST("api/post/create")
    fun createPost(

        @Part("user_id")
        userId: RequestBody,

        @Part("content")
        content: RequestBody,

        @Part("feeling")
        feeling: RequestBody,

        @Part
        image: MultipartBody.Part?

    ): Call<ApiResponse>

    @GET("api/post/all/")
    fun getPosts(): Call<ApiResponse>
}