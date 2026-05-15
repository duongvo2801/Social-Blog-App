package com.example.socialblog.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

import com.bumptech.glide.Glide

import com.example.socialblog.api.ApiClient
import com.example.socialblog.api.ApiResponse
import com.example.socialblog.databinding.FragmentUserBinding
import com.example.socialblog.mainUI.LoginActivity
import com.example.socialblog.model.User
import com.example.socialblog.utils.FileUtils

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import java.io.File

class UserFragment : Fragment() {

    private var _binding:
            FragmentUserBinding? = null

    private val binding get() = _binding!!

    private var imageUri: Uri? = null

    private var userId = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentUserBinding.inflate(
                inflater,
                container,
                false
            )

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(
            view,
            savedInstanceState
        )

        // GET USER ID

        val sharedPreferences =
            requireActivity()
                .getSharedPreferences(
                    "USER",
                    AppCompatActivity.MODE_PRIVATE
                )

        userId =
            sharedPreferences.getInt(
                "userId",
                0
            )

        // CLICK AVATAR

        binding.imgAvatar.setOnClickListener {

            checkPermission()
        }

        // LOAD USER

        getUser()

        // UPDATE

        binding.btnUpdate.setOnClickListener {

            updateUser()
        }

        // LOGOUT

        binding.btnLogout.setOnClickListener {

            sharedPreferences
                .edit()
                .clear()
                .apply()

            startActivity(
                Intent(
                    requireContext(),
                    LoginActivity::class.java
                )
            )

            requireActivity().finish()
        }
    }

    // PERMISSION

    private fun checkPermission() {

        if (
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                101
            )

        } else {

            openGallery()
        }
    }

    // OPEN GALLERY

    private fun openGallery() {

        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )

        startActivityForResult(
            intent,
            100
        )
    }

    // RESULT IMAGE

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {

        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )

        if (
            requestCode == 100 &&
            data != null
        ) {

            imageUri = data.data

            binding.imgAvatar.setImageURI(
                imageUri
            )

            uploadImage()
        }
    }

    // UPLOAD IMAGE

    private fun uploadImage() {

        if (imageUri == null) return

        val filePath =
            FileUtils.getPath(
                requireContext(),
                imageUri!!
            )

        if (filePath.isEmpty()) {

            Toast.makeText(
                requireContext(),
                "Cannot get image path",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val file =
            File(filePath)

        val requestFile =
            file.asRequestBody(
                "image/*"
                    .toMediaTypeOrNull()
            )

        val body =
            MultipartBody.Part.createFormData(
                "avatar",
                file.name,
                requestFile
            )

        ApiClient.apiService
            .uploadAvatar(
                userId,
                body
            )
            .enqueue(object : Callback<ApiResponse> {

                override fun onResponse(
                    call: Call<ApiResponse>,
                    response: Response<ApiResponse>
                ) {

                    Toast.makeText(
                        requireContext(),
                        "Upload Success",
                        Toast.LENGTH_SHORT
                    ).show()

                    getUser()
                }

                override fun onFailure(
                    call: Call<ApiResponse>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        requireContext(),
                        t.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    // GET USER

    private fun getUser() {

        ApiClient.apiService
            .getUser(userId)
            .enqueue(object : Callback<ApiResponse> {

                override fun onResponse(
                    call: Call<ApiResponse>,
                    response: Response<ApiResponse>
                ) {

                    if (response.isSuccessful) {

                        val user =
                            response.body()?.user

                        Glide.with(requireContext())
                            .load(
                                "http://192.168.1.52:3000/uploads/avatar/" +
                                        user?.avatar +
                                        "?time=" +
                                        System.currentTimeMillis()
                            )
                            .into(binding.imgAvatar)

                        binding.edtId.setText(
                            user?.id.toString()
                        )

                        binding.edtUsername.setText(
                            user?.username
                        )

                        binding.edtEmail.setText(
                            user?.email
                        )

                        binding.edtPassword.setText(
                            user?.password
                        )
                    }
                }

                override fun onFailure(
                    call: Call<ApiResponse>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        requireContext(),
                        t.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    // UPDATE USER

    private fun updateUser() {

        val username =
            binding.edtUsername.text
                .toString()
                .trim()

        val email =
            binding.edtEmail.text
                .toString()
                .trim()

        val password =
            binding.edtPassword.text
                .toString()
                .trim()

        val user = User(
            id = userId,
            username = username,
            email = email,
            password = password
        )

        ApiClient.apiService
            .updateUser(
                userId,
                user
            )
            .enqueue(object : Callback<ApiResponse> {

                override fun onResponse(
                    call: Call<ApiResponse>,
                    response: Response<ApiResponse>
                ) {

                    Toast.makeText(
                        requireContext(),
                        response.body()?.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onFailure(
                    call: Call<ApiResponse>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        requireContext(),
                        t.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}