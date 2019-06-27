package com.example.nipunu.notekeeper;

import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.pressBack;
import static org.hamcrest.Matchers.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;

/**
 * Created by Nipunu on 27,May,2019
 */

@RunWith(AndroidJUnit4.class)
public class NoteCreationTest {
    static DataManager dataManager;

    @BeforeClass
    public static void classSetUp(){
        dataManager = DataManager.getInstance();
    }

    //declare the test environment
    @Rule
    public ActivityTestRule<NoteListActivity> noteListActivityRule =
            new ActivityTestRule<>(NoteListActivity.class);

    @Test
    public void createNewNote(){
        final CourseInfo course = dataManager.getCourse("java_lang");
        final String noteText = "Android test note text";
        final String noteTitle = "Android test note title";
        //get a reference to the View and perform an action on the view- two line version
        //ViewInteraction fabClickNewNote = onView(withId(R.id.fab));
        //fabClickNewNote.perform(click());
        //one line version
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.spinner_courses)).perform(click());
        onData(allOf(instanceOf(CourseInfo.class),equalTo(course))).perform(click());
        onView(withId(R.id.spinner_courses)).check(matches(withSpinnerText
                (containsString(course.getTitle()))));
        onView(withId(R.id.text_note_title)).perform(typeText(noteTitle))
                .check(matches(withText(containsString(noteTitle))));
        onView(withId(R.id.text_note_text)).perform(typeText(noteText),
                closeSoftKeyboard());
        onView(withId(R.id.text_note_text)).check(matches(withText(containsString(noteText))));
        pressBack();
        //logic to test new note creation
        int newNoteIndex = dataManager.getNotes().size()-1;
        NoteInfo newNote = dataManager.getNotes().get(newNoteIndex);
        assertEquals(course,newNote.getCourse());
        assertEquals(noteTitle,newNote.getTitle());
        assertEquals(noteText,newNote.getText());

    }

}