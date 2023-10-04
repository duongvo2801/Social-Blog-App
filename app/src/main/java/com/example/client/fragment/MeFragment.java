package com.example.client.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.client.R;
import com.example.client.account.Login;
import com.example.client.me.ChangePasswordActivity;
import com.example.client.me.ProfileActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class MeFragment extends Fragment {
    private LinearLayout linearChangePass, linearLogout, linearProfile, linearVerify;
    private ImageView imgProfile;
    private TextView tvFullName, tvEmail;
    TextView tvVerify;
    Button btnSentLink;
    FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private FirebaseFirestore firestore;
    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);

        linearVerify = view.findViewById(R.id.linearVerify);
        linearChangePass = view.findViewById(R.id.linearChangePass);
        linearLogout = view.findViewById(R.id.linearLogout);
        linearProfile = view.findViewById(R.id.linearProfile);
        tvVerify = view.findViewById(R.id.tvVerify);
        btnSentLink = view.findViewById(R.id.btnSentLink);
        imgProfile = view.findViewById(R.id.imgProfile);
        tvFullName = view.findViewById(R.id.tvFullName);
        tvEmail = view.findViewById(R.id.tvEmail);

        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        // get image from firebase
        StorageReference fileRef = storageReference.child("users/" + firebaseAuth.getCurrentUser().getUid() + "/profile.jpg");
        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(imgProfile);
            }
        });

        showUser();

        // sent link verify email
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        if(!user.isEmailVerified()) {
            linearVerify.setVisibility(View.VISIBLE);

            btnSentLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(view.getContext(), "Verifivation Email Has Been Sent", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(view.getContext(), "Email not sent" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }

        linearProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ProfileActivity.class));
            }
        });


        linearChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ChangePasswordActivity.class));
            }
        });

        linearLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        return view;
    }

    private void showUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null) {
            return;
        }
        String name = user.getDisplayName();
        String email = user.getEmail();

        if(name == null) {
            tvFullName.setVisibility(View.GONE);
        } else {
            tvFullName.setVisibility(View.VISIBLE);
            tvFullName.setText(name);
        }
        tvEmail.setText(email);

    }
}