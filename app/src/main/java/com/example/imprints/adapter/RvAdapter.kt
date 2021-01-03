package com.example.imprints.adapter

import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.imprints.R
import com.example.imprints.entity.Notes
import com.makeramen.roundedimageview.RoundedImageView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_rv.view.*


class RvAdapter() : RecyclerView.Adapter<RvAdapter.RvViewHolder>() {
  private val allNotes = ArrayList<Notes>()
    private  val defaultColor = "#1F2022"
    private var updateListener : NotesOnClickInterface? = null

    inner class RvViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview){
        val title: TextView = itemview.rvTittleName
        val subTitle: TextView = itemview.rvSubtitle
        val dateTime :TextView= itemview.rvDateTime
        val content: TextView = itemview.rvContent
        val cardView: CardView =itemView.cardView_leave
        val displayImg : RoundedImageView = itemview.rv_preview_img
        val container : LinearLayout = itemview.pre_view_main_lay
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvViewHolder {
        val view  = LayoutInflater.from(parent.context).inflate(R.layout.item_rv, parent, false)
        return RvViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: RvViewHolder, position: Int) {
        holder.title.text = allNotes[position].title
        holder.subTitle.text = allNotes[position].sub_title
        holder.content.text = allNotes[position].content
        holder.dateTime.text = allNotes[position].date

        if(allNotes[position].sub_title.isNullOrEmpty()){
            holder.subTitle.visibility = View.GONE
        }else holder.subTitle.visibility = View.VISIBLE

        if(allNotes[position].content.isNullOrEmpty()){
            holder.content.visibility = View.GONE
        }else holder.content.visibility = View.VISIBLE


        if(!allNotes[position].colour.isNullOrEmpty()){
            holder.cardView.background.setTint(Color.parseColor(allNotes[position].colour))         //mv_cardView.getBackground().setTint(Color.BLUE)
            Log.d("omega ranger", " (At rvAdapter) customized cardBackground colour => ${allNotes[position].colour}")
        }
        else   holder.cardView.background.setTint(Color.parseColor(defaultColor))

        //handling WHITE cardBackground
        if(allNotes[position].colour.equals("#FFFFFFFF")) {
            holder.content.setTextColor(Color.parseColor("#8E8E8E"))//grey
            holder.title.setTextColor(Color.parseColor("#FF000000"))
            holder.subTitle.setTextColor(Color.parseColor("#FF000000"))
            holder.dateTime.setTextColor(Color.parseColor("#FF000000"))
        }
        else{
            holder.content.setTextColor(Color.parseColor("#FFFFFFFF"))
            holder.title.setTextColor(Color.parseColor("#FFFFFFFF"))
            holder.subTitle.setTextColor(Color.parseColor("#FFFFFFFF"))
            holder.dateTime.setTextColor(Color.parseColor("#FFFFFFFF"))
        }

        if(allNotes[position].sub_title.isNullOrEmpty()) {
            holder.subTitle.visibility=View.GONE
        }else {
            holder.subTitle.visibility=View.VISIBLE
        }
        if(allNotes[position].img_path.isNullOrEmpty()) {
            Log.d("Omega Ranger ", "(At rvAdapter) img uri.toString is null....!")
            holder.displayImg.visibility=View.GONE
        }
        else{ Log.d("Omega Ranger ", "(At rvAdapter) img path is NotNull....! -> allNotes[position].img_path = ${allNotes[position].img_path}")
// holder.displayImg.setImageBitmap(BitmapFactory.decodeFile(allNotes[position].img_path))
            Picasso.get().load(Uri.parse(allNotes[position].img_path)).into(holder.displayImg)
            holder.displayImg.visibility=View.VISIBLE
            Log.d("Omega Ranger ", "is previewImage visible ${holder.displayImg.isVisible}")
        }
        holder.container.setOnClickListener {
            updateListener!!.notesOnClicked(allNotes[position])
        }
        holder.content.setOnClickListener {
            updateListener!!.notesOnClicked(allNotes[position])
        }
    }

    override fun getItemCount(): Int {
        return allNotes.size
    }

    fun setUpdateNoteListner(listner : NotesOnClickInterface){
        updateListener = listner
    }

    fun updateList(newList: List<Notes>){
        allNotes.clear()
        allNotes.addAll(newList)
        notifyDataSetChanged()
    }

    interface NotesOnClickInterface {
        fun notesOnClicked(notes : Notes)
    }
}
