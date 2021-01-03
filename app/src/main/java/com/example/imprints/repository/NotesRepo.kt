package com.example.imprints.repository

import androidx.lifecycle.LiveData
import com.example.imprints.dao.NotesDao
import com.example.imprints.entity.Notes

class NotesRepo(private val dao : NotesDao) {
   val  allNotes : LiveData<List<Notes>> = dao.geAllNotes()

    suspend fun insert(note : Notes){
        dao.insert(note)
    }
    suspend fun  delete(note: Notes){
        dao.delete(note)
    }

}