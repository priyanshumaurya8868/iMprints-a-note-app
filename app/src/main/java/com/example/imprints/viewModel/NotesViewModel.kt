package com.example.imprints.viewModel
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.imprints.dataBase.NotesDB
import com.example.imprints.entity.Notes
import com.example.imprints.repository.NotesRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotesViewModel(application: Application) : AndroidViewModel(application){
     val notesRepo : NotesRepo
     val allNotes : LiveData<List<Notes>>

    init {

        val notesDao = NotesDB.getDataBase(application).NotesDao()
        notesRepo = NotesRepo(notesDao)
        allNotes = NotesRepo(notesDao).allNotes
        }
    fun deleteNote(note :Notes) = viewModelScope.launch(Dispatchers.IO){
        notesRepo.delete(note)
    }
    fun insertNote(note : Notes) = viewModelScope.launch(Dispatchers.IO){
        notesRepo.insert(note)
    }


}