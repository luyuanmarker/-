package com.commander.eventeditor.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.commander.eventeditor.R;
import com.commander.eventeditor.model.Event;

import java.util.List;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder> {

    private List<Event> events;
    private int selectedPosition = -1;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Event event, int position);
        void onDeleteClick(Event event, int position);
    }

    public EventListAdapter(List<Event> events, OnItemClickListener listener) {
        this.events = events;
        this.listener = listener;
    }

    public void updateData(List<Event> newData) {
        this.events = newData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = events.get(position);
        holder.tvId.setText("事件 ID: " + event.getId());
        holder.tvTrigger.setText(Event.getTriggerName(event.getTrigger()));

        // Border color based on trigger type
        int colorRes;
        int trigger = event.getTrigger();
        if (trigger == 1 || trigger == 9) {
            colorRes = R.color.primary;
        } else if (trigger == 2 || trigger == 8) {
            colorRes = R.color.secondary;
        } else if (trigger == 3 || trigger == 4 || trigger == 7) {
            colorRes = R.color.warning;
        } else if (trigger == 6) {
            colorRes = R.color.danger;
        } else {
            colorRes = R.color.text_secondary;
        }
        holder.itemView.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        holder.itemView.getContext().getColor(colorRes))
        );

        holder.itemView.setSelected(position == selectedPosition);
        holder.itemView.setActivated(position == selectedPosition);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                selectedPosition = position;
                notifyDataSetChanged();
                listener.onItemClick(event, position);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(event, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return events != null ? events.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvTrigger, btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvEventId);
            tvTrigger = itemView.findViewById(R.id.tvEventTrigger);
            btnDelete = itemView.findViewById(R.id.btnDeleteEvent);
        }
    }
}
