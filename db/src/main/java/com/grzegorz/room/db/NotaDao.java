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
public interface NotaDao {
    @Query("SELECT * FROM Nota")
    Single<List<Nota>> getAll();

    @Query("SELECT * FROM Nota WHERE noteId = :id")
    Single<Nota> find(int id);

    @Transaction
    @Query("SELECT * FROM Nota")
    Single<List<NoteWithTags>> getNotesWithTags();

    @Transaction
    @Query("SELECT * FROM Nota WHERE noteId = :noteId")
    Single<NoteWithTags> findWithTags(int noteId);

    @Insert
    Completable insertNota(Nota nota);

    @Update
    Completable updateNota(Nota nota);

    @Delete
    Completable deleteNota(Nota nota);

}