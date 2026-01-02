package com.orbitex.sis.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.orbitex.sis.R;
import com.orbitex.sis.models.Event;

import org.jspecify.annotations.NonNull;

import java.util.List;

public class MyEventsAdapter extends RecyclerView.Adapter<MyEventsAdapter.EventVH> {

    private final List<Event> events;
    private final Context context;

    public MyEventsAdapter(Context context, List<Event> events) {
        this.context = context;
        this.events = events;
    }

    @NonNull
    @Override
    public EventVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_event, parent, false);
        return new EventVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EventVH holder, int position) {
        Event event = events.get(position);

        holder.tvTitle.setText(event.getTitle());
        holder.tvDate.setText(event.getDateTime());
        holder.tvLocation.setText(event.getLocation());

        if (event.getCoverImageUrl() != null) {
            Glide.with(context)
                    .load(event.getCoverImageUrl())
                    .centerCrop()
                    .into(holder.imgCover);
        }
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    static class EventVH extends RecyclerView.ViewHolder {
        ImageView imgCover;
        TextView tvTitle, tvDate, tvLocation;

        EventVH(View itemView) {
            super(itemView);
            imgCover = itemView.findViewById(R.id.imgCover);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvLocation = itemView.findViewById(R.id.tvLocation);
        }
    }
}

