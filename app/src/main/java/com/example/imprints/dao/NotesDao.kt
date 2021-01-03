package com.example.imprints.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.imprints.entity.Notes

@Dao
interface  NotesDao {

    @Query("SELECT * FROM notes_table ORDER BY id DESC")
 fun geAllNotes() : LiveData<List<Notes>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend  fun insert(note :Notes)
    @Delete
  suspend fun  delete(note : Notes)

}