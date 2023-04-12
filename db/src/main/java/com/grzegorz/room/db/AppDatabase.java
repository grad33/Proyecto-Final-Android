package com.grzegorz.room.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Nota.class,Tag.class, NoteTagCrossRef.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract NotaDao NotaDao();
    public abstract TagsDao TagsDao();
    public abstract NotesWithTagsDao NotesWithTagsDao();

    public static AppDatabase getInstance(Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, "notas-database").build();
    }
}