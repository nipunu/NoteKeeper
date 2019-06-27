package com.example.nipunu.notekeeper;

import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;

import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.Espresso.pressBack;
import static org.hamcrest.Matchers.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;

/**
 * Created by Nipunu on 07,June,2019
 */
public class NextThroughNotesTest {

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void nextThroughNotes(){
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_notes));

        onView(withId(R.id.list_items)).perform(RecyclerViewActions.actionOnItemAtPosition(0,click()));

        List<NoteInfo> notes = DataManager.getInstance().getNotes();

        for(int index = 0; index < notes.size(); index++) {

            NoteInfo note = notes.get(index);

            onView(withId(R.id.spinner_courses)).check(
                    matches(withSpinnerText(note.getCourse().getTitle())));
            onView(withId(R.id.text_note_title)).check(matches(withText(note.getTitle())));
            onView(withId(R.id.text_note_text)).check(matches(withText(note.getText())));
            //checking whether menu item are enabled
            if(index < notes.size() -1)
            onView(allOf(withId(R.id.action_next),isEnabled())).perform(click());
        }
        //checking whether last menu item is disabled
        onView(withId(R.id.action_next)).check(matches(not(isEnabled())));
    }

}