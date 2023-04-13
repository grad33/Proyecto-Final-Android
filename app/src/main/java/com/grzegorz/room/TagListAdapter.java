package com.grzegorz.room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.grzegorz.room.db.TagWithNotes;
import java.util.List;

public class TagListAdapter extends RecyclerView.Adapter<TagViewHolder> {

    private final List<TagWithNotes> _twn;
    private final TagClickListener _tagClickListener;

    public interface TagClickListener {
        void onTagDelete(int position);

        void onTagEdit(int position);
    }
    public TagListAdapter(List<TagWithNotes> twn,TagClickListener tagClickListener) {
        _twn = twn;
        _tagClickListener=tagClickListener;
    }

    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View tagView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_tag_layout, parent, false);
        TagViewHolder tagViewHolder = new TagViewHolder(tagView,_tagClickListener);
        return tagViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        holder.bind(_twn.get(position));
    }


    @Override
    public int getItemCount() {
        return _twn.size();
    }


}
