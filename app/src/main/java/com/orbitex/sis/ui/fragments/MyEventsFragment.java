package com.orbitex.sis.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.orbitex.sis.CreateEventActivity;
import com.orbitex.sis.EventDetailsActivity;
import com.orbitex.sis.R;
import com.orbitex.sis.adapters.MyEventsAdapter;
import com.orbitex.sis.models.Event;

import java.util.ArrayList;
import java.util.List;

public class MyEventsFragment extends Fragment {

    private RecyclerView recyclerView;
    private MyEventsAdapter adapter;
    private List<Event> events = new ArrayList<>();

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private LinearLayout layoutEmpty;
    private MaterialButton btnCreateEvent;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_events, container, false);

        recyclerView = view.findViewById(R.id.recyclerEvents);
        recyclerView.setHasFixedSize(true);
        adapter = new MyEventsAdapter(requireContext(), events, event -> {
            Intent i = new Intent(requireContext(), EventDetailsActivity.class);
            i.putExtra("event_id", event.getId());
            startActivity(i);
        });
        recyclerView.setAdapter(adapter);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
        btnCreateEvent = view.findViewById(R.id.btnCreateEvent);

        btnCreateEvent.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), CreateEventActivity.class));
        });

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadMyEvents();

        return view;
    }

    private void loadMyEvents() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;

        db.collection("events")
                .whereEqualTo("userId", user.getUid())
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snapshot -> {
                    events.clear();

                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Event event = doc.toObject(Event.class);
                        if (event != null) {
                            event.setId(doc.getId());
                            events.add(event);
                        }
                    }

                    if (events.isEmpty()) {
                        recyclerView.setVisibility(View.GONE);
                        layoutEmpty.setVisibility(View.VISIBLE);
                    } else {
                        layoutEmpty.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(),
                                "Failed to load events",
                                Toast.LENGTH_SHORT).show());
    }

}

