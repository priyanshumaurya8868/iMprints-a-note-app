package com.example.imprints

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.MediaStore
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.imprints.entity.Notes
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.buttom_sheet.*
import kotlinx.android.synthetic.main.delete_note_dialog.*
import kotlinx.android.synthetic.main.fragment_create_notes.*
import kotlinx.android.synthetic.main.web_link_dialog.*
import java.text.SimpleDateFormat
import java.util.*


class CreateNotesFragment : BaseFragment() {

    private lateinit var bottomSheet: BottomSheetDialog
    private  var selectedColor = "#1F2022"
    private var selectedUri: Uri? = null
    private  var noteToBeUpdate : Notes? = null

    private var insertedUrlLinkString: String? = null
    private val REQUEST_CODE_PERMISSION = 1
    private val REQUEST_CODE_SELECT_IMAGE = 2

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bottomSheet = BottomSheetDialog(requireContext())
        bottomSheet.setContentView(R.layout.buttom_sheet)
        bottomSheet.setCancelable(true)
        if (bottomSheet.window != null) {
            bottomSheet.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        arguments?.let {
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
     isCreateNotesFragmentActive = 1
        retainInstance = true


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_notes, container, false)
    }


    companion object {

        var updateMode = false
        @JvmStatic
        fun newInstance(note: Notes? = null, isOnUpdateMode: Boolean? = false) =
                CreateNotesFragment().apply {
                    this.noteToBeUpdate = note
                        if (isOnUpdateMode != null) {
                            updateMode = isOnUpdateMode
                        }
                    arguments = Bundle().apply {
                    }
                }

    }

    private fun getDateTime(): String {
//    val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
        val sdf = SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault())
        return sdf.format(Date().time)
    }

    fun discardUpdate(){
        noteToBeUpdate?.let {viewModel.insertNote(it)}
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(updateMode){
            retrievedData()
        } else
        date_h.setText(getDateTime())
        img_back.setOnClickListener {
            requireActivity().onBackPressed()
        }
        img_done.setOnClickListener {
            saveNote()
        }
        img_mis.setOnClickListener {

            bottomSheet.show()
            //initialize variable
            bottomSheet.ic_addImage.setOnClickListener {
                if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(requireActivity(),
                            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE_PERMISSION)
                } else
                    selectImg()
                bottomSheet.dismiss()
            }
            bottomSheet.ic_addWebLink.setOnClickListener {

           val builder = AlertDialog.Builder(requireContext())
                builder.setView(R.layout.web_link_dialog)
                builder.setCancelable(false)
                val alertDialog = builder.create()
                alertDialog.show()

                //for hiding white edge corners
               if (alertDialog.window != null){
             alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0)) }

                alertDialog.url_editText_dialog.requestFocus()
                if(!input_url.text.isNullOrEmpty()){
                   alertDialog.url_editText_dialog.setText(insertedUrlLinkString)
                }
                alertDialog.add_inputLink_response_dialog.setOnClickListener {
                    if (alertDialog.url_editText_dialog.text.isNullOrEmpty()) {
                        Toast.makeText(requireContext(), "Enter URL...!", Toast.LENGTH_SHORT).show()
                    } else if (!Patterns.WEB_URL.matcher(alertDialog.url_editText_dialog.text.toString()).matches()) {
                        Toast.makeText(requireContext(), "Enter Valid URL...!", Toast.LENGTH_SHORT).show()
                        alertDialog.url_editText_dialog.setTextColor(Color.parseColor("#D10F0F"))
                    } else {
                        insertedUrlLinkString = alertDialog.url_editText_dialog.text.toString()
                        input_url.text = insertedUrlLinkString
                        layout3.visibility = View.VISIBLE
                        alertDialog.dismiss()
                    }
                }
                alertDialog.cancel_inputLink_response_dialog.setOnClickListener {
                    alertDialog.dismiss()
                }
           bottomSheet.dismiss()
            }

            selectedColor = getSelectedColor()
          }
        inserted_img.setOnClickListener {
            del_inserted_img.visibility = View.VISIBLE
            object:CountDownTimer(2000,1000){

                override fun onTick(millisUntilFinished: Long) { }

                override fun onFinish() { del_inserted_img?.visibility = View.GONE}
            }.start()
        }
        del_inserted_img?.setOnClickListener {
            inserted_img.setImageURI(null)
            inserted_img.visibility = View.GONE
        }

        del_input_url?.setOnClickListener {
            input_url.setText(null)
            layout3.visibility=View.GONE
        }

        bottomSheet.bottomSheetlinearLayout4_delete.setOnClickListener {
            bottomSheet.dismiss()
            val del_builder = AlertDialog.Builder(requireContext())
            del_builder.setView(R.layout.delete_note_dialog)
            del_builder.setCancelable(true)
            val del_alert = del_builder.create()
            del_alert.window?.let{ del_alert.window!!.setBackgroundDrawable(ColorDrawable(0))}
            del_alert.show()

            del_alert.delete_note_response_dialog.setOnClickListener {
                viewModel.deleteNote(noteToBeUpdate!!)
                eraseUp()
                del_alert.dismiss()
            }
            del_alert.cancel_delete_note_response_dialog.setOnClickListener {
                del_alert.dismiss()

            }

        }
        if(updateMode) {
            bottomSheet.bottomSheetlinearLayout4_delete.visibility = View.VISIBLE
        } else bottomSheet.bottomSheetlinearLayout4_delete.visibility = View.GONE

       }

    private fun retrievedData(){
 //title
        if (!noteToBeUpdate?.title.isNullOrEmpty())
            title_h.setText(noteToBeUpdate?.title)

//date
        date_h.setText(noteToBeUpdate?.date)

//subtitle
        if (!noteToBeUpdate?.sub_title.isNullOrEmpty())
            subTitle_h.setText(noteToBeUpdate?.sub_title)
//color
        if (!noteToBeUpdate?.colour.isNullOrEmpty()){
            selectedColor = noteToBeUpdate?.colour.toString()
            colourView_crt.setBackgroundColor(Color.parseColor(selectedColor))

            when(selectedColor){
                "#1C1F2C" ->{
                    bottomSheet.colorTick1.alpha = 1f
                }
                "#FFFFFFFF"->{
                   bottomSheet.colorTick2.alpha = 1f
                }
                "#00ACC1" -> {
                    bottomSheet.colorTick3.alpha=1f
                }
                "#ffff4444"->{
                    bottomSheet.colorTick4.alpha = 1f
                }
                "#FF580A"->{
                    bottomSheet.colorTick5.alpha = 1f
                }
                "#E91E63"->{
                    bottomSheet.colorTick6.alpha =1f
                }
                "#D10F0F"->{
                   bottomSheet.colorTick7.alpha=1f
                }
                "#FFB402"->{
                    bottomSheet.colorTick8.alpha=1f
                }
                "#04C10C"->{
                    bottomSheet.colorTick9.alpha=1f
                }
                "#00895E"->{
                    bottomSheet.colorTick10.alpha=1f
                }
                "#9C27B0"->{
                    bottomSheet.colorTick11.alpha = 1f
                }
                "#FF6200EE"->{
                    bottomSheet.colorTick12.alpha = 1f
                }
                "#303F9F"->{
                    bottomSheet.colorTick13.alpha =1f
                }
                "#0D25BF"->{
                    bottomSheet.colorTick14.alpha = 1f
                }
            }

        }
// mange color tic left




        if (!noteToBeUpdate?.content.isNullOrEmpty())
            content_h.setText(noteToBeUpdate?.content)
//img_uri
        if (!noteToBeUpdate?.img_path.isNullOrEmpty()){
            selectedUri = Uri.parse(noteToBeUpdate?.img_path)
            inserted_img.setImageURI(selectedUri)
            inserted_img.visibility = View.VISIBLE
        } else inserted_img.visibility = View.GONE
//link
        if(!noteToBeUpdate?.web_link.isNullOrEmpty()){
 insertedUrlLinkString =  noteToBeUpdate?.web_link
            layout3.visibility = View.VISIBLE
            input_url.setText(insertedUrlLinkString)
        }else layout3.visibility = View.GONE

    }


    private fun selectImg() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE)
        }
    }

    //  handling RequestPermissionsResult
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION && grantResults.isNotEmpty()) {
            selectImg()
        } else
            Toast.makeText(requireContext(), "Permission Denied...!", Toast.LENGTH_SHORT).show()
    }

    //handling image setting
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_IMAGE) {
            if (data != null) selectedUri = data.data
            if (selectedUri != null) {
                inserted_img.setImageURI(null)
                inserted_img.setImageURI(selectedUri)
                inserted_img.visibility = View.VISIBLE

            }
        }
    }



    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun saveNote() {
//fetching data....:)
        val note = Notes()
        note.title = title_h.text.toString()
        note.date = date_h.text.toString()
        note.sub_title = subTitle_h.text.toString()
        note.content = content_h.text.toString()
      //link
        if (layout3.isVisible)
            note.web_link = insertedUrlLinkString
        //img
        if(inserted_img.isVisible)
            note.img_path = selectedUri.toString()
        //color
        note.colour = selectedColor

       if(updateMode){
           note.id = noteToBeUpdate?.id!!
       }
            if (!(note.title.isNullOrEmpty())) {
                viewModel.insertNote(note)
                eraseUp()
                Toast.makeText(context, "Note saved..!", Toast.LENGTH_SHORT).show()
            } else
                Toast.makeText(this.context, "Tittle is required", Toast.LENGTH_SHORT).show()

        updateMode = false

    }

    override fun onDestroy() {
        super.onDestroy()
        noteToBeUpdate = null
        updateMode = false
        isCreateNotesFragmentActive = 0
    }

    private fun eraseUp() {
        title_h.setText(null)
        date_h.setText(getDateTime())
        subTitle_h.setText(null)
        content_h.setText(null)

        layout3.visibility = View.GONE
        input_url.setText(null)

        inserted_img.visibility = View.GONE
        inserted_img.setImageURI(null)

    }

    @JvmName("getSelectedColor1")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun getSelectedColor(): String {

        bottomSheet.bs_farme_lay_1.setOnClickListener {
            bottomSheet.colorTick1.alpha = 1f
            bottomSheet.colorTick2.alpha = 0f
            bottomSheet.colorTick3.alpha = 0f
            bottomSheet.colorTick4.alpha = 0f
            bottomSheet.colorTick5.alpha = 0f
            bottomSheet.colorTick6.alpha = 0f
            bottomSheet.colorTick7.alpha = 0f
            bottomSheet.colorTick8.alpha = 0f
            bottomSheet.colorTick9.alpha = 0f
            bottomSheet.colorTick10.alpha = 0f
            bottomSheet.colorTick11.alpha = 0f
            bottomSheet.colorTick12.alpha = 0f
            bottomSheet.colorTick13.alpha = 0f
            bottomSheet.colorTick14.alpha = 0f
            val color = "#1C1F2C"
            selectedColor = color
            colourView_crt.setBackgroundColor(Color.parseColor(color))
        }
        bottomSheet.bs_farme_lay_2.setOnClickListener {
            bottomSheet.colorTick1.alpha = 0f
            bottomSheet.colorTick2.alpha = 1f
            bottomSheet.colorTick3.alpha = 0f
            bottomSheet.colorTick4.alpha = 0f
            bottomSheet.colorTick5.alpha = 0f
            bottomSheet.colorTick6.alpha = 0f
            bottomSheet.colorTick7.alpha = 0f
            bottomSheet.colorTick8.alpha = 0f
            bottomSheet.colorTick9.alpha = 0f
            bottomSheet.colorTick10.alpha = 0f
            bottomSheet.colorTick11.alpha = 0f
            bottomSheet.colorTick12.alpha = 0f
            bottomSheet.colorTick13.alpha = 0f
            bottomSheet.colorTick14.alpha = 0f

            val color = "#FFFFFFFF"
                colourView_crt.setBackgroundColor(Color.parseColor(color))

            selectedColor = color
        }
        bottomSheet.bs_farme_lay_3.setOnClickListener {
            bottomSheet.colorTick1.alpha = 0f
            bottomSheet.colorTick2.alpha = 0f
            bottomSheet.colorTick3.alpha = 1f
            bottomSheet.colorTick4.alpha = 0f
            bottomSheet.colorTick5.alpha = 0f
            bottomSheet.colorTick6.alpha = 0f
            bottomSheet.colorTick7.alpha = 0f
            bottomSheet.colorTick8.alpha = 0f
            bottomSheet.colorTick9.alpha = 0f
            bottomSheet.colorTick10.alpha = 0f
            bottomSheet.colorTick11.alpha = 0f
            bottomSheet.colorTick12.alpha = 0f
            bottomSheet.colorTick13.alpha = 0f
            bottomSheet.colorTick14.alpha = 0f
            val color = "#00ACC1"
            selectedColor = color

                colourView_crt.setBackgroundColor(Color.parseColor(color))

        }
        bottomSheet.bs_farme_lay_4.setOnClickListener {
            bottomSheet.colorTick1.alpha = 0f
            bottomSheet.colorTick2.alpha = 0f
            bottomSheet.colorTick3.alpha = 0f
            bottomSheet.colorTick4.alpha = 1f
            bottomSheet.colorTick5.alpha = 0f
            bottomSheet.colorTick6.alpha = 0f
            bottomSheet.colorTick7.alpha = 0f
            bottomSheet.colorTick8.alpha = 0f
            bottomSheet.colorTick9.alpha = 0f
            bottomSheet.colorTick10.alpha = 0f
            bottomSheet.colorTick11.alpha = 0f
            bottomSheet.colorTick12.alpha = 0f
            bottomSheet.colorTick13.alpha = 0f
            bottomSheet.colorTick14.alpha = 0f
            val color = "#ffff4444"
            selectedColor = color

                colourView_crt.setBackgroundColor(Color.parseColor(color))

        }
        bottomSheet.bs_farme_lay_5.setOnClickListener {
            bottomSheet.colorTick1.alpha = 0f
            bottomSheet.colorTick2.alpha = 0f
            bottomSheet.colorTick3.alpha = 0f
            bottomSheet.colorTick4.alpha = 0f
            bottomSheet.colorTick5.alpha = 1f
            bottomSheet.colorTick6.alpha = 0f
            bottomSheet.colorTick7.alpha = 0f
            bottomSheet.colorTick8.alpha = 0f
            bottomSheet.colorTick9.alpha = 0f
            bottomSheet.colorTick10.alpha = 0f
            bottomSheet.colorTick11.alpha = 0f
            bottomSheet.colorTick12.alpha = 0f
            bottomSheet.colorTick13.alpha = 0f
            bottomSheet.colorTick14.alpha = 0f
            val color = "#FF580A"
            selectedColor = color
            colourView_crt.setBackgroundColor(Color.parseColor(color))
        }
        bottomSheet.bs_farme_lay_6.setOnClickListener {
            bottomSheet.colorTick1.alpha = 0f
            bottomSheet.colorTick2.alpha = 0f
            bottomSheet.colorTick3.alpha = 0f
            bottomSheet.colorTick4.alpha = 0f
            bottomSheet.colorTick5.alpha = 0f
            bottomSheet.colorTick6.alpha = 1f
            bottomSheet.colorTick7.alpha = 0f
            bottomSheet.colorTick8.alpha = 0f
            bottomSheet.colorTick9.alpha = 0f
            bottomSheet.colorTick10.alpha = 0f
            bottomSheet.colorTick11.alpha = 0f
            bottomSheet.colorTick12.alpha = 0f
            bottomSheet.colorTick13.alpha = 0f
            bottomSheet.colorTick14.alpha = 0f
            val color = "#E91E63"
            selectedColor = color
            colourView_crt.setBackgroundColor(Color.parseColor(color))

        }
        bottomSheet.bs_farme_lay_7.setOnClickListener {
            bottomSheet.colorTick1.alpha = 0f
            bottomSheet.colorTick2.alpha = 0f
            bottomSheet.colorTick3.alpha = 0f
            bottomSheet.colorTick4.alpha = 0f
            bottomSheet.colorTick5.alpha = 0f
            bottomSheet.colorTick6.alpha = 0f
            bottomSheet.colorTick7.alpha = 1f
            bottomSheet.colorTick8.alpha = 0f
            bottomSheet.colorTick9.alpha = 0f
            bottomSheet.colorTick10.alpha = 0f
            bottomSheet.colorTick11.alpha = 0f
            bottomSheet.colorTick12.alpha = 0f
            bottomSheet.colorTick13.alpha = 0f
            bottomSheet.colorTick14.alpha = 0f
            val color = "#D10F0F"
            selectedColor = color
            colourView_crt.setBackgroundColor(Color.parseColor(color))
        }
        bottomSheet.bs_farme_lay_8.setOnClickListener {
            bottomSheet.colorTick1.alpha = 0f
            bottomSheet.colorTick2.alpha = 0f
            bottomSheet.colorTick3.alpha = 0f
            bottomSheet.colorTick4.alpha = 0f
            bottomSheet.colorTick5.alpha = 0f
            bottomSheet.colorTick6.alpha = 0f
            bottomSheet.colorTick7.alpha = 0f
            bottomSheet.colorTick8.alpha = 1f
            bottomSheet.colorTick9.alpha = 0f
            bottomSheet.colorTick10.alpha = 0f
            bottomSheet.colorTick11.alpha = 0f
            bottomSheet.colorTick12.alpha = 0f
            bottomSheet.colorTick13.alpha = 0f
            bottomSheet.colorTick14.alpha = 0f
            val color = "#FFB402"
            selectedColor = color
            colourView_crt.setBackgroundColor(Color.parseColor(color))
        }
        bottomSheet.bs_farme_lay_9.setOnClickListener {
            bottomSheet.colorTick1.alpha = 0f
            bottomSheet.colorTick2.alpha = 0f
            bottomSheet.colorTick3.alpha = 0f
            bottomSheet.colorTick4.alpha = 0f
            bottomSheet.colorTick5.alpha = 0f
            bottomSheet.colorTick6.alpha = 0f
            bottomSheet.colorTick7.alpha = 0f
            bottomSheet.colorTick8.alpha = 0f
            bottomSheet.colorTick9.alpha = 1f
            bottomSheet.colorTick10.alpha = 0f
            bottomSheet.colorTick11.alpha = 0f
            bottomSheet.colorTick12.alpha = 0f
            bottomSheet.colorTick13.alpha = 0f
            bottomSheet.colorTick14.alpha = 0f
            val color = "#04C10C"
            selectedColor = color
            colourView_crt.setBackgroundColor(Color.parseColor(color))
        }
        bottomSheet.bs_farme_lay_10.setOnClickListener {
            bottomSheet.colorTick1.alpha = 0f
            bottomSheet.colorTick2.alpha = 0f
            bottomSheet.colorTick3.alpha = 0f
            bottomSheet.colorTick4.alpha = 0f
            bottomSheet.colorTick5.alpha = 0f
            bottomSheet.colorTick6.alpha = 0f
            bottomSheet.colorTick7.alpha = 0f
            bottomSheet.colorTick8.alpha = 0f
            bottomSheet.colorTick9.alpha = 0f
            bottomSheet.colorTick10.alpha = 1f
            bottomSheet.colorTick11.alpha = 0f
            bottomSheet.colorTick12.alpha = 0f
            bottomSheet.colorTick13.alpha = 0f
            bottomSheet.colorTick14.alpha = 0f
            val color = "#00895E"
            selectedColor = color
            colourView_crt.setBackgroundColor(Color.parseColor(color))
        }
        bottomSheet.bs_farme_lay_11.setOnClickListener {
            bottomSheet.colorTick1.alpha = 0f
            bottomSheet.colorTick2.alpha = 0f
            bottomSheet.colorTick3.alpha = 0f
            bottomSheet.colorTick4.alpha = 0f
            bottomSheet.colorTick5.alpha = 0f
            bottomSheet.colorTick6.alpha = 0f
            bottomSheet.colorTick7.alpha = 0f
            bottomSheet.colorTick8.alpha = 0f
            bottomSheet.colorTick9.alpha = 0f
            bottomSheet.colorTick10.alpha = 0f
            bottomSheet.colorTick11.alpha = 1f
            bottomSheet.colorTick12.alpha = 0f
            bottomSheet.colorTick13.alpha = 0f
            bottomSheet.colorTick14.alpha = 0f
            val color = "#9C27B0"
            selectedColor = color
            colourView_crt.setBackgroundColor(Color.parseColor(color))
        }
        bottomSheet.bs_farme_lay_12.setOnClickListener {
            bottomSheet.colorTick1.alpha = 0f
            bottomSheet.colorTick2.alpha = 0f
            bottomSheet.colorTick3.alpha = 0f
            bottomSheet.colorTick4.alpha = 0f
            bottomSheet.colorTick5.alpha = 0f
            bottomSheet.colorTick6.alpha = 0f
            bottomSheet.colorTick7.alpha = 0f
            bottomSheet.colorTick8.alpha = 0f
            bottomSheet.colorTick9.alpha = 0f
            bottomSheet.colorTick10.alpha = 0f
            bottomSheet.colorTick11.alpha = 0f
            bottomSheet.colorTick12.alpha = 1f
            bottomSheet.colorTick13.alpha = 0f
            bottomSheet.colorTick14.alpha = 0f
            val color = "#FF6200EE"
            selectedColor = color
            colourView_crt.setBackgroundColor(Color.parseColor(color))
        }
        bottomSheet.bs_farme_lay_13.setOnClickListener {
            bottomSheet.colorTick1.alpha = 0f
            bottomSheet.colorTick2.alpha = 0f
            bottomSheet.colorTick3.alpha = 0f
            bottomSheet.colorTick4.alpha = 0f
            bottomSheet.colorTick5.alpha = 0f
            bottomSheet.colorTick6.alpha = 0f
            bottomSheet.colorTick7.alpha = 0f
            bottomSheet.colorTick8.alpha = 0f
            bottomSheet.colorTick9.alpha = 0f
            bottomSheet.colorTick10.alpha = 0f
            bottomSheet.colorTick11.alpha = 0f
            bottomSheet.colorTick12.alpha = 0f
            bottomSheet.colorTick13.alpha = 1f
            bottomSheet.colorTick14.alpha = 0f
            val color = "#303F9F"
            selectedColor = color
            colourView_crt.setBackgroundColor(Color.parseColor(color))
        }
        bottomSheet.bs_farme_lay_14.setOnClickListener {
            bottomSheet.colorTick1.alpha = 0f
            bottomSheet.colorTick2.alpha = 0f
            bottomSheet.colorTick3.alpha = 0f
            bottomSheet.colorTick4.alpha = 0f
            bottomSheet.colorTick5.alpha = 0f
            bottomSheet.colorTick6.alpha = 0f
            bottomSheet.colorTick7.alpha = 0f
            bottomSheet.colorTick8.alpha = 0f
            bottomSheet.colorTick9.alpha = 0f
            bottomSheet.colorTick10.alpha = 0f
            bottomSheet.colorTick11.alpha = 0f
            bottomSheet.colorTick12.alpha = 0f
            bottomSheet.colorTick13.alpha = 0f
            bottomSheet.colorTick14.alpha = 1f
            val color = "#0D25BF"
            selectedColor = color
            colourView_crt.setBackgroundColor(Color.parseColor(color))
        }
        return selectedColor
    }

}



