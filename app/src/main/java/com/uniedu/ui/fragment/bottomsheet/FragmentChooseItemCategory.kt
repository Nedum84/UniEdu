package com.uniedu.ui.fragment.bottomsheet

import android.app.AlertDialog
import android.app.Dialog
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.uniedu.R
import com.uniedu.adapter.AdapterCourses
import com.uniedu.adapter.AdapterItemForSaleCategory
import com.uniedu.adapter.CourseClickListener2
import com.uniedu.adapter.ItemForSaleCatClickListener
import com.uniedu.databinding.FragmentChooseCourseBinding
import com.uniedu.databinding.FragmentChooseItemCategoryBinding
import com.uniedu.extension.toast
import com.uniedu.model.Courses
import com.uniedu.network.RetrofitPOST
import com.uniedu.network.ServerResponse
import com.uniedu.ui.fragment.BaseFragmentBottomSheet
import com.uniedu.utils.ClassAlertDialog
import com.uniedu.utils.ClassProgressDialog
import com.uniedu.utils.ClassUtilities
import com.uniedu.viewmodel.ModelCourses
import com.uniedu.viewmodel.ModelItemForSaleCategory
import com.uniedu.viewmodel.ModelItemsForSale
import kotlinx.android.synthetic.main.dialog_add_course.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

class FragmentChooseItemCategory : BaseFragmentBottomSheet() {
    lateinit var binding:FragmentChooseItemCategoryBinding
    lateinit var modeItemCategory: ModelItemForSaleCategory
    lateinit var ADAPTER: AdapterItemForSaleCategory
    var course:Courses? = null



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val viewModelFactory = ModelCourses.Factory(requireActivity().application)
        modeItemCategory = requireActivity().run{
            ViewModelProvider(this, viewModelFactory).get(ModelItemForSaleCategory::class.java)
        }


        modeItemCategory.itemCategories().observe(viewLifecycleOwner, Observer {
            it?.let {
                ADAPTER.list = it
            }
        })
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_choose_item_category, container, false)

        val viewModelFactory = ModelItemForSaleCategory.Factory(application)
          modeItemCategory = ViewModelProvider(this, viewModelFactory).get(ModelItemForSaleCategory::class.java)
        binding.apply {
            lifecycleOwner = this@FragmentChooseItemCategory
        }

        ADAPTER = AdapterItemForSaleCategory(ItemForSaleCatClickListener {
            modeItemCategory.setItemCategory(it)
            dialog?.dismiss()
        })

        binding.recyclerItemCategory.apply {
            adapter = ADAPTER
            layoutManager= LinearLayoutManager(activity)
            itemAnimator = DefaultItemAnimator()
        }


        return binding.root
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), theme).apply {
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
//            behavior.state = BottomSheetBehavior.STATE_EXPANDED
//            behavior.peekHeight = PEEK_HEIGHT_AUTO
//            behavior.peekHeight = Resources.getSystem().displayMetrics.heightPixels
            behavior.peekHeight = Resources.getSystem().displayMetrics.heightPixels-200
        }
    }

}
