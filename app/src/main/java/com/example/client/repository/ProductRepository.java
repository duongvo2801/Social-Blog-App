package com.example.client.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.client.api.ApiResponseCallback;
import com.example.client.api.ApiService;
import com.example.client.api.RetrofitClient;
import com.example.client.model.ApiResponse;
import com.example.client.model.Category;
import com.example.client.model.Product;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductRepository {
    private final ApiService apiService;
    private static final String TAG = "ProductRepository";
    private MutableLiveData<List<Product>> products;
    public ProductRepository() {
        apiService = RetrofitClient.getClient().create(ApiService.class);
    }

    public void getAllProducts(ApiResponseCallback<List<Product>> apiResponseCallback) {

        Call<ApiResponse<List<Product>>> call = apiService.getAllProducts();
        call.enqueue(new Callback<ApiResponse<List<Product>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Product>>> call, Response<ApiResponse<List<Product>>> response) {
                if (response.isSuccessful()) {
                    Log.d("API_RESPONSE", "Response is successful");
                    ApiResponse<List<Product>> apiResponse = response.body();
                    if (apiResponse != null && "success".equals(apiResponse.getStatus())) {
//                        apiResponseCallback.onSuccess(apiResponse.getData());
                        List<Product> productList = apiResponse.getData();

                        Log.d("ProductRepository", "API success: Received " + apiResponse.getData().size() + " products");
                        for (Product product : productList) {
                            // Here, we assume that the image data is returned as a byte[] from the API
//                            byte[] imageData = (byte[]) product.getImage();
//                            product.setImage(imageData);
                            byte[] imageData = product.getImage().getDataAsByteArray();
                        }
                        apiResponseCallback.onSuccess(productList);
                    } else {
                        Log.e("API_RESPONSE", "Network error: " + response.code());
                        apiResponseCallback.onFailure("Failed to fetch products.");
                        Log.e("ProductRepository", "API failure: Failed to update product");

                    }
                } else {
                    apiResponseCallback.onFailure("Network error.");
                    Log.e("ProductRepository", "API network error");

                }

            }

            @Override
            public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {
                apiResponseCallback.onFailure("Network error: " + t.getMessage());
                Log.e("ProductRepository", "API network error: " + t.getMessage());
            }
        });
    }
    public void createProduct(Product product, ApiResponseCallback<Product> apiResponseCallback) {
        // ... Implementation for createProduct ...
        Call<ApiResponse<Product>> call = apiService.createProduct(product);
        call.enqueue(new Callback<ApiResponse<Product>>() {
            @Override
            public void onResponse(Call<ApiResponse<Product>> call, Response<ApiResponse<Product>> response) {
                if (response.isSuccessful()) {
                    ApiResponse<Product> apiResponse = response.body();
                    if (apiResponse != null && "success".equals(apiResponse.getStatus())) {
                        apiResponseCallback.onSuccess(apiResponse.getData());
                    } else {
                        apiResponseCallback.onFailure("Failed to create product.");
                    }
                } else {
                    apiResponseCallback.onFailure("Network error.");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Product>> call, Throwable t) {
                apiResponseCallback.onFailure("Network error: " + t.getMessage());
            }
        });
    }

    public void getProductById(String productId, ApiResponseCallback<Product> apiResponseCallback) {
        // ... Implementation for getProductById ...
        Call<ApiResponse<Product>> call = apiService.getProductById(productId);
        call.enqueue(new Callback<ApiResponse<Product>>() {
            @Override
            public void onResponse(Call<ApiResponse<Product>> call, Response<ApiResponse<Product>> response) {
                if (response.isSuccessful()) {
                    ApiResponse<Product> apiResponse = response.body();
                    if (apiResponse != null && "success".equals(apiResponse.getStatus())) {
                        apiResponseCallback.onSuccess(apiResponse.getData());
                    } else {
                        apiResponseCallback.onFailure("Failed to fetch product details.");
                    }
                } else {
                    apiResponseCallback.onFailure("Network error.");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Product>> call, Throwable t) {
                apiResponseCallback.onFailure("Network error: " + t.getMessage());
            }
        });
    }

    public void updateProduct(String productId, Product product, ApiResponseCallback<Product> apiResponseCallback) {
        if (productId == null || product == null) {
            apiResponseCallback.onFailure("ID sản phẩm hoặc thông tin sản phẩm không được để trống.");
            return;
        }
        Log.d("ProductRepository", "Updating product with ID " + productId + " and data: " + product.toString());
        // ... Implementation for updateProduct ...
        Call<ApiResponse<Product>> call = apiService.updateProduct(productId, product);
        call.enqueue(new Callback<ApiResponse<Product>>() {
            @Override
            public void onResponse(Call<ApiResponse<Product>> call, Response<ApiResponse<Product>> response) {
                Log.d("ProductRepository", "Response from server: " + response.body());
                if (response.isSuccessful()) {
                    ApiResponse<Product> apiResponse = response.body();
                    if (apiResponse != null && "success".equals(apiResponse.getStatus())) {
                        apiResponseCallback.onSuccess(apiResponse.getData());
                    } else {
                        apiResponseCallback.onFailure("Failed to update product.");
                    }
                } else {
                    Log.e("ProductRepository", "Response error: " + response.code() + " " + response.message());
                    apiResponseCallback.onFailure("Network error.");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Product>> call, Throwable t) {
                Log.e("ProductRepository", "Request failed: " + t.getMessage());
                apiResponseCallback.onFailure("Network error: " + t.getMessage());

            }
        });
    }
    public void uploadProductImage(String productId, File imageFile, ApiResponseCallback<Product> callback) {
        // Convert the file to a RequestBody for uploading
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), imageFile);

        // Convert the RequestBody to a MultipartBody.Part
        MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("image", imageFile.getName(), requestBody);

        Call<ApiResponse<Product>> call = apiService.uploadProductImage(productId, multipartBody);

        call.enqueue(new Callback<ApiResponse<Product>>() {
            @Override
            public void onResponse(Call<ApiResponse<Product>> call, Response<ApiResponse<Product>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onFailure("Failed to upload image.");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Product>> call, Throwable t) {
                callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }



    public void deleteProduct(String productId, ApiResponseCallback<Void> apiResponseCallback) {
        // ... Implementation for deleteProduct ...
        if (productId == null) {
            apiResponseCallback.onFailure("ID sản phẩm không được để trống.");
            return;
        }
        Call<ApiResponse<Void>> call = apiService.deleteProduct(productId);
        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    ApiResponse<Void> apiResponse = response.body();
                    if (apiResponse != null && "success".equals(apiResponse.getStatus())) {
                        apiResponseCallback.onSuccess(null);
                    } else {
                        apiResponseCallback.onFailure("Failed to delete product.");
                    }
                } else {
                    apiResponseCallback.onFailure("Network error.");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                apiResponseCallback.onFailure("Network error: " + t.getMessage());
            }
        });

    }
    public void getCategories(ApiResponseCallback<List<Category>> apiResponseCallback) {
        Call<ApiResponse<List<Category>>> call = apiService.getCategories();
        call.enqueue(new Callback<ApiResponse<List<Category>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Category>>> call, Response<ApiResponse<List<Category>>> response) {
                if (response.isSuccessful()) {
                    ApiResponse<List<Category>> apiResponse = response.body();
                    if (apiResponse != null && "success".equals(apiResponse.getStatus())) {
                        apiResponseCallback.onSuccess(apiResponse.getData());
                    } else {
                        apiResponseCallback.onFailure("Failed to fetch categories.");
                    }
                } else {
                    apiResponseCallback.onFailure("Network error.");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Category>>> call, Throwable t) {
                apiResponseCallback.onFailure("Network error: " + t.getMessage());
            }
        });
    }

}
