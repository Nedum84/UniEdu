package com.uniedu.ui.fragment.bottomsheet

import android.app.Dialog
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.uniedu.R
import com.uniedu.databinding.FragmentItemForSaleDetailBinding
import com.uniedu.extension.*
import com.uniedu.model.EBooks
import com.uniedu.model.ItemsForSale
import com.uniedu.ui.fragment.BaseFragmentBottomSheet
import com.uniedu.viewmodel.ModelItemsForSale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


private const val ITEM = "item_for_sale"


class FragmentItemForSaleDetail : BaseFragmentBottomSheet() {
    lateinit var modelItemsForSale: ModelItemsForSale
    private var item: ItemsForSale? = null

    lateinit var binding:FragmentItemForSaleDetailBinding


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        arguments?.let {
            item = it.getParcelable(ITEM)
        }
        val viewModelFactory = ModelItemsForSale.Factory(application)
        modelItemsForSale = requireActivity().run{
            ViewModelProvider(this, viewModelFactory).get(ModelItemsForSale::class.java)
        }
        modelItemsForSale.items().observe(viewLifecycleOwner, Observer {
            it?.let {
                item = it.first { it.item_id == item?.item_id }

                modelItemsForSale.setCurItem(item!!)
            }
        })
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val itCat = db.itemCategoryDao.getById(item!!.item_id.toLong())
                binding.itemCategory = itCat?.category_name
            } catch (e: Exception) {e.printStackTrace()}
        }
        binding.apply { lifecycleOwner = this@FragmentItemForSaleDetail }
        modelItemsForSale.setCurItem(item!!)
        binding.item = modelItemsForSale
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_item_for_sale_detail, container, false)
        // Inflate the layout for this fragment


        initToolbar()
        bindActions()

        return binding.root
    }

    private fun bindActions() {
        binding.editItem.setOnClickListener {
            requireActivity().let {
                FragmentSellItem.newInstance(item!!).apply {
                    show(it.supportFragmentManager, tag)
                }
            }
        }

        binding.callSeller.setOnClickListener {
            context?.toast("Call started...")
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
            FragmentItemForSaleDetail().apply {
                arguments = Bundle().apply {
                    putParcelable(ITEM, param)
                }
            }
    }
}
