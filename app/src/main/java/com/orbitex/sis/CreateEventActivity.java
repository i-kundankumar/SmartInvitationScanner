package com.orbitex.sis;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreateEventActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private ShapeableImageView imgCover;
    private ImageView btnChangeCover;

    private TextInputEditText etTitle, etOrganizer,
            etEventDate, etLocation, etPrice, etDescription;
    private TextInputLayout tilPrice;
    private MaterialAutoCompleteTextView etEventType;

    private Chip chipFree, chipPaid;
    private MaterialButton btnContinue;
    private TextView coverTxt;

    private LocalDate selectedDate;
    private LocalTime selectedTime;
    private Uri coverImageUri;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_event);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initViews();
        setupImagePicker();
        setupListeners();
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        etEventDate.setOnClickListener(v -> showDatePicker());

        chipFree.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) tilPrice.setVisibility(View.GONE);
        });

        chipPaid.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) tilPrice.setVisibility(View.VISIBLE);
        });

        btnChangeCover.setOnClickListener(v ->
                imagePickerLauncher.launch("image/*"));

        btnContinue.setOnClickListener(v -> saveEvent());
    }

    private void saveEvent() {
        String title = etTitle.getText().toString().trim();
        String organizer = etOrganizer.getText().toString().trim();
        String type = etEventType.getText().toString().trim();
        String dateTime = etEventDate.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        boolean isPaid = chipPaid.isChecked();
        String price = etPrice.getText().toString().trim();

        if (title.isEmpty() || organizer.isEmpty() || type.isEmpty()
                || dateTime.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isPaid && price.isEmpty()) {
            etPrice.setError("Enter price");
            return;
        }

        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;

        btnContinue.setEnabled(false);
        btnContinue.setText("Saving...");

        // If a cover image was selected, upload it first
        if (coverImageUri != null) {
            uploadImageAndSaveEvent(title, organizer, type, dateTime, location, description, isPaid, price, user.getUid());
        } else {
            // Otherwise, save the event without an image URL
            saveEventToFirestore(title, organizer, type, dateTime, location, description, isPaid, price, user.getUid(), null);
        }
    }

    private void uploadImageAndSaveEvent(String title, String organizer, String type,
                                         String dateTime, String location, String description,
                                         boolean isPaid, String price, String uid) {

        btnContinue.setEnabled(false);
        btnContinue.setText("Uploading...");

        MediaManager.get().upload(coverImageUri)
                .option("folder", "event_covers")
                .option("resource_type", "image")
                .option("quality", "auto")
                .option("fetch_format", "auto")
                .callback(new UploadCallback() {
                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String imageUrl = resultData.get("secure_url").toString();

                        saveEventToFirestore(title, organizer, type,
                                dateTime, location, description, isPaid, price, uid,imageUrl);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        btnContinue.setEnabled(true);
                        btnContinue.setText("SAVE & CONTINUE");
                        Toast.makeText(CreateEventActivity.this,
                                "Image upload failed", Toast.LENGTH_SHORT).show();
                    }

                    @Override public void onStart(String requestId) {}
                    @Override public void onProgress(String requestId, long bytes, long total) {}
                    @Override public void onReschedule(String requestId, ErrorInfo error) {}
                })
                .dispatch();
    }


    private void saveEventToFirestore(String title, String organizer, String type, String dateTime, String location, String description, boolean isPaid, String price, String userId, String imageUrl) {
        Map<String, Object> event = new HashMap<>();
        event.put("title", title);
        event.put("organizer", organizer);
        event.put("type", type);
        event.put("dateTime", dateTime);
        event.put("location", location);
        event.put("description", description);
        event.put("isPaid", isPaid);
        event.put("price", isPaid ? price : "0");
        event.put("userId", userId);
        event.put("createdAt", FieldValue.serverTimestamp());

        // Add the cover image URL to the map if it exists
        if (imageUrl != null) {
            event.put("coverImageUrl", imageUrl);
        }

        db.collection("events")
                .add(event)
                .addOnSuccessListener(doc -> {
                    Toast.makeText(this, "Event created successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(this::handleFailure);
    }

    private void handleFailure(Exception e) {
        btnContinue.setEnabled(true);
        btnContinue.setText("Continue");
        Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }


    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        coverImageUri = uri;
                        imgCover.setImageURI(uri);
                        coverTxt.setVisibility(View.GONE);
                    }
                }
        );
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        imgCover = findViewById(R.id.imgCover);
        btnChangeCover = findViewById(R.id.btnChangeCover);
        coverTxt = findViewById(R.id.coverText);

        etTitle = findViewById(R.id.et_title);
        etOrganizer = findViewById(R.id.et_organizer);
        etEventType = findViewById(R.id.et_event_type);
        etEventDate = findViewById(R.id.et_event_date);
        etLocation = findViewById(R.id.et_location);
        etPrice = findViewById(R.id.et_price);
        etDescription = findViewById(R.id.et_description);

        tilPrice = findViewById(R.id.til_price);

        chipFree = findViewById(R.id.chip_free);
        chipPaid = findViewById(R.id.chip_paid);

        btnContinue = findViewById(R.id.btnContinue);

        String[] eventTypes = {"Conference", "Meetup", "Workshop", "Birthday", "Wedding"};
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, eventTypes);
        etEventType.setAdapter(adapter);

    }

    private void showDatePicker() {
        MaterialDatePicker<Long> datePicker =
                MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Select Event Date")
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            Instant instant = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                instant = Instant.ofEpochMilli(selection);
            }
            ZoneId zoneId = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                zoneId = ZoneId.systemDefault();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                selectedDate = instant.atZone(zoneId).toLocalDate();
            }

            showTimePicker();
        });

        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
    }

    private void showTimePicker() {
        MaterialTimePicker timePicker =
                new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_12H)
                        .setHour(10)
                        .setMinute(0)
                        .setTitleText("Select Time")
                        .build();

        timePicker.addOnPositiveButtonClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                selectedTime = LocalTime.of(timePicker.getHour(), timePicker.getMinute());
            }

            LocalDateTime dateTime = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                dateTime = LocalDateTime.of(selectedDate, selectedTime);
            }

            DateTimeFormatter formatter = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
                etEventDate.setText(dateTime.format(formatter));
            }
        });

        timePicker.show(getSupportFragmentManager(), "TIME_PICKER");
    }
}
