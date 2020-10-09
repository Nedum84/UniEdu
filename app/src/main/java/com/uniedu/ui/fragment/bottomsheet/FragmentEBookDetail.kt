package com.uniedu.ui.fragment.bottomsheet

import android.app.Dialog
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.uniedu.R
import com.uniedu.databinding.FragmentEBookDetailBinding
import com.uniedu.extension.*
import com.uniedu.model.Answers
import com.uniedu.model.EBooks
import com.uniedu.model.Questions
import com.uniedu.ui.fragment.BaseFragmentBottomSheet
import com.uniedu.utils.ClassProgressDialog
import com.uniedu.viewmodel.ModelAnswersFrag
import com.uniedu.viewmodel.ModelEbook
import jp.wasabeef.richeditor.RichEditor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


private const val EBOOK = "ebook"


class FragmentEBookDetail : BaseFragmentBottomSheet() {
    lateinit var modelEbook: ModelEbook
    private var ebook: EBooks? = null

    lateinit var binding:FragmentEBookDetailBinding


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        arguments?.let {
            ebook = it.getParcelable(EBOOK)
        }
        val viewModelFactory = ModelEbook.Factory(application)
        modelEbook = requireActivity().run{
            ViewModelProvider(this, viewModelFactory).get(ModelEbook::class.java)
        }
        modelEbook.ebooks().observe(viewLifecycleOwner, Observer {
            it?.let {
                ebook = it.first { it.book_id == ebook?.book_id }

                modelEbook.setCurEBook(ebook!!)
                launch {updateCourseCode()}
            }
        })
        binding.apply { lifecycleOwner = this@FragmentEBookDetail }
        modelEbook.setCurEBook(ebook!!)
        binding.ebook = modelEbook
    }

    suspend fun updateCourseCode(){
        withContext(Dispatchers.Default){
            try {
                val course = db.coursesDao.getById(ebook!!.course_id.toInt())
                binding.courseCode = course?.courseCode()
            } catch (e: Exception) {e.printStackTrace()}
        }
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_e_book_detail, container, false)
        // Inflate the layout for this fragment


        initToolbar()
        bindActions()

        return binding.root
    }

    private fun bindActions() {
        binding.editEbook.setOnClickListener {
            requireActivity().let {
                FragmentUploadEBook.newInstance(ebook).apply {
                    show(it.supportFragmentManager, tag)
                }
            }
        }

        binding.downloadEbook.setOnClickListener {
            context?.toast("Download started...")
        }

    }


    private fun initToolbar() {
        val toolbar = binding.root.findViewById(R.id.toolbar) as androidx.appcompat.widget.Toolbar
        toolbar.setNavigationOnClickListener {
            dismiss()
            dialog?.dismiss()
        }
    }







    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = context?.makeFullScreen(this)
        return dialog!!
    }


    companion object {

        @JvmStatic
        fun newInstance(param: Parcelable) =
            FragmentEBookDetail().apply {
                arguments = Bundle().apply {
                    putParcelable(EBOOK, param)
                }
            }
    }
}

