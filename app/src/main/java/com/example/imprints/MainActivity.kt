package com.example.imprints

import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.discard_changes.*
import kotlinx.android.synthetic.main.leave_dialog.*
class MainActivity : AppCompatActivity() {
 private  val HOME_FRAGMENT_TAG = "home_fragment_key"
   private lateinit var fragment : HomeFragment
   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("Omega ranger", " Basefragment.isCrtNoteActive = ${BaseFragment.isCreateNotesFragmentActive} | backstackCount = ${supportFragmentManager.backStackEntryCount}")
        setContentView(R.layout.activity_main)
            if (savedInstanceState != null) {
                Log.d("Omega ranger", " (At MainActivity) using retainedTnstance of HomeFragment " )
                fragment = supportFragmentManager.getFragment(savedInstanceState, HOME_FRAGMENT_TAG) as HomeFragment
                justReplaceFragment(fragment,false,HOME_FRAGMENT_TAG)

            } else {
                Log.d("Omega ranger", " (At MainActivity) using newTnstance of HomeFragment ")
                fragment = HomeFragment.newInstance()
                replaceFragment(fragment, true, HOME_FRAGMENT_TAG)
            }

  }
    private fun replaceFragment(fragment: Fragment, isTransition :Boolean, tag :String){
        val fragmentTransition = this.supportFragmentManager.beginTransaction()
        if(isTransition){
            fragmentTransition.setCustomAnimations(android.R.anim.slide_out_right,android.R.anim.slide_in_left)
        }
        fragmentTransition.replace(R.id.frame_layout,fragment,tag).addToBackStack(fragment.javaClass.simpleName).commit()
    }

    fun justReplaceFragment(fragment: Fragment,isTransition :Boolean,tag :String){
        val fragmentTransition = supportFragmentManager.beginTransaction()
        if(isTransition){
            fragmentTransition.setCustomAnimations(android.R.anim.slide_out_right,android.R.anim.slide_in_left)
        }
        fragmentTransition.replace(R.id.frame_layout,fragment,tag).commit()
    }
 private var doubleBackPressed = false
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        Log.d("Omega ranger", "backStackEntry count : $count (before)")
        if (count == 0 || count== 1) {
            Log.d("Omega ranger", "onBackPressed \"if block\"" )

            if(doubleBackPressed )
            {      supportFragmentManager.popBackStack() //this extra statement because we hv nothing in our main activity (i.e blankActivity),
                // which gonna show u a black screen(or blackActivity) in single tap
                super.onBackPressed()
            }
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()
            this.doubleBackPressed = true
            Handler().postDelayed(Runnable { this.doubleBackPressed = false },2000)//I Think this handler helps to reset the variable after 2 second.
        } else {
            Log.d("Omega ranger", "onBackPressed -> popBackStack()->\"else block\"")
        val frag = supportFragmentManager.findFragmentByTag(BaseFragment.CREATE_NOTES_FRAGMENT_TAG)
        if(CreateNotesFragment.updateMode){
            val builder = frag?.requireContext()?.let { AlertDialog.Builder(it) }
            builder?.setCancelable(true)
            builder?.setView(R.layout.discard_changes)
            val alertDialog = builder?.create()
         alertDialog?.window?.let{ alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))}
            alertDialog?.show()

            alertDialog?.cancel_discard_changes_response_dialog?.setOnClickListener {
                alertDialog.dismiss()
            }

            alertDialog?.discard_discard_changes_note_response_dialog?.setOnClickListener {
                alertDialog.dismiss()
                (frag as CreateNotesFragment).discardUpdate()
                supportFragmentManager.popBackStack()
                Toast.makeText(frag.context, "Changes discarded...!",Toast.LENGTH_SHORT).show()
            }
            alertDialog?.save_discard_changes_response_dialog?.setOnClickListener {
                (frag as CreateNotesFragment).saveNote()
                alertDialog.dismiss()
                supportFragmentManager.popBackStack()
            }
        }
            else{
            val builder = frag?.requireContext()?.let { AlertDialog.Builder(it) }
            builder?.setCancelable(true)
            builder?.setView(R.layout.leave_dialog)
            val alertDialog = builder?.create()
            alertDialog?.window?.let{ alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))}
            alertDialog?.show()

            alertDialog?.cancel_discard_response_dialog?.setOnClickListener {
                alertDialog.dismiss()
            }
            alertDialog?.sure_discard_note_response_dialog?.setOnClickListener {
                alertDialog.dismiss()
                supportFragmentManager.popBackStack()
            }
        }
        Log.d("Omega ranger", "backStackEntry count : $count (after)")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        supportFragmentManager.putFragment(outState,HOME_FRAGMENT_TAG,fragment)
        super.onSaveInstanceState(outState )
    }
}