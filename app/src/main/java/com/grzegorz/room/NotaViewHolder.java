package com.grzegorz.room;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.grzegorz.room.db.Nota;
import com.grzegorz.room.R;

public class NotaViewHolder extends RecyclerView.ViewHolder {

    private final TextView _nameTextView;
    private final TextView _firstSurnameTextView;
    private final TextView _secondSurnameTextView;

    public NotaViewHolder(@NonNull View itemView, NotaAdapter.NotaClickListener notaClickListener) {
        super(itemView);
        _nameTextView = itemView.findViewById(R.id.name);
        _firstSurnameTextView = itemView.findViewById(R.id.first_surname);
        _secondSurnameTextView = itemView.findViewById(R.id.second_surname);
        ImageButton buttonDelete = itemView.findViewById(R.id.button_delete);
        buttonDelete.setOnClickListener(view -> notaClickListener.onNoteDelete(getAdapterPosition()));
        ImageButton buttonEdit = itemView.findViewById(R.id.button_details);
        buttonEdit.setOnClickListener(view -> notaClickListener.onNotaEdit(getAdapterPosition()));
    }

    public void bind(Nota nota) {
        _nameTextView.setText(nota.name);
        _firstSurnameTextView.setText(nota.titulo);
        _secondSurnameTextView.setText(nota.cuerpo);
    }
}