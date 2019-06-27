package com.example.nipunu.notekeeper;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

public class NoteActivity extends AppCompatActivity {
    public final String TAG = getClass().getSimpleName();
    public static final String NOTE_INFO = "com.example.nipunu.notekeeper.NOTE_INFO";
    public static final String NOTE_POSITION = "com.example.nipunu.notekeeper.NOTE_POSITION";
    public static final String ORIGINAL_COURSE_ID = "com.example.nipunu.notekeeper.ORIGINAL_COURSE_ID";
    public static final String ORIGINAL_NOTE_TITLE = "com.example.nipunu.notekeeper.ORIGINAL_NOTE_TITLE";
    public static final String ORIGINAL_NOTE_TEXT = "com.example.nipunu.notekeeper.ORIGINAL_NOTE_TEXT";
    public static final int POSITION_NOT_SET = -1;
    private NoteInfo selectedNoteInfo;
    private boolean isNewNote;
    private EditText textNoteText;
    private EditText textNoteTitle;
    private Spinner spinnerCourses;
    private boolean isCanceling;
    private int newNotePosition;
    private String originalNoteCourseId;
    private String originalNoteTitle;
    private String originalNoteText;
    private int selectedNotePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        spinnerCourses = (Spinner) findViewById(R.id.spinner_courses);
        List<CourseInfo> listCourses = DataManager.getInstance().getCourses();
        ArrayAdapter<CourseInfo> adapterCourses =
                new ArrayAdapter<CourseInfo>(this,android.R.layout.simple_spinner_item,listCourses);
        adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourses.setAdapter(adapterCourses);

        readDisplayStateValues();
        if(savedInstanceState == null) {
            saveOriginalNoteValues();
        }
        else{
            restoreOriginalStateValues(savedInstanceState);
        }

        textNoteTitle = (EditText) findViewById(R.id.text_note_title);
        textNoteText = (EditText) findViewById(R.id.text_note_text);

        if(!isNewNote)
        displayNote(spinnerCourses, textNoteTitle, textNoteText);

        Log.d(TAG,"onCreate");
    }

    private void restoreOriginalStateValues(Bundle savedInstanceState) {
        originalNoteCourseId = savedInstanceState.getString(ORIGINAL_COURSE_ID);
        originalNoteText = savedInstanceState.getString(ORIGINAL_NOTE_TITLE);
        originalNoteText = savedInstanceState.getString(ORIGINAL_NOTE_TEXT);
    }

    private void saveOriginalNoteValues() {
        if(isNewNote)
            return;
        originalNoteCourseId = selectedNoteInfo.getCourse().getCourseId();
        originalNoteTitle = selectedNoteInfo.getTitle();
        originalNoteText = selectedNoteInfo.getText();
    }

    private void displayNote(Spinner spinnerCourses, EditText textNoteTile, EditText textNoteText) {

            List<CourseInfo> courseList = DataManager.getInstance().getCourses();
            int courseIndex = courseList.indexOf(selectedNoteInfo.getCourse());
            spinnerCourses.setSelection(courseIndex);
            textNoteTile.setText(selectedNoteInfo.getTitle());
            textNoteText.setText(selectedNoteInfo.getText());
    }

    private void readDisplayStateValues() {
        Intent intent = getIntent();
//        selectedNoteInfo = intent.getParcelableExtra(NOTE_INFO);
        selectedNotePosition = intent.getIntExtra(NOTE_POSITION, POSITION_NOT_SET);
//        isNewNote = selectedNoteInfo == null;
        isNewNote = selectedNotePosition == POSITION_NOT_SET;
        if(!isNewNote) {
            selectedNoteInfo = DataManager.getInstance().getNotes().get(selectedNotePosition);
        }
        else{
            createNewNote();
        }
    }

    private void createNewNote() {
            DataManager dm = DataManager.getInstance();
        newNotePosition = dm.createNewNote();
        selectedNoteInfo = dm.getNotes().get(newNotePosition);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ORIGINAL_COURSE_ID,originalNoteCourseId);
        outState.putString(ORIGINAL_NOTE_TITLE,originalNoteTitle);
        outState.putString(ORIGINAL_NOTE_TEXT,originalNoteText);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isCanceling){
            Log.i(TAG,"Canceling note at position " + newNotePosition);
            if(isNewNote){
                DataManager.getInstance().removeNote(newNotePosition);
            }
            else{
                storePreviousNoteValues();
            }
        }
        else {
            saveNote();
        }
        Log.d(TAG,"onPause");
    }

    private void storePreviousNoteValues() {
        CourseInfo course = DataManager.getInstance().getCourse(originalNoteCourseId);
        selectedNoteInfo.setCourse(course);
        selectedNoteInfo.setTitle(originalNoteTitle);
        selectedNoteInfo.setText(originalNoteText);
    }

    private void saveNote() {
        selectedNoteInfo.setCourse((CourseInfo) spinnerCourses.getSelectedItem());
        selectedNoteInfo.setTitle(textNoteTitle.getText().toString());
        selectedNoteInfo.setText(textNoteText.getText().toString());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send_mail) {
            sendEmail();
            return true;
        }
        else if(id == R.id.action_cancel){
            isCanceling = true;
            finish();
        }
        else if(id == R.id.action_next){
            moveNext();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem next = menu.findItem(R.id.action_next);
        int lastNoteIndex = DataManager.getInstance().getNotes().size()-1;
        next.setEnabled(selectedNotePosition < lastNoteIndex);
        return super.onPrepareOptionsMenu(menu);
    }

    private void moveNext() {
        saveNote();
        ++selectedNotePosition;
        selectedNoteInfo = DataManager.getInstance().getNotes().get(selectedNotePosition);
        saveOriginalNoteValues();
        displayNote(spinnerCourses,textNoteTitle,textNoteText);
        invalidateOptionsMenu();
    }

    private void sendEmail() {
        CourseInfo selectedCourse = (CourseInfo) spinnerCourses.getSelectedItem();
        String subject = textNoteTitle.getText().toString();
        String text = "Your note title is " + selectedCourse.getTitle() +
                "\n\n your note is  " +  textNoteText.getText().toString();
        Intent intentEmail = new Intent(Intent.ACTION_SEND);
        intentEmail.setType("message/rfc2822");
        intentEmail.putExtra(Intent.EXTRA_SUBJECT,subject);
        intentEmail.putExtra(Intent.EXTRA_TEXT,text);
        startActivity(intentEmail);
    }



}
