package com.uniedu.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.uniedu.R
import com.uniedu.adapter.AdapterItemForSale
import com.uniedu.adapter.ItemForSaleClickListener
import com.uniedu.databinding.FragmentItemForSaleBinding
import com.uniedu.model.ItemsForSale
import com.uniedu.ui.fragment.bottomsheet.FragmentEBookDetail
import com.uniedu.ui.fragment.bottomsheet.FragmentItemForSaleDetail
import com.uniedu.ui.fragment.bottomsheet.FragmentSellItem
import com.uniedu.ui.fragment.bottomsheet.FragmentUploadEBook
import com.uniedu.utils.ClassUtilities
import com.uniedu.viewmodel.ModelEbook
import com.uniedu.viewmodel.ModelItemsForSale


class FragmentItemForSale : BaseFragment() {
    lateinit var binding:FragmentItemForSaleBinding
    lateinit var modelEbook: ModelItemsForSale
    lateinit var ADAPTER: AdapterItemForSale

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val viewModelFactory = ModelItemsForSale.Factory(application)
        modelEbook = requireActivity().run{
            ViewModelProvider(this, viewModelFactory).get(ModelItemsForSale::class.java)
        }


        modelEbook.items().observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it.isEmpty()&&ADAPTER.itemCount==0){
//                    binding.noComplainWrapper.visibility = View.VISIBLE
                }else{
//                    binding.noComplainWrapper.visibility = View.GONE

                    ADAPTER.list =it
                }
            }
        })
        modelEbook.feedBack.observe(viewLifecycleOwner, Observer {
            when(it){
                "network_success"->{}
                "network_error"->{
//                    if (questions.isEmpty()){
//                        //show no network tag
//                    }
                }
            }
        })

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_item_for_sale, container, false)

        initToolbar()
        bindActions()

        binding.apply {
            lifecycleOwner = this@FragmentItemForSale
        }


        ADAPTER = AdapterItemForSale(object : ItemForSaleClickListener{
            override fun onClick(item: ItemsForSale) {
                val frag = FragmentItemForSaleDetail.newInstance(item)
                requireActivity().let {
                    frag.apply {
                        show(it.supportFragmentManager, tag)
                    }
                }
            }

            override fun onMenuClick(item: ItemsForSale, view: View) {
                //creating a popup menu
                val popup = PopupMenu(thisContext, view)
                //inflating menu from xml resource
                popup.inflate(R.menu.menu_item_for_sale)
                //adding click listener
                popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                    override fun onMenuItemClick(item: MenuItem): Boolean {
                        when (item.itemId) {
                            R.id.action_menu_1 -> {
//                                requireActivity().let {
//                                    FragmentUploadEBook.newInstance(item).apply {
//                                        show(it.supportFragmentManager, tag)
//                                    }
//                                }
                            }
                            R.id.action_menu_2 -> {
                            }
                            R.id.action_menu_3 -> {
                            }
                        }

                        return false
                    }
                })
                //displaying the popup
                popup.show()
            }
        })
        binding.recyclerItemForSale.apply {
            adapter = ADAPTER
            layoutManager= GridLayoutManager(activity,  ClassUtilities().calculateNoOfColumns(thisContext,128f))
            itemAnimator = DefaultItemAnimator()
            isNestedScrollingEnabled = false
        }


        return binding.root
    }

    private fun bindActions() {
        binding.sellItem.setOnClickListener {
            requireActivity().let {
                FragmentSellItem().apply {
                    show(it.supportFragmentManager, tag)
                }
            }
        }
    }

    private fun initToolbar() {
        binding.toolbar.inflateMenu(R.menu.menu_question)
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.toolbar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.action_menu_filter_question->{

                }
                else -> super.onOptionsItemSelected(it)
            }

            true
        }
    }

}