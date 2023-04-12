package com.grzegorz.room.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;


@Entity(primaryKeys = {"noteId", "tagId"})
public class NoteTagCrossRef {
    public int noteId;
    @ColumnInfo(index = true)
    public int tagId;}

