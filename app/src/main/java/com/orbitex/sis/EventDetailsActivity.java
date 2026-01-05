package com.orbitex.sis;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.orbitex.sis.models.Event;

public class EventDetailsActivity extends AppCompatActivity {

    private ImageView imgCover;
    private TextView tvTitle, tvDate, tvLocation, tvOrganizer, tvDescription, tvPrice;

    private FirebaseFirestore db;
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imgCover = findViewById(R.id.imgCover);
        tvTitle = findViewById(R.id.tvTitle);
        tvDate = findViewById(R.id.tvDate);
        tvLocation = findViewById(R.id.tvLocation);
        tvOrganizer = findViewById(R.id.tvOrganizer);
        tvDescription = findViewById(R.id.tvDescription);
        tvPrice = findViewById(R.id.tvPrice);

        db = FirebaseFirestore.getInstance();

        eventId = getIntent().getStringExtra("event_id");
        if (eventId == null) finish();

        loadEvent();
    }

    private void loadEvent() {
        db.collection("events").document(eventId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) return;

                    Event event = doc.toObject(Event.class);
                    if (event == null) return;

                    tvTitle.setText(event.getTitle());
                    tvDate.setText(event.getDateTime());
                    tvLocation.setText(event.getLocation());
                    tvOrganizer.setText(event.getOrganizer());
                    tvDescription.setText(event.getDescription());

                    if (event.getIsPaid()) {
                        tvPrice.setText("₹ " + event.getPrice());
                    } else {
                        tvPrice.setText("Free");
                    }

                    if (event.getCoverImageUrl() != null) {
                        Glide.with(this)
                                .load(event.getCoverImageUrl())
                                .centerCrop()
                                .into(imgCover);
                    }
                });
    }
}