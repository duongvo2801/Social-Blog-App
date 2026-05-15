package com.example.socialblog.mainUI

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.example.socialblog.MainActivity
import com.example.socialblog.api.ApiClient
import com.example.socialblog.api.ApiResponse
import com.example.socialblog.databinding.ActivityLoginBinding
import com.example.socialblog.model.User

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {

            val email =
                binding.edtEmail.text.toString().trim()

            val password =
                binding.edtPassword.text.toString().trim()

            // Validate

            if (
                email.isEmpty() ||
                password.isEmpty()
            ) {

                Toast.makeText(
                    this,
                    "Vui lòng nhập đầy đủ thông tin",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            // Create user object

            val user = User(
                email = email,
                password = password
            )

            // API Login

            ApiClient.apiService.login(user)
                .enqueue(object : Callback<ApiResponse> {

                    override fun onResponse(
                        call: Call<ApiResponse>,
                        response: Response<ApiResponse>
                    ) {

                        if (response.isSuccessful) {

                            val result = response.body()

                            Toast.makeText(
                                this@LoginActivity,
                                result?.message,
                                Toast.LENGTH_SHORT
                            ).show()

                            if (result?.success == true) {

                                // Save userId

                                val sharedPreferences =
                                    getSharedPreferences(
                                        "USER",
                                        MODE_PRIVATE
                                    )

                                val editor =
                                    sharedPreferences.edit()

                                editor.putInt(
                                    "userId",
                                    result.user?.id ?: 0
                                )

                                editor.putString(
                                    "avatar",
                                    result.user?.avatar ?: ""
                                )

                                editor.apply()

                                // Open MainActivity

                                startActivity(
                                    Intent(
                                        this@LoginActivity,
                                        MainActivity::class.java
                                    )
                                )

                                finish()
                            }

                        } else {

                            Toast.makeText(
                                this@LoginActivity,
                                "Login Failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(
                        call: Call<ApiResponse>,
                        t: Throwable
                    ) {

                        Toast.makeText(
                            this@LoginActivity,
                            t.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }

        // Open RegisterActivity

        binding.txtRegister.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    RegisterActivity::class.java
                )
            )
        }
    }
}