package com.example.nipunu.notekeeper;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

public class NoteActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    public static final String NOTE_POSITION = "com.example.nipunu.notekeeper.NOTE_POSITION";
    public static final String ORIGINAL_NOTE_COURSE_ID = "com.example.nipunu.notekeeper.ORIGINAL_NOTE_COURSE_ID";
    public static final String ORIGINAL_NOTE_TITLE = "com.example.nipunu.notekeeper.ORIGINAL_NOTE_TITLE";
    public static final String ORIGINAL_NOTE_TEXT = "com.example.nipunu.notekeeper.ORIGINAL_NOTE_TEXT";
    public static final int POSITION_NOT_SET = -1;

    private NoteInfo noteInfo;
    private boolean isNewNote;
    private Spinner spinnerCourses;
    private EditText textNoteTitle;
    private EditText textNoteText;
    private int notePosition;
    private boolean isCancelling;
    private String originalNoteCourseId;
    private String originalNoteTitle;
    private String originalNoteText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        spinnerCourses = (Spinner) findViewById(R.id.spinner_courses);
        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        ArrayAdapter<CourseInfo> adapterCourses =
                new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,courses);
        adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourses.setAdapter(adapterCourses);

        readDisplayStateValues();
        if(savedInstanceState == null) {
            saveOriginalNoteValues();
        }
        else{
            restoreOriginalNoteValues(savedInstanceState);
        }

        textNoteTitle = (EditText)findViewById(R.id.text_note_title);
        textNoteText = (EditText)findViewById(R.id.text_note_text);

        if(!isNewNote) {
            displayNote(spinnerCourses, textNoteTitle, textNoteText);
        }

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        Log.d(TAG,"onCreate");
    }

    private void restoreOriginalNoteValues(Bundle savedInstanceState) {
        originalNoteCourseId = savedInstanceState.getString(ORIGINAL_NOTE_COURSE_ID);
        originalNoteTitle = savedInstanceState.getString(ORIGINAL_NOTE_TITLE);
        originalNoteText = savedInstanceState.getString(ORIGINAL_NOTE_TEXT);
    }

    private void saveOriginalNoteValues() {
            if(isNewNote){
                return;
            }
            else{
                originalNoteCourseId = noteInfo.getCourse().getCourseId();
                originalNoteTitle = noteInfo.getTitle();
                originalNoteText = noteInfo.getText();
            }
    }

    private void displayNote(Spinner spinnerCourses, EditText textNoteTitle, EditText textNoteText) {
        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        int courseIndex  = courses.indexOf(noteInfo.getCourse());
        spinnerCourses.setSelection(courseIndex);
        textNoteTitle.setText(noteInfo.getTitle());
        textNoteText.setText(noteInfo.getText());
    }

    private void readDisplayStateValues() {
        Intent intent = getIntent();
        notePosition = intent.getIntExtra(NOTE_POSITION, POSITION_NOT_SET);
        isNewNote = notePosition == POSITION_NOT_SET;
        if(isNewNote){
            createNewNote();
        }
        Log.i(TAG,"note position is " + notePosition);
        noteInfo = DataManager.getInstance().getNotes().get(notePosition);
    }

    private void createNewNote() {
            DataManager dm = DataManager.getInstance();
        notePosition = dm.createNewNote();
//        noteInfo = dm.getNotes().get(notePosition);
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
        outState.putString(ORIGINAL_NOTE_COURSE_ID,originalNoteCourseId);
        outState.putString(ORIGINAL_NOTE_TITLE,originalNoteTitle);
        outState.putString(ORIGINAL_NOTE_TEXT,originalNoteText);
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
            isCancelling = true;
            finish();
        }
        else if(id == R.id.action_next){
            moveNext();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.action_next);
        int lastNoteIndex = DataManager.getInstance().getNotes().size()-1;
        menuItem.setEnabled(notePosition < lastNoteIndex);
        return super.onPrepareOptionsMenu(menu);

    }

    private void moveNext() {
        saveNote();
        ++notePosition;
        noteInfo = DataManager.getInstance().getNotes().get(notePosition);
        saveOriginalNoteValues();
        displayNote(spinnerCourses,textNoteTitle,textNoteText);
        invalidateOptionsMenu();
    }

    private void sendEmail() {
        CourseInfo course = (CourseInfo) spinnerCourses.getSelectedItem();
        String subject = textNoteTitle.getText().toString();
        String text = "I have learnt a lot from " + course.getTitle() + " on " + textNoteText.getText().toString();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc2822");
        intent.putExtra(intent.EXTRA_SUBJECT,subject);
        intent.putExtra(intent.EXTRA_TEXT,text);
        startActivity(intent);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isCancelling){
            Log.i(TAG,"Canceling note at position" + notePosition);
            if(isNewNote) {
                DataManager.getInstance().removeNote(notePosition);
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
        noteInfo.setCourse(course);
        noteInfo.setTitle(originalNoteTitle);
        noteInfo.setText(originalNoteText);
    }

    private void saveNote() {
        noteInfo.setCourse((CourseInfo)spinnerCourses.getSelectedItem());
        noteInfo.setTitle(textNoteTitle.getText().toString());
        noteInfo.setText(textNoteText.getText().toString());
    }
}
