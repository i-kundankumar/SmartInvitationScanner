package com.orbitex.sis;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private TextView btnSignUp, forgotPassword;
    private TextInputLayout userEmail, userPassword;
    private Button btnSignIn;
    private FirebaseAuth auth;
    private long lastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Firebase
        auth = FirebaseAuth.getInstance();

        //Views
        btnSignUp = findViewById(R.id.signUpBtn);
        forgotPassword = findViewById(R.id.forgotPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        userEmail = findViewById(R.id.email);
        userPassword = findViewById(R.id.password);


        forgotPassword.setPaintFlags(forgotPassword.getPaintFlags() | TextPaint.UNDERLINE_TEXT_FLAG);
        btnSignUp.setPaintFlags(btnSignUp.getPaintFlags() | TextPaint.UNDERLINE_TEXT_FLAG);
        btnSignUp.setOnClickListener(v -> {
            if (isFastClick()) {
                return;
            }
            NavigationUtils.go(LoginActivity.this, SignUpActivity.class, false);
        });

        btnSignIn.setOnClickListener(v-> {
            if (isFastClick()) {
                return;
            }
            if (!validateAll()) {
                return;
            }
            SignIn();
        });

    }

    private void SignIn() {
        String email = userEmail.getEditText().getText().toString().trim();
        String password = userPassword.getEditText().getText().toString().trim();

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (!task.isSuccessful()) {
                        Exception e = task.getException();
                        String msg = (e == null) ? "Authentication failed" : e.getMessage();
                        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show();
                        return;
                    }

                    FirebaseUser user = auth.getCurrentUser();
                    if (user == null) {
                        Toast.makeText(LoginActivity.this, "Authentication succeeded but no user found", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (!user.isEmailVerified()) {
                        new MaterialAlertDialogBuilder(LoginActivity.this)
                                .setTitle("Email not verified")
                                .setMessage("Please verify your email address. Would you like us to resend the verification email?")
                                .setPositiveButton("Resend", (d, w) -> {
                                    user.sendEmailVerification()
                                            .addOnCompleteListener(resendTask -> {
                                                if (resendTask.isSuccessful()) {
                                                    Toast.makeText(LoginActivity.this, "Verification email sent", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(LoginActivity.this, "Failed to send verification email", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                        auth.signOut(); // force sign-out since not verified
                        return;
                    }

                    // At this point login is successful and email is verified.
                    // Optionally refresh ID token to get latest custom claims:
                    user.getIdToken(true).addOnCompleteListener(tokenTask -> {
                        // You can inspect tokenTask.getResult().getClaims() to route by role
                        // For now, go to MainActivity
                        NavigationUtils.go(LoginActivity.this, MainActivity.class, true);
                    });
                });
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
    private Boolean validatePassword() {
        if (userPassword == null || userPassword.getEditText() == null) return false;
        String val = userPassword.getEditText().getText() == null ? "" : userPassword.getEditText().getText().toString();
        if (val.isEmpty()) {
            userPassword.setError("Password is required");
            return false;
        }
        // Require at least one letter and one digit and one special char, no whitespace
        String passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&\\^#_+=~`\\-])[A-Za-z\\d@$!%*?&\\^#_+=~`\\-]{8,}$";

        if (!val.matches(passwordPattern)) {
            userPassword.setError("Password must contain letters, numbers and a special character");
            return false;
        }
        userPassword.setError(null);
        userPassword.setErrorEnabled(false);
        return true;
    }
    private boolean validateAll() {
        boolean emailOk = validateEmail();
        boolean passOk = validatePassword();
        return emailOk  && passOk;
    }

    private boolean isFastClick() {
        long now = System.currentTimeMillis();
        if (now - lastClickTime < 1000) {
            return true;
        }
        lastClickTime = now;
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // No user → stay on login page
        if (user == null) {
            return;
        }

        // Email not verified → force logout and stay on login page
        if (!user.isEmailVerified()) {
            FirebaseAuth.getInstance().signOut();
            return;
        }

        // Refresh token to get latest custom claims (optional but recommended)

            // Optional: read role
            // Map<String, Object> claims = task.getResult().getClaims();
            // String role = (String) claims.get("role");

            // Automatically go to main screen
            NavigationUtils.go(LoginActivity.this, MainActivity.class, true);
    }
}
