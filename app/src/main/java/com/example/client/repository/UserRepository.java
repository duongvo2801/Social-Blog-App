package com.example.client.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;


import com.example.client.api.ApiResponseCallback;
import com.example.client.api.ApiService;
import com.example.client.api.RetrofitClient;
import com.example.client.model.ApiResponse;
import com.example.client.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {
    private final ApiService apiService;
    public UserRepository() {
        apiService = RetrofitClient.getClient().create(ApiService.class);
    }
    public void getAllUsers(Callback<ApiResponse<List<User>>> callback) {
        Call<ApiResponse<List<User>>> call = apiService.getAllUsers();
        call.enqueue(callback);
    }

    public void createUser(User user, ApiResponseCallback<User> apiResponseCallback) {
        Call<ApiResponse<User>> call = apiService.createUser(user);
        call.enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful()) {
                    Log.d("UserRepository", "Network request completed successfully. User data: " + response.body().toString());
                    ApiResponse<User> apiResponse = response.body();
                    if (apiResponse != null && "success".equals(apiResponse.getStatus())) {
                        apiResponseCallback.onSuccess(apiResponse.getData());
                    } else {
                        Log.d("UserRepository", "Network request completed with error. Error code: " + response.code() + ", Error body: " + response.errorBody().toString());
                        apiResponseCallback.onFailure("Failed to create user.");
                    }
                } else {
                    apiResponseCallback.onFailure("Network error.");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                Log.d("UserRepository", "Network request failed. Error: " + t.getMessage());
                apiResponseCallback.onFailure("Network error: " + t.getMessage());


            }
        });
    }


    public void getUserById(String userId, Callback<ApiResponse<User>> callback) {
        Call<ApiResponse<User>> call = apiService.getUserById(userId);
        call.enqueue(callback);
    }

    public void updateUser(String userId, User user, Callback<ApiResponse<User>> callback) {
        Call<ApiResponse<User>> call = apiService.updateUser(userId, user);
        call.enqueue(callback);
    }

    public void deleteUser(String userId, Callback<ApiResponse<Void>> callback) {
        Call<ApiResponse<Void>> call = apiService.deleteUser(userId);
        call.enqueue(callback);
    }

    public LiveData<ApiResponse<User>> registerUser(String username, String password) {


        User user= new User();
        user.setUsername(username);
        user.setPassword(password);

        return apiService.registerUser(user);
    }

    public LiveData<ApiResponse<User>> loginUser(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        return apiService.loginUser(user);
    }


}
