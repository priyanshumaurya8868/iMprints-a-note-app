package com.example.imprints.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "notes_table")
class Notes() :Serializable {
    @ColumnInfo(name = "title")
    var title : String? = null

    @ColumnInfo(name = "date")
    var date : String? = null

    @ColumnInfo(name = "sub_title")
    var sub_title : String? = null

    @ColumnInfo(name = "content")
    var content : String? = null

    @ColumnInfo(name = "colour")
    var colour : String? = null

    @ColumnInfo(name = "img_path")
    var img_path : String? = null

    @ColumnInfo(name = "web_link")
    var web_link : String? = null

    @PrimaryKey(autoGenerate = true)
    var id :Int =0

    override fun toString(): String {
        return "$title : $date"
    }
}