package com.orbitex.sis;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;

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

        Map<String, Object> event = new HashMap<>();
        event.put("title", title);
        event.put("organizer", organizer);
        event.put("type", type);
        event.put("dateTime", dateTime);
        event.put("location", location);
        event.put("description", description);
        event.put("isPaid", isPaid);
        event.put("price", isPaid ? price : "0");
        event.put("userId", user.getUid());
        event.put("createdAt", FieldValue.serverTimestamp());

        btnContinue.setEnabled(false);

        db.collection("events")
                .add(event)
                .addOnSuccessListener(doc -> {
                    Toast.makeText(this, "Event created successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnContinue.setEnabled(true);
                    Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        coverImageUri = uri;
                        imgCover.setImageURI(uri);
                    }
                }
        );
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        imgCover = findViewById(R.id.imgCover);
        btnChangeCover = findViewById(R.id.btnChangeCover);

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