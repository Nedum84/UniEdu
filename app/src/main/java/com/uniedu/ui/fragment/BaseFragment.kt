package com.uniedu.ui.fragment

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.uniedu.utils.ClassSharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

abstract class BaseFragment : Fragment(), CoroutineScope{
    lateinit var thisContext: Activity
    lateinit var prefs: ClassSharedPreferences

    private lateinit var job: Job


    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        thisContext = requireActivity()
        prefs = ClassSharedPreferences(thisContext)

        job = Job()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}