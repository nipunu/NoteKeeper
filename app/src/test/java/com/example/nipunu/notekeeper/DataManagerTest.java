package com.example.nipunu.notekeeper;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Nipunu on 20,June,2019
 */
public class DataManagerTest {

    private static DataManager dm;

    @BeforeClass
    public static void classSetUp(){
        dm = DataManager.getInstance();
    }

    @Before
    public void setUp(){
        dm = DataManager.getInstance();
        dm.getNotes().clear();
        dm.initializeExampleNotes();
    }

    @Test
    public void createNewNote() {
        dm = DataManager.getInstance();
        //creating the note details
        final CourseInfo course = dm.getCourse("android_async");
        final String noteTile = "Test note title";
        final String noteText = "Test note text";

        //creating  the empty new note
        int index = dm.createNewNote();
        //retrieve the new note associated with the index
        NoteInfo newNote = dm.getNotes().get(index);
        //populating the empty note with test values
        newNote.setCourse(course);
        newNote.setTitle(noteTile);
        newNote.setText(noteText);
        //retrieve the same note using the index
        NoteInfo compareNote = dm.getNotes().get(index);
        //compare values
        assertEquals(course,compareNote.getCourse());
        assertEquals(noteTile,compareNote.getTitle());
        assertEquals(noteText,compareNote.getText());
    }

    @Test
    public void confirmNotePosition(){
        //This test method verifies that a note created at a
        //particular index position is fixed when later retrieves it
        dm = DataManager.getInstance();
        //creating the note details
        final CourseInfo course = dm.getCourse("android_async");
        final String noteTile = "Test note title";
        final String noteText1 = "Test note text";
        final String noteText2 = "Second test note text";

        int noteIndex1 = dm.createNewNote();
        NoteInfo newNote1 = dm.getNotes().get(noteIndex1);
        newNote1.setCourse(course);
        newNote1.setTitle(noteTile);
        newNote1.setText(noteText1);

        int noteIndex2 = dm.createNewNote();
        NoteInfo newNote2 = dm.getNotes().get(noteIndex2);
        newNote2.setCourse(course);
        newNote2.setTitle(noteTile);
        newNote2.setText(noteText2);

        int foundNoteIndex1 = dm.findNote(newNote1);
        assertEquals(noteIndex1,foundNoteIndex1);

        int foundNoteIndex2 = dm.findNote(newNote2);
        assertEquals(noteIndex2,foundNoteIndex2);
    }

    @Test
    public void createNewNoteWithData(){
        final CourseInfo course = dm.getCourse("android_async");
        final String noteTile = "Test note title";
        final String noteText = "Test note text";

        int noteIndex = dm.createNewNote(course,noteTile,noteText);
        NoteInfo newNote = dm.getNotes().get(noteIndex);

        assertEquals(course,newNote.getCourse());
        assertEquals(noteTile,newNote.getTitle());
        assertEquals(noteText,newNote.getText());

    }
}