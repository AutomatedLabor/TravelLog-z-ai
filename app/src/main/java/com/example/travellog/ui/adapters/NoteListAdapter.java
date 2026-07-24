package com.example.travellog.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travellog.R;
import com.example.travellog.data.db.entity.PlaceNote;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.NoteViewHolder> {

    public interface OnNoteClickListener {
        void onNoteClick(PlaceNote note, int position);
        void onNoteLongClick(PlaceNote note, int position);
    }

    private final Context context;
    private List<PlaceNote> notes = new ArrayList<>();
    private final OnNoteClickListener listener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy h:mm a", Locale.US);

    public NoteListAdapter(Context context, OnNoteClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setNotes(List<PlaceNote> notes) {
        this.notes = notes != null ? notes : new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        PlaceNote note = notes.get(position);
        holder.bind(note);
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvNoteTitle;
        private final TextView tvNoteContent;
        private final TextView tvNoteDate;

        NoteViewHolder(View itemView) {
            super(itemView);
            tvNoteTitle = itemView.findViewById(R.id.tvNoteTitle);
            tvNoteContent = itemView.findViewById(R.id.tvNoteContent);
            tvNoteDate = itemView.findViewById(R.id.tvNoteDate);
        }

        void bind(PlaceNote note) {
            tvNoteTitle.setText(note.title != null ? note.title : "Untitled Note");
            tvNoteContent.setText(note.content != null ? note.content : "");
            tvNoteDate.setText(dateFormat.format(new Date(note.createdAt)));

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onNoteClick(note, getAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onNoteLongClick(note, getAdapterPosition());
                }
                return true;
            });
        }
    }
}
