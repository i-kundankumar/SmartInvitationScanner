package com.orbitex.sis;

import android.os.Build;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class CreateEventActivity extends AppCompatActivity {

    private TextInputEditText etEventDate;
    private LocalDate selectedDate;
    private LocalTime selectedTime;
    private ImageButton btnBack;

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
        etEventDate = findViewById(R.id.et_event_date);
        btnBack = findViewById(R.id.btnBack);
        etEventDate.setOnClickListener(v -> showDatePicker());
        btnBack.setOnClickListener(v -> {
            NavigationUtils.go(CreateEventActivity.this, MainActivity.class, true);
        });
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