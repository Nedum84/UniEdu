package com.uniedu.extension

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog


fun Context.makeFullScreen(frag:DialogFragment): Dialog{
    val dialog = BottomSheetDialog(this, frag.theme)
    dialog.setOnShowListener {

        val bottomSheetDialog = it as BottomSheetDialog
        val parentLayout =  bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        parentLayout?.let { it ->
            val behaviour = BottomSheetBehavior.from(it)
            setupFullHeight(it)
            behaviour.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }
    return dialog
}

fun setupFullHeight(bottomSheet: View) {
    val layoutParams = bottomSheet.layoutParams
    layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
    bottomSheet.layoutParams = layoutParams
}