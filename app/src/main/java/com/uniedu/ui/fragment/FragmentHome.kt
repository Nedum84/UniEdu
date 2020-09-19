package com.uniedu.ui.fragment

import android.app.ProgressDialog.show
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.OnMenuItemClickListener
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import com.uniedu.R
import com.uniedu.ui.fragment.bottomsheet.FragmentChooseCourse
import com.uniedu.ui.fragment.bottomsheet.FragmentChooseSchool
import com.uniedu.ui.home.HomeViewModel
import kotlinx.android.synthetic.main.fragment_home.*


class FragmentHome : Fragment() {
    lateinit var powerMenu:PowerMenu

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
//        val textView: TextView = root.findViewById(R.id.text_home)
//        homeViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })

//            .showAsAnchorLeftTop(view) // showing the popup menu as left-top aligns to the anchor.
//            .showAsAnchorLeftBottom(view) // showing the popup menu as left-bottom aligns to the anchor.
//            .showAsAnchorRightTop(view) // using with .setAnimation(MenuAnimation.SHOWUP_TOP_RIGHT) looks better
//            .showAsAnchorRightBottom(view) // using with .setAnimation(MenuAnimation.SHOWUP_TOP_RIGHT) looks better
//            .showAsAnchorCenter(view) // using with .setAnimation(MenuAnimation.SHOW_UP_CENTER) looks better

        powerMenu = PowerMenu.Builder(requireActivity())
//            .addItemList(list) // list has "Novel", "Poerty", "Art"
            .addItem(PowerMenuItem("Journals", false)) // add an item.
            .addItem(PowerMenuItem("Travel", false)) // aad an item list.
            .addItem(PowerMenuItem("Games", false)) // aad an item list.
            .addItem(PowerMenuItem("Gallery", false)) // aad an item list.
            .setAnimation(MenuAnimation.SHOWUP_TOP_LEFT) // Animation start point (TOP | LEFT).
            .setMenuRadius(10f) // sets the corner radius.
            .setMenuShadow(10f) // sets the shadow.
            .setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorAccent))
            .setTextGravity(Gravity.CENTER)
            .setTextTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD))
            .setSelectedTextColor(Color.WHITE)
            .setMenuColor(Color.WHITE)
            .setSelectedMenuColor(ContextCompat.getColor(requireActivity(), R.color.colorPrimary))
            .setOnMenuItemClickListener(onMenuItemClickListener)
            .build()

        return root
    }
    private val onMenuItemClickListener =  OnMenuItemClickListener<PowerMenuItem> { position, item ->
            Toast.makeText(requireActivity(), item.title, Toast.LENGTH_SHORT).show()
            powerMenu.setSelectedPosition(position) // change selected item
            powerMenu.dismiss()
        }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        school_selections.setOnClickListener {
//            powerMenu.showAsDropDown(it) // view is an anchor


            requireActivity()?.let {
                FragmentChooseCourse().apply {
//                    fragmentManager?.let { it1 -> show(it1, FragmentChooseCourse().tag) }
                    show(it.supportFragmentManager, tag)
                }
            }
        }
    }
}