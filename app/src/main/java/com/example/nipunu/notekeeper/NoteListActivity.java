package com.example.nipunu.notekeeper;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class NoteListActivity extends AppCompatActivity {
    private NoteRecyclerAdapter noteRecyclerAdapter;

//    private ArrayAdapter<NoteInfo> adapterNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NoteListActivity.this,NoteActivity.class));
            }
        });

        initializeDisplayContent();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        adapterNotes.notifyDataSetChanged();
        noteRecyclerAdapter.notifyDataSetChanged();
    }

    private void initializeDisplayContent() {

//        final ListView listNotes = (ListView) findViewById(R.id.lits_notes);
//        List<NoteInfo> notes = DataManager.getInstance().getNotes();
//        adapterNotes = new ArrayAdapter<NoteInfo>(this,android.R.layout.simple_list_item_1,notes);
//        listNotes.setAdapter(adapterNotes);
//        listNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intentNoteActivity = new Intent(NoteListActivity.this,NoteActivity.class);
////                NoteInfo selectedNote = (NoteInfo) listNotes.getItemAtPosition(position);
////                intentNoteActivity.putExtra(NoteActivity.NOTE_INFO,selectedNote);
//                intentNoteActivity.putExtra(NoteActivity.NOTE_POSITION,position);
//                startActivity(intentNoteActivity);
//            }
//        });

        RecyclerView recyclerNotes = (RecyclerView) findViewById(R.id.list_notes);
        LinearLayoutManager notesLayoutManager = new LinearLayoutManager(this);
        recyclerNotes.setLayoutManager(notesLayoutManager);
        List<NoteInfo> notes = DataManager.getInstance().getNotes();
        noteRecyclerAdapter = new NoteRecyclerAdapter(this,notes);
        recyclerNotes.setAdapter(noteRecyclerAdapter);
    }

}
