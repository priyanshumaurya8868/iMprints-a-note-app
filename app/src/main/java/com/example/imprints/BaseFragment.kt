package com.example.imprints

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.imprints.viewModel.NotesViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

open class BaseFragment : Fragment(),CoroutineScope {

    private lateinit var job : Job
     lateinit var viewModel : NotesViewModel
    override val coroutineContext: CoroutineContext
        get() = job+Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
        viewModel = ViewModelProvider(requireActivity(),
            ViewModelProvider.AndroidViewModelFactory(this.requireActivity().application)).get(
            NotesViewModel::class.java)

    }
 companion object{
     val CREATE_NOTES_FRAGMENT_TAG = "create_notes_fragment_key"
     private  val HOME_FRAGMENT_TAGz = "home_fragment_key"
     var  isCreateNotesFragmentActive : Int = 0
 }
    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    fun replaceFragment(fragment: Fragment,isTransition :Boolean,tag :String){
        val fragmentTransition = requireActivity().supportFragmentManager.beginTransaction()
        if(isTransition){
            fragmentTransition.setCustomAnimations(android.R.anim.slide_out_right,android.R.anim.slide_in_left)
        }
        fragmentTransition.replace(R.id.frame_layout,fragment,tag).addToBackStack(fragment.javaClass.simpleName).commit()
    }
    fun justReplaceFragment(fragment: Fragment,isTransition :Boolean,tag :String){
        val fragmentTransition = requireActivity().supportFragmentManager.beginTransaction()
        if(isTransition){
            fragmentTransition.setCustomAnimations(android.R.anim.slide_out_right,android.R.anim.slide_in_left)
        }
        fragmentTransition.replace(R.id.frame_layout,fragment,tag).commit()
    }
}