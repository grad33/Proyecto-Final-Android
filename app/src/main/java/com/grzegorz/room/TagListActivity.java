package com.grzegorz.room;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grzegorz.room.db.AppDatabase;
import com.grzegorz.room.db.Tag;
import com.grzegorz.room.db.TagWithNotes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class TagListActivity extends AppCompatActivity {

    private List<Tag> tags_ = new ArrayList<>();
    private List<TagWithNotes> twn=new ArrayList<>();


    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_list);
        AppDatabase appDatabase = ((RoomApplication) getApplication()).appDatabase;

        appDatabase.TagsDao().getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Tag>>() {
                    @SuppressLint("CheckResult")
                    @Override
                    public void accept(List<Tag> tags) {
                        tags_=tags;
                        RecyclerView recyclerView = findViewById(R.id.tag_list);
                        //recyclerView.setAdapter
                        TagListAdapter ta=new TagListAdapter(twn, new TagListAdapter.TagClickListener(){
                            @Override
                            public void onTagDelete(int position){

                                TagWithNotes tagWithNotes = twn.get(position);
                                Tag tag = tagWithNotes.tag;

                                appDatabase.NotesWithTagsDao().deleteTagAndCrossReferences(tag)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(() -> {
                                            twn.remove(position);
                                            recyclerView.getAdapter().notifyItemRemoved(position);
                                        });
                            }
                            @Override
                            public void onTagEdit(int position){
                                TagWithNotes tag=twn.get(position);
                                if (tag.tag.tagId > 0) {
                                    appDatabase.TagsDao().updateTag(tag.tag).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe();

                                }else{
                                    appDatabase.TagsDao().insertTag(tag.tag).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe();
                                }
                            }
                        });

                        recyclerView.setLayoutManager(new LinearLayoutManager(TagListActivity.this));
                        recyclerView.setAdapter(ta);
                        appDatabase.TagsDao().getTagsWithNotes().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe((tagsWithNotes -> {
                            twn.clear();
                            twn.addAll(tagsWithNotes);
                            ta.notifyDataSetChanged();

                        }));
                    }
                });

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Tags");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tag_list_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }if(item.getItemId() == R.id.menu_add_tag){
            Tag tag=new Tag();
            tag.tag="";
            AppDatabase appDatabase = ((RoomApplication) getApplication()).appDatabase;
            Consumer<Tag> consumer=tag1 -> {
                tag1.tag="";
                Completable completable = tag1.tagId > 0 ? appDatabase.TagsDao().updateTag(tag1) : appDatabase.TagsDao().insertTag(tag1);
                completable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe();};
        }
        return super.onOptionsItemSelected(item);
    }
}
