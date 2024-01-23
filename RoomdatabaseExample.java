package com.example.myapplication.room;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.Update;

import android.os.Bundle;
import android.util.Log;

import com.example.myapplication.R;

import java.util.List;
/*
implementation "androidx.room:room-runtime:2.2.5"
annotationProcessor "androidx.room:room-compiler:2.2.5"
implementation "androidx.lifecycle:lifecycle-extensions:2.2.0"
annotationProcessor "androidx.lifecycle:lifecycle-compiler:2.2.0"
*/
public class RoomdatabaseExample extends AppCompatActivity {
    private static final String TAG = "NoteActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        new Thread(new Runnable() {
            @Override
            public void run() {
                //insert note
                Note newNote = new Note();
                newNote.setContent("content of note");

                //get all note
                NoteDatabase noteDatabase = Room.databaseBuilder(getApplicationContext(), NoteDatabase.class, "note_database").build();
                noteDatabase.noteDao().insert(newNote);
                List<Note> noteList = noteDatabase.noteDao().getAllNotes();
                for (int i = 0; i < noteList.size(); i++) {
                    Log.e(TAG, "onCreate: "+noteList.get(i).toString() );
                }
            }
        }).start();
    }

    @Entity(tableName = "notes")
    public class Note {
        @PrimaryKey(autoGenerate = true)
        private int id;

        private String content;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        @NonNull
        @Override
        public String toString() {
            return "Note{" +
                    "id=" + id +
                    ", content='" + content + '\'' +
                    '}';
        }
    }

    @Dao
    public interface NoteDao {
        @Insert
        void insert(Note note);

        @Update
        void update(Note note);

        @Delete
        void delete(Note note);

        @Query("DELETE FROM notes")
        void deleteAllNotes();

        @Query("SELECT * FROM notes")
        List<Note> getAllNotes();

        @Query("SELECT * FROM notes WHERE id = :noteId")
        Note getNoteById(int noteId);
    }
    @Database(entities = {Note.class}, version = 1)
    public abstract class NoteDatabase extends RoomDatabase {
        public abstract NoteDao noteDao();
    }

}
