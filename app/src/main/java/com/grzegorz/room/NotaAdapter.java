package com.grzegorz.room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.grzegorz.room.db.Nota;
import com.grzegorz.room.R;

import java.util.List;


public class NotaAdapter extends RecyclerView.Adapter<NotaViewHolder> {

    private final List<Nota> _notas;
    private final NotaClickListener _notaClickListener;

    public interface NotaClickListener {
        void onNotaEdit(int position);

        void onNoteDelete(int position);
    }

    public NotaAdapter(List<Nota> notas, NotaClickListener notaClickListener) {
        _notas = notas;
        _notaClickListener = notaClickListener;
    }

    @Override
    public NotaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View NotaView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_nota_item, parent, false);
        NotaViewHolder notaViewHolder = new NotaViewHolder(NotaView, _notaClickListener);
        return notaViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NotaViewHolder holder, int position) {
        holder.bind(_notas.get(position));
    }

    @Override
    public int getItemCount() {
        return _notas.size();
    }
}