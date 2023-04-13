package com.grzegorz.room;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.grzegorz.room.db.AppDatabase;
import com.grzegorz.room.db.NoteWithTags;
import com.grzegorz.room.db.Tag;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class EditNotaActivity extends AppCompatActivity {

    public static final String TAG = EditNotaActivity.class.getName();
    public static final String NOTA_ID_KEY = "NOTE_ID";
    public static final String TAG_ID_KEY = "TAG_ID";
    private ChipGroup chipGroup;
    private List<Tag> taglist=new ArrayList<>();
    private CompositeDisposable disposable=new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_nota);
        AppDatabase appDatabase = ((RoomApplication) getApplication()).appDatabase;
        chipGroup=findViewById(R.id.tag_chip_group);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Añadir/Editar Nota");
        loadTags();
        Consumer<NoteWithTags> notaConsumer = new Consumer<NoteWithTags>() {
            @Override
            public void accept(NoteWithTags nota) {
                EditText NameText = findViewById(R.id.edit_nota_name);
                NameText.setText(nota.nota.titulo);
                EditText DescriptionText = findViewById(R.id.edit_nota_title);
                DescriptionText.setText(nota.nota.cuerpo);

                for (Tag tag : nota.tags) {
                    for (int i = 0; i < chipGroup.getChildCount(); i++) {
                        Chip chip = (Chip) chipGroup.getChildAt(i);
                        chip.setCheckable(true);
                        if (chip.getText().toString().equals(tag.tag)) {

                            chip.setChecked(true);

                        }
                    }
                }

                findViewById(R.id.save_nota_button).setOnClickListener(view -> {
                    nota.nota.titulo = NameText.getText().toString();
                    nota.nota.cuerpo = DescriptionText.getText().toString();

                    ChipGroup chipGroup = findViewById(R.id.tag_chip_group);
                    List<Tag> tagsToPersist = new ArrayList<>();
                    for (int i = 0; i < taglist.size(); i++) {
                        if (((Chip) chipGroup.getChildAt(i)).isChecked()) {
                            tagsToPersist.add(taglist.get(i));
                        }
                    }
                    nota.tags = tagsToPersist;

                    Action navigateToMainActivityAction = new Action() {
                        @Override
                        public void run() throws Throwable {
                            Intent intent = new Intent();
                            intent.setClass(EditNotaActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    };

                    if (nota.nota.noteId > 0) {
                        appDatabase.NotesWithTagsDao().updateNoteWithTags(nota).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(navigateToMainActivityAction);
                    } else {
                        appDatabase.NotesWithTagsDao().insertNoteWithTags(nota).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(navigateToMainActivityAction);
                    }
                });
            }
        };

        int notaId = getIntent().getIntExtra(NOTA_ID_KEY, 0);
        if (notaId > 0) {
            appDatabase.NotaDao().findWithTags(notaId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(notaConsumer);
        } else {
            try {
                notaConsumer.accept(NoteWithTags.Empty());
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tag_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id= item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }if(id == R.id.menu_add_tag_2){
            AlertDialog.Builder builder=new AlertDialog.Builder(EditNotaActivity.this);
            builder.setTitle("Nuevo tag");
            EditText et=new EditText(EditNotaActivity.this);
            et.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(et);
            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String s=et.getText().toString();
                    Tag tag=new Tag();
                    tag.tag=s;
                    AppDatabase appDatabase = ((RoomApplication) getApplication()).appDatabase;
                    Consumer<Tag> consumer=tag1 -> {
                        tag1.tag=s;
                        Completable completable = tag1.tagId > 0 ? appDatabase.TagsDao().updateTag(tag1) : appDatabase.TagsDao().insertTag(tag1);
                        completable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(()->{
                            Chip chip=new Chip(EditNotaActivity.this);
                            chip.setText(tag1.tag);

                            chipGroup.addView(chip);
                            taglist.add(tag1);
                        });
                    };
                    int tagId = getIntent().getIntExtra(TAG_ID_KEY, 0);
                    if (tagId > 0) {
                        disposable.add(
                                appDatabase.TagsDao().find(tagId)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(consumer, throwable -> {
                                            Toast.makeText(EditNotaActivity.this, "Error loading tag", Toast.LENGTH_SHORT).show();
                                            throwable.printStackTrace();
                                        })
                        );
                    } else {
                        try {
                            consumer.accept(new Tag());
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }
                }

            });
            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            builder.show();

        }
        return super.onOptionsItemSelected(item);
    }
    private void loadTags() {
        AppDatabase appDatabase = ((RoomApplication) getApplication()).appDatabase;
        disposable.add(appDatabase.TagsDao().getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tags -> {
                    taglist.clear();
                    taglist.addAll(tags);

                    for (Tag tag : taglist) {
                        Chip chip = new Chip(EditNotaActivity.this);
                        chip.setText(tag.tag);
                        chip.setClickable(true);
                        chipGroup.addView(chip);
                        chip.setCheckable(true);
                    }
                }));
    }
}
