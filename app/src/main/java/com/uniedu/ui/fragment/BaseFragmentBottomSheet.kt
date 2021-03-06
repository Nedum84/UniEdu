package com.uniedu.ui.fragment

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.uniedu.model.MyDetails
import com.uniedu.room.DatabaseRoom
import com.uniedu.utils.ClassSharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

abstract class BaseFragmentBottomSheet : BottomSheetDialogFragment(), CoroutineScope{
    lateinit var thisContext: Activity
    lateinit var prefs: ClassSharedPreferences
    lateinit var myDetails: MyDetails
    val application: Application by lazy { requireNotNull(this.activity).application }
    val db:DatabaseRoom by lazy { DatabaseRoom.getDatabaseInstance(application) }

    private lateinit var job: Job


    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        thisContext = requireActivity()
        prefs = ClassSharedPreferences(thisContext)
        myDetails = prefs.getCurUserDetail()

        job = Job()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}