package com.grzegorz.room.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface TagsDao {

    @Query("SELECT * FROM tags")
    Single<List<Tag>> getAll();

    @Query("SELECT * FROM Tags WHERE TagId = :tagId")
    Single<Tag> find(int tagId);

    @Transaction
    @Query("SELECT * FROM tags")
    Single<List<TagWithNotes>> getTagsWithNotes();

    @Transaction
    @Query("SELECT * FROM tags WHERE TagId = :tagId")
    Single<TagWithNotes> findWithTags(int tagId);

    @Insert
    Completable insertTag(Tag tag);

    @Insert
    Single<List<Long>> insertTags(List<Tag> tags);

    @Update
    Completable updateTag(Tag tag);

    @Delete
    Completable deleteTag(Tag Tag);
}
