package com.example.client.api;

import androidx.lifecycle.LiveData;

import com.example.client.model.ApiResponse;
import com.example.client.model.Category;
import com.example.client.model.Product;
import com.example.client.model.User;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {
    // user
    @GET("users")
    Call<ApiResponse<List<User>>> getAllUsers();

    @POST("users")
    Call<ApiResponse<User>> createUser(@Body User user);

    @GET("users/{id}")
    Call<ApiResponse<User>> getUserById(@Path("id") String userId);

    @PUT("users/{id}")
    Call<ApiResponse<User>> updateUser(@Path("id") String userId, @Body User user);

    @DELETE("users/{id}")
    Call<ApiResponse<Void>> deleteUser(@Path("id") String userId);

    @POST("register")
    LiveData<ApiResponse<User>> registerUser(@Body User user);

    @POST("login")
    LiveData<ApiResponse<User>> loginUser(@Body User user);

    // product
    @GET("products")
    Call<ApiResponse<List<Product>>> getAllProducts();

    @GET("products/{id}")
    Call<ApiResponse<Product>> getProductById(@Path("id") String id);

    @POST("products")
    Call<ApiResponse<Product>> createProduct(@Body Product product);

    @PUT("products/{id}")
    Call<ApiResponse<Product>> updateProduct(@Path("id") String id, @Body Product product);

    @DELETE("products/{id}")
    Call<ApiResponse<Void>> deleteProduct(@Path("id") String id);
//    @DELETE("products/{id}")
//    Call<ApiResponse<Void>> deleteProduct(@Path("id") int id);
    // upload image
    @Multipart
    @PUT("products/{id}/image")
    Call<ApiResponse<Product>> uploadProductImage(@Path("id") String productId, @Part MultipartBody.Part imageFile);

    // category
    @GET("categories")
    Call<ApiResponse<List<Category>>> getCategories();
    @POST("categories")
    Call<ApiResponse<String>> createCategory(@Body String category);

    @GET("categories/{id}")
    Call<ApiResponse<String>> getCategoryById(@Path("id") String categoryId);

    @PUT("categories/{id}")
    Call<ApiResponse<String>> updateCategory(@Path("id") String categoryId, @Body String category);

    @DELETE("categories/{id}")
    Call<ApiResponse<Void>> deleteCategory(@Path("id") String categoryId);
}
