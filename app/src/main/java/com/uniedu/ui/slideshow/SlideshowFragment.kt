package com.uniedu.ui.slideshow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.uniedu.R
import kotlinx.android.synthetic.main.fragment_slideshow.*

class SlideshowFragment : Fragment() {

    private lateinit var slideshowViewModel: SlideshowViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        slideshowViewModel =
                ViewModelProviders.of(this).get(SlideshowViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_slideshow, container, false)
//        val textView: TextView = root.findViewById(R.id.text_slideshow)
        slideshowViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        requireActivity().setSupportActionBar(toolbar)
        requireActivity().actionBar?.setDisplayHomeAsUpEnabled(true)
        requireActivity().actionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener {

        }
        toolbar.inflateMenu(R.menu.menu_question)
        toolbar.setNavigationOnClickListener {
            Toast.makeText(requireActivity(),"Home button clicked...", Toast.LENGTH_SHORT).show()
        }
        toolbar.setOnMenuItemClickListener {
            when(it.itemId){
                android.R.id.home->{
//                    Toast.makeText(requireActivity(),"Home button clicked...", Toast.LENGTH_SHORT).show()
                }
                R.id.action_menu_filter_question->{
                    Toast.makeText(requireActivity(),"Filter button clicked...", Toast.LENGTH_SHORT).show()
                }
                else -> super.onOptionsItemSelected(it)
            }

            true
        }


//        val a: Animation = AnimationUtils.loadAnimation(context, R.anim.progress_anim)
//        a.duration = 1000
//        imageView.startAnimation(a)
    }


}