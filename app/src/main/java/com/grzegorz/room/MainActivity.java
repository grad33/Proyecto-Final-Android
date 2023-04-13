package com.grzegorz.room;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grzegorz.room.db.AppDatabase;
import com.grzegorz.room.db.Nota;

import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private List<Nota> _notas;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppDatabase appDatabase = ((RoomApplication) getApplication()).appDatabase;
        appDatabase.NotaDao().getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Nota>>() {
                    @SuppressLint("CheckResult")
                    @Override
                    public void accept(List<Nota> notas) {
                        _notas = notas;
                        RecyclerView recyclerView = findViewById(R.id.recyclerview);
                        recyclerView.setAdapter(new NotaAdapter(_notas, new NotaAdapter.NotaClickListener() {
                            @Override
                            public void onNotaEdit(int position) {
                                Nota nota = _notas.get(position);
                                Intent intent = new Intent();
                                intent.setClass(MainActivity.this, EditNotaActivity.class);
                                intent.putExtra(EditNotaActivity.NOTA_ID_KEY, nota.noteId);
                                startActivity(intent);
                            }

                            @Override
                            public void onNoteDelete(int position) {
                                Nota nota = _notas.get(position);
                                appDatabase.NotaDao().deleteNota(nota)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Action() {
                                            @Override
                                            public void run() {
                                                _notas.remove(position);
                                                Objects.requireNonNull(recyclerView.getAdapter()).notifyItemRemoved(position);
                                            }
                                        });
                            }
                        }));
                        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

                        // Set up the action bar
                        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
                        getSupportActionBar().setTitle("Mis Notas");

                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem moreOptionsItem = menu.findItem(R.id.dots);
        moreOptionsItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                PopupMenu popup = new PopupMenu(MainActivity.this, findViewById(R.id.dots));
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.tag_list) {
                            Intent intent = new Intent(MainActivity.this, TagListActivity.class);
                            startActivity(intent);
                            return true;
                        }
                        return false;
                    }
                });

                popup.show();
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_nota) {
            Intent intent = new Intent(MainActivity.this, EditNotaActivity.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.tag_list) {
            Intent intent = new Intent(MainActivity.this, TagListActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
    }
}