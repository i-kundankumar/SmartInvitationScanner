package com.orbitex.sis.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.KeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.orbitex.sis.LoginActivity;
import com.orbitex.sis.R;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragments extends Fragment {
    private TextInputEditText etEmail, etName, etPhone;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    public ProfileFragments() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Bind views
        etEmail = view.findViewById(R.id.etEmail);
        etName = view.findViewById(R.id.etName);
        etPhone = view.findViewById(R.id.etPhone);
        MaterialButton btnSave = view.findViewById(R.id.btnUpdate);
        MaterialButton btnSignOut = view.findViewById(R.id.btnSignOut);
        loadProfileData();
        lockEditText(etEmail);
        btnSave.setOnClickListener(v -> onSaveClicked());
        btnSignOut.setOnClickListener(v -> onSignOutClicked());
        return view;
    }

    private void onSignOutClicked() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Sign out")
                .setMessage("Are you sure you want to sign out?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Sign out", (dialog, which) -> {
                    FirebaseAuth.getInstance().signOut();
                    Intent i = new Intent(requireContext(), LoginActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    requireActivity().finish();
                })
                .show();
    }

    private void onSaveClicked() {
        FirebaseUser current = auth.getCurrentUser();
        if (current == null) {
            Toast.makeText(requireContext(), "No logged-in user", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = etName.getText() == null ? "" : etName.getText().toString().trim();
        String phone = etPhone.getText() == null ? "" : etPhone.getText().toString().trim();

        if (name.isEmpty()) {
            etName.setError("Name is required");
            etName.requestFocus();
            return;
        }
        if (!phone.isEmpty()) {
            String normalized = phone.replaceAll("[\\s\\-()]", "");
            if (!normalized.matches("^\\+?[0-9]{6,15}$")) {
                etPhone.setError("Enter a valid phone number");
                etPhone.requestFocus();
                return;
            }
        }


        String uid = current.getUid();
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("phone", phone);

        db.collection("users").document(uid)
                .set(updates, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    // Also update Firebase Auth profile (displayName & photo)
                    UserProfileChangeRequest.Builder profileBuilder = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name);
                    UserProfileChangeRequest profileUpdates = profileBuilder.build();

                    current.updateProfile(profileUpdates)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show();
                                } else {
                                    String msg = task.getException() != null ? task.getException().getMessage() : "Failed to update auth profile";
                                    Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show();
                                }
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to save profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void loadProfileData() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(requireContext(), LoginActivity.class));
            requireActivity().finish();
            return;
        }
        String userId = user.getUid();
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        etName.setText(user.getDisplayName() != null ? user.getDisplayName() : "");
                        etEmail.setText(user.getEmail());
                        return;
                    }
                    String name = documentSnapshot.getString("name");
                    String phone = documentSnapshot.getString("phone");
                    String email = documentSnapshot.getString("email");
                    etName.setText(name != null ? name : "");
                    etPhone.setText(phone != null ? phone : "");
                    etEmail.setText(email!= null ? email : user.getEmail());
                }).addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }

    private void lockEditText(TextInputEditText et) {
        et.setFocusable(false);
        et.setFocusableInTouchMode(false);
        et.setCursorVisible(false);
        et.setKeyListener(null);
    }
}
