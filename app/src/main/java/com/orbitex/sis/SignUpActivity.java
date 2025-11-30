package com.orbitex.sis;

import android.os.Bundle;
import android.text.TextPaint;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    private TextView btnSignIn;
    private TextInputLayout userName, userEmail, userNo, userPassword;
    private Button btnSignUp;
    private static final int PASSWORD_MIN_LENGTH = 8;
    private long lastClickTime = 0;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnSignIn = findViewById(R.id.signInBtn);
        btnSignIn.setPaintFlags(btnSignIn.getPaintFlags() | TextPaint.UNDERLINE_TEXT_FLAG);
        btnSignIn.setOnClickListener(v -> {
            if (isFastClick()) {
                return;
            }
            NavigationUtils.go(SignUpActivity.this, LoginActivity.class, false);
        });
        userName = findViewById(R.id.user_name);
        userEmail = findViewById(R.id.user_email);
        userNo = findViewById(R.id.user_no);
        userPassword = findViewById(R.id.user_password);
        btnSignUp = findViewById(R.id.btnSignUp);

        btnSignUp.setOnClickListener(v -> {
            if (isFastClick()) return;
            if (!validateAll()) return;
            SignUp();
        });
    }

    private Boolean validateName() {
        if (userName == null || userName.getEditText() == null) return false;
        String val = userName.getEditText().getText() == null ? "" : userName.getEditText().getText().toString().trim();
        if (val.isEmpty()) {
            userName.setError("Full name is required");
            return false;
        }
        // optional: more complex name rules e.g., min length or no digits
        if (val.length() < 2) {
            userName.setError("Please enter a valid name");
            return false;
        }
        userName.setError(null);
        userName.setErrorEnabled(false);
        return true;
    }
    private Boolean validateEmail() {
        if (userEmail == null || userEmail.getEditText() == null) return false;
        String val = userEmail.getEditText().getText() == null ? "" : userEmail.getEditText().getText().toString().trim();
        if (val.isEmpty()) {
            userEmail.setError("Email is required");
            return false;
        }
        // Use Android's built-in email pattern (more robust)
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(val).matches()) {
            userEmail.setError("Enter a valid email address");
            return false;
        }
        userEmail.setError(null);
        userEmail.setErrorEnabled(false);
        return true;
    }
    private Boolean validatePhoneNo() {
        if (userNo == null || userNo.getEditText() == null) return false;
        String val = userNo.getEditText().getText() == null ? "" : userNo.getEditText().getText().toString().trim();
        if (val.isEmpty()) {
            userNo.setError("Phone number is required");
            return false;
        }

        // Basic digit-only check (allow + for international numbers)
        String normalized = val.replaceAll("[\\s\\-()]", ""); // remove spaces, dashes, parentheses
        if (!normalized.matches("^\\+?[0-9]{10,15}$")) {
            userNo.setError("Enter a valid phone number");
            return false;
        }
        userNo.setError(null);
        userNo.setErrorEnabled(false);
        return true;
    }
    private Boolean validatePassword() {
        if (userPassword == null || userPassword.getEditText() == null) return false;
        String val = userPassword.getEditText().getText() == null ? "" : userPassword.getEditText().getText().toString();
        if (val.isEmpty()) {
            userPassword.setError("Password is required");
            return false;
        }
        if (val.length() < PASSWORD_MIN_LENGTH) {
            userPassword.setError("Password must be at least " + PASSWORD_MIN_LENGTH + " characters");
            return false;
        }
        // Require at least one letter and one digit and one special char, no whitespace
        String passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&\\^#_+=~`\\-])[A-Za-z\\d@$!%*?&\\^#_+=~`\\-]{"
                + PASSWORD_MIN_LENGTH + ",}$";
        if (!val.matches(passwordPattern)) {
            userPassword.setError("Password must contain letters, numbers and a special character");
            return false;
        }
        userPassword.setError(null);
        userPassword.setErrorEnabled(false);
        return true;
    }
    private boolean validateAll() {
        boolean nameOk = validateName();
        boolean emailOk = validateEmail();
        boolean phoneOk = validatePhoneNo();
        boolean passOk = validatePassword();
        return nameOk && emailOk && phoneOk && passOk;
    }

    private void SignUp() {
        String email = Objects.requireNonNull(userEmail.getEditText()).getText().toString().trim();
        String password = Objects.requireNonNull(userPassword.getEditText()).getText().toString().trim();
        String name = Objects.requireNonNull(userName.getEditText()).getText().toString().trim();
        String phone = Objects.requireNonNull(userNo.getEditText()).getText().toString().trim();


        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (!task.isSuccessful()) {
                        handleSignUpError(task);
                        return;
                    }
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    if (firebaseUser == null) {
                        Toast.makeText(SignUpActivity.this, "Unexpected auth error", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String uid = firebaseUser.getUid();
                    Map<String, Object> user = new HashMap<>();
                    user.put("name", name);
                    user.put("email", email);
                    user.put("phone", phone);
                    user.put("createdAt", FieldValue.serverTimestamp());


                    db.collection("users").document(uid)
                            .set(user)
                            .addOnCompleteListener(dbTask -> {
                                if (dbTask.isSuccessful()){
                                sendEmailVerification(firebaseUser);
                                new MaterialAlertDialogBuilder(SignUpActivity.this)
                                        .setTitle("Account Created")
                                        .setMessage("An email verification link has been sent to your email address. Please verify your email before logging in.")
                                        .setPositiveButton("OK", (dialog, which) -> {
                                            NavigationUtils.go(SignUpActivity.this, LoginActivity.class, true);
                                        })
                                        .setCancelable(false)
                                        .show();
                            } else {
                                firebaseUser.delete();
                                Toast.makeText(SignUpActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                    });
                });

    }

    private void sendEmailVerification(FirebaseUser user) {
        if (user == null) return;
        user.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        // Log but don't block user (can retry later)
                    }
                });
    }


    private void handleSignUpError(Task<AuthResult> task) {
        Exception e = task.getException();
        if (e == null) {
            Toast.makeText(this, "Sign up failed", Toast.LENGTH_LONG).show();
            return;
        }

        if (e instanceof FirebaseAuthWeakPasswordException) {
            userPassword.setError("Weak password: " + ((FirebaseAuthWeakPasswordException) e).getReason());
            userPassword.requestFocus();
        } else if (e instanceof FirebaseAuthUserCollisionException) {
            // Email already in use
            userEmail.setError("Email already in use. Try signing in or reset password.");
            userEmail.requestFocus();
            // optionally provide dialog to go to login
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Email already registered")
                    .setMessage("This email is already registered. Would you like to go to sign in?")
                    .setPositiveButton("Sign in", (d, w) -> NavigationUtils.go(SignUpActivity.this, LoginActivity.class, true))
                    .setNegativeButton("Cancel", null)
                    .show();
        } else {
            Toast.makeText(this, "Sign up failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private boolean isFastClick() {
        long now = System.currentTimeMillis();
        if (now - lastClickTime < 1000) {
            return true;
        }
        lastClickTime = now;
        return false;
    }
}