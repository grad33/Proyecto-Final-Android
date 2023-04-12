package com.grzegorz.room.db;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class TagWithNotes {
    @Embedded
    public Tag tag;
    @Relation(parentColumn = "tagId",entityColumn = "noteId", associateBy = @Junction(NoteTagCrossRef.class))
    public List<Nota> notes;}

