package com.uniedu.ui.fragment.bottomsheet

import android.app.Dialog
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.uniedu.R
import com.uniedu.databinding.FragmentSellItemBinding
import com.uniedu.extension.*
import com.uniedu.model.ItemCategory
import com.uniedu.model.ItemsForSale
import com.uniedu.network.RetrofitPOST
import com.uniedu.network.ServerResponse
import com.uniedu.ui.fragment.BaseFragmentBottomSheetUploadFile
import com.uniedu.viewmodel.*
import jp.wasabeef.richeditor.RichEditor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import kotlin.collections.HashMap

private const val ITEM = "item_for_sale"

class FragmentSellItem : BaseFragmentBottomSheetUploadFile() {
    private var item: ItemsForSale? = null
    lateinit var binding:FragmentSellItemBinding

    lateinit var modelItemsForSale: ModelItemsForSale

    private lateinit var mEditor: RichEditor
    private var itemCategory: ItemCategory? = null
    private var is_adding_new = true



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
        arguments?.let {
            item = it.getParcelable(ITEM)

            if (!item!!.item_name.isNullOrEmpty()){
                launch {
                    is_adding_new = false
                    withContext(Dispatchers.Main){
                        itemCategory = db.itemCategoryDao.getById(item!!.item_category.toLong())
                        mEditor.html = item?.item_description
                        binding.itemCategory.text = itemCategory?.category_name
                    }
                }
            }
        }
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sell_item, container, false)

        initToolbar(binding.root)
        mEditor = binding.editor
        binding.root.setupTextEditor(mEditor)
        mEditor.setPlaceholder("Additional Details...")

        bindActions()

        return binding.root
    }

    private fun bindActions() {
        binding.toolbar.setNavigationOnClickListener {
            dismiss()
        }

        binding.itemCategory.setOnClickListener{
            requireActivity().let {
                FragmentChooseItemCategory().apply {
                    show(it.supportFragmentManager, tag)
                }
            }
        }

        binding.submitBTN.setOnClickListener {
            checkInputs()
        }
    }

    private fun checkInputs() {
        val itemName = binding.itemName.text.trim()
        val itemPrice = binding.itemPrice.text.trim()
        val itemQty = binding.itemQty.text.trim()

        if (itemName.isEmpty()){
            thisContext.toast("Enter product name")
        }else if (itemQty.isEmpty()){
            thisContext.toast("Enter quantity")
        }else if (itemPrice.isEmpty()){
            thisContext.toast("Enter price")
        }else if (itemCategory==null || itemCategory!!.category_name.isNullOrEmpty()){
            context.let {it!!.toast("Choose category")}
        }else{
            pDialog?.createDialog()
            val imgPaths = mEditor.html.getImgPaths()
            if (imgPaths.isEmpty()){
                publishContent()
            }else{
                uploadImage(imgPaths)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val viewModelFactory = ModelItemForSaleCategory.Factory(application)
        val modelItemCategory = requireActivity().run{
            ViewModelProvider(this, viewModelFactory).get(ModelItemForSaleCategory::class.java)
        }
        modelItemCategory.curItemCategory.observe(viewLifecycleOwner, Observer {
            it?.getContentIfNotHandled().let {c->
                binding.itemCategory.text = c?.category_name
                itemCategory = c
            }
        })


        val vFactory = ModelItemsForSale.Factory(requireActivity().application)
        modelItemsForSale = requireActivity().run{
            ViewModelProvider(this, vFactory).get(ModelItemsForSale::class.java)
        }


        modelUploadFile = requireActivity().run {
            ViewModelProvider(this).get(ModelUploadFile::class.java)
        }
        modelUploadFile.imageUploaded.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.getContentIfNotHandled()?.let {content->
                publishContent(content)
            }
        })
    }


    private fun publishContent(content:String = mEditor.html) {
        val retrofit = RetrofitPOST.retrofitWithJsonResponse.create(SellItemService::class.java)
        retrofit.sellItemRequest(
            request_type = "sell_item",
            item_name = binding.itemName.text.toString(),
            school_id = myDetails.user_school,
            item_description = content,
            item_category = itemCategory!!.category_id.toString(),
            item_qty = binding.itemQty.text.toString(),
            item_uploaded_by = myDetails.user_id,
            imgMap = HashMap(),
            item_price = binding.itemPrice.text.toString(),
            is_adding_new = is_adding_new
        ).enqueue(object :Callback<ServerResponse>{
            override fun onFailure(call: Call<ServerResponse>, t: Throwable) {
                pDialog?.dismissDialog()
                requireContext().toast("No internet connect!")
                t.printStackTrace()
            }

            override fun onResponse(call: Call<ServerResponse>,response: Response<ServerResponse>) {
                pDialog?.dismissDialog()
                if (response.isSuccessful) {
                    if (response.body() != null) {

                        val serverResponse = response.body()
                        if (!(serverResponse!!.success as Boolean)){
                            context!!.toast(serverResponse.respMessage!!)
                        }else{
                            thisContext.toast(if (is_adding_new) "Product published" else "Product updated")

                            try {//add to database
                                val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                                val type = Types.newParameterizedType(MutableList::class.java, ItemsForSale::class.java)
                                val adapter: JsonAdapter<List<ItemsForSale>> = moshi.adapter(type)
                                val item: List<ItemsForSale> = adapter.fromJson(serverResponse.otherDetail!!)!!
                                modelItemsForSale.addToDb(item)

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            published()
                        }
                    }
                } else {
                    context!!.toast("An error occurred, Try again")
                }
            }

        })
    }


    //Make full screen
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = context?.makeFullScreen(this)
        return dialog!!
    }


    companion object {

        @JvmStatic
        fun newInstance(param: Parcelable) =
            FragmentSellItem().apply {
                arguments = Bundle().apply {
                    putParcelable(ITEM, param)
                }
            }
    }
}


interface SellItemService {
    @Multipart
    @POST("sell_item.php")
    fun sellItemRequest(
        @Part("request_type") request_type: String,
        @Part("item_name") item_name: String,
        @Part("item_description") item_description: String,
        @Part("item_category") item_category: String,
        @Part("item_price") item_price: String,
        @Part("item_qty") item_qty: String,
        @Part("item_uploaded_by") item_uploaded_by: Int,
        @PartMap imgMap: Map<String, @JvmSuppressWildcards RequestBody>? = null,
        @Part("is_adding_new") is_adding_new:Boolean, //to know if you are adding or editing(false)
        @Part("school_id") school_id:String
    ): Call<ServerResponse>
}