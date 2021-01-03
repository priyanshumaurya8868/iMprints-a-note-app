package com.example.imprints.dataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.imprints.dao.NotesDao
import com.example.imprints.entity.Notes

@Database( entities = arrayOf(Notes::class), version = 1,exportSchema = false)
abstract  class NotesDB : RoomDatabase(){
   abstract fun  NotesDao() : NotesDao


    companion object{
        @Volatile
     private var notesDB : NotesDB? = null
    @Synchronized
      fun getDataBase(context: Context): NotesDB{
        if(notesDB == null){
            notesDB = Room.databaseBuilder(context,NotesDB::class.java,"notes_dataBase").build()
        }
        return notesDB!!
    }
    }

}