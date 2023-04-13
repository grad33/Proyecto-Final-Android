package com.grzegorz.room;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.grzegorz.room.db.TagWithNotes;

import io.reactivex.rxjava3.annotations.NonNull;

public class TagViewHolder extends RecyclerView.ViewHolder {

    private TagWithNotes twn;
    private final EditText _tag;
    private final TextView _count;
    private final ImageButton deleteTagButton;
    private final ImageButton saveTagButton;

    public TagViewHolder(@NonNull View itemView, TagListAdapter.TagClickListener tagClickListener) {
        super(itemView);
        _tag = itemView.findViewById(R.id.tag_name_edittext);
        _count = itemView.findViewById(R.id.tag_count);
        deleteTagButton = itemView.findViewById(R.id.delete_tag_button);
        saveTagButton = itemView.findViewById(R.id.save_tag_button);
        deleteTagButton.setOnClickListener(view -> tagClickListener.onTagDelete(getAdapterPosition()));
        saveTagButton.setOnClickListener(view -> {
            twn.tag.tag=
        _tag.getText().toString();
        tagClickListener.onTagEdit(getAdapterPosition());
        });
    }

    public void bind(TagWithNotes tag) {
        twn=tag;
        _tag.setText(tag.tag.tag);
            Log.d("hola",tag.tag.tag);

        //_count.setText(tag.notes.size());
    }
}