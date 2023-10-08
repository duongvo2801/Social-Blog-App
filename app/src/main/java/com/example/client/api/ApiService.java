package com.example.client.api;

import com.example.client.model.ApiResponse;
import com.example.client.model.Product;

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

    @Multipart
    @PUT("products/{id}/image")
    Call<ApiResponse<Product>> uploadProductImage(@Path("id") String productId, @Part MultipartBody.Part imageFile);

}
