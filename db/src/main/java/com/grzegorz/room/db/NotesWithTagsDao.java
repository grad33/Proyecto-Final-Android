package com.grzegorz.room.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Completable;

@Dao
public abstract class NotesWithTagsDao {
    @Insert
    protected abstract Long insertNote(Nota note);

    @Insert
    protected abstract List<Long> insertTags(List<Tag> tags);

    @Insert
    protected abstract void insertCrossRefs(List<NoteTagCrossRef> noteTagCrossRefs);

    @Query("DELETE FROM NoteTagCrossRef WHERE noteId = :noteId")
    protected abstract void deleteCrossRefsByNoteId(int noteId);

    public Completable insertNoteWithTags(NoteWithTags noteWithTags) {
        return Completable.fromAction(() -> {
            //First insert the note and get its Id
            int noteId = insertNote(noteWithTags.nota).intValue();

            //Then insert *only* the newly created tags
            List<Tag> newTags = noteWithTags.tags.stream().filter(tag -> tag.tagId == 0).collect(Collectors.toList());
            List<Long> newTagIds = insertTags(newTags);
            List<Tag> existingTags = noteWithTags.tags.stream().filter(tag -> tag.tagId != 0).collect(Collectors.toList());

            //Finally, create the note/tag cross references.
            insertCrossRefs(newTagIds, existingTags, noteId);
        });
    }

    @Update
    protected abstract void updateNote(Nota note);

    @Update
    protected abstract void updateTags(List<Tag> tags);


    public Completable updateNoteWithTags(NoteWithTags noteWithTags) {
        return Completable.fromAction(() -> {
            //First update the note
            updateNote(noteWithTags.nota);
            //Then insert *only* the newly created tags
            List<Tag> newTags = noteWithTags.tags.stream().filter(tag -> tag.tagId == 0).collect(Collectors.toList());
            List<Long> newTagIds = insertTags(newTags);
            //And update the existingTags in case they've somehow changed. To keep things simple, we update them without further checks
            List<Tag> existingTags = noteWithTags.tags.stream().filter(tag -> tag.tagId != 0).collect(Collectors.toList());
            updateTags(existingTags);

            //Finally, create the note/tag cross references.
            //Again, to keep things simple, we delete any existing cross reference
            deleteCrossRefsByNoteId(noteWithTags.nota.noteId);
            //and then we insert them all
            insertCrossRefs(newTagIds, existingTags, noteWithTags.nota.noteId);
        });
    }

    private void insertCrossRefs(List<Long> newTagIds, List<Tag> existingTags, int noteId) {
        List<Long> tagIdsToCrossReference = new ArrayList<>();
        tagIdsToCrossReference.addAll(newTagIds);
        tagIdsToCrossReference.addAll(existingTags.stream().map(t -> Long.valueOf(t.tagId)).collect(Collectors.toList()));
        ArrayList<NoteTagCrossRef> notesTagCrossRefs = new ArrayList<>();
        for (Long tagId : tagIdsToCrossReference) {
            NoteTagCrossRef noteTagCrossRef = new NoteTagCrossRef();
            noteTagCrossRef.noteId = noteId;
            noteTagCrossRef.tagId = tagId.intValue();
            notesTagCrossRefs.add(noteTagCrossRef);
        }
        insertCrossRefs(notesTagCrossRefs);
    }

    @Query("DELETE FROM NoteTagCrossRef WHERE tagId = :tagId")
    protected abstract void deleteCrossRefsByTagId(int tagId);

    @Delete
    protected abstract void deleteTag(Tag tag);

    public Completable deleteTagAndCrossReferences(Tag tag) {
        return Completable.fromAction(() -> {
            //First delete all the crossreferences
            deleteCrossRefsByTagId(tag.tagId);
            //Then delete the tag proper
            deleteTag(tag);
        });
    }

    @Delete
    protected abstract void deleteNote(Nota note);
    public Completable deleteNoteAndCrossReferences(Nota note) {
        return Completable.fromAction(() -> {
            //First delete all the crossreferences
            deleteCrossRefsByNoteId(note.noteId);
            //Then delete the note proper
            deleteNote(note);
        });
    }
}
