package com.example.imprints

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.imprints.adapter.RvAdapter
import com.example.imprints.entity.Notes
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*

class   HomeFragment : BaseFragment(){

    private var allNotes: List<Notes>? = null
    var fragment :CreateNotesFragment? = null
    val CREATE_NOTES_FRAGMENT_TAG = "create_notes_fragment_key"

    private  var mAdapter :RvAdapter = RvAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // retain this fragment when activity is re-initialized
        if(isCreateNotesFragmentActive == 1)
        {justReplaceFragment(fragment!!,true,CREATE_NOTES_FRAGMENT_TAG)
            isCreateNotesFragmentActive =0
        }
        retainInstance = true
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            HomeFragment().apply {
                arguments = Bundle().apply {

                }
            }
//        var  isCreateNotesFragmentActive : Int = 0
    }

    private val onClicked = object :RvAdapter.NotesOnClickInterface{

        override fun notesOnClicked(notes: Notes) {
            fragment =  CreateNotesFragment.newInstance(notes,true)
            replaceFragment(fragment!!,true,CREATE_NOTES_FRAGMENT_TAG)

        }

    }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rv = view.recyclerView
        rv.layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
            rv.adapter =mAdapter
        mAdapter.setUpdateNoteListner(onClicked)
        viewModel.allNotes.observe(viewLifecycleOwner, Observer {
            mAdapter.updateList(it)
            allNotes = it
        })




    floatingActionButton.setOnClickListener {
            if(savedInstanceState != null
                    &&
                    requireActivity().supportFragmentManager.findFragmentByTag(CREATE_NOTES_FRAGMENT_TAG) != null){
                Log.d("omega ranger"," (At HomeFragment) using retainedInstance of CreateNoteFragment")
               fragment = (requireActivity().supportFragmentManager.findFragmentByTag(CREATE_NOTES_FRAGMENT_TAG)) as CreateNotesFragment?
                justReplaceFragment(fragment as Fragment,true,CREATE_NOTES_FRAGMENT_TAG)
            }
            else {  Log.d("omega ranger"," (At HomeFragment) creating newInstance of CreateNoteFragment")
              fragment =  CreateNotesFragment.newInstance()
                replaceFragment(fragment!!,true,CREATE_NOTES_FRAGMENT_TAG)
            }
        }

 seachView.isFocusable = false
            seachView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    val arrNotes = ArrayList<Notes>()
                    if (allNotes?.size != null) {
                       if (allNotes?.size!! > 0 ) {
                           for (arr in allNotes!!) {

                               if (newText != null) {
                                   if(arr.title!!.contains(newText.trim(),true) || arr.sub_title?.contains(newText.trim(),true) == true || arr.content?.contains(newText.trim(),true)  ==true){
                                       arrNotes.add(arr)
                                   }

                               }
                           }
                       }
                    }

                    mAdapter.updateList(arrNotes)
                    return true
                }

            })

    }



    //        launch {
//            context?.let {
//                NotesDB.getDataBase(it).NotesDao().delete(note)
//            }
//   u can do anything in background here inside launch block, as we hv done required things in our base fragment
//        }
//    }

}