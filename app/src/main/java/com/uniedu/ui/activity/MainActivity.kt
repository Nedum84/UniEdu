package com.uniedu.ui.activity

import android.os.Bundle
import android.os.Environment
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.uniedu.R
import com.uniedu.UrlHolder
import com.uniedu.databinding.ActivityMainBinding
import com.uniedu.extension.toast
import com.uniedu.model.MyDetails
import com.uniedu.ui.fragment.bottomsheet.FragmentAsk
import com.uniedu.utils.ClassCustomExceptionHandler
import com.uniedu.utils.ClassSharedPreferences
import it.sephiroth.android.library.bottomnavigation.BottomNavigation


class MainActivity : AppCompatActivity(), BottomNavigation.OnMenuItemSelectionListener {
    lateinit var binding: ActivityMainBinding

    lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appCrashReport()//SEND APP CRASH REPORTS

//        setContentView(R.layout.activity_main)
//        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
//        DataBindingUtil.setContentView(this, R.layout.activity_main) as ActivityMainBinding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.apply {
            lifecycleOwner = this@MainActivity
        }

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
//        navController = findNavController(R.id.nav_host_fragment)
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        appBarConfiguration = AppBarConfiguration(setOf(
//            R.id.nav_home,
//            R.id.nav_gallery,
//            R.id.nav_slideshow
//        ), drawerLayout)
//        setupActionBarWithNavController(navController, appBarConfiguration)
//        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            if (destination.id == R.id.fragmentEBook || destination.id == R.id.fragmentQuestion || destination.id == R.id.fragmentTopicVideo) {
//                supportActionBar?.show()
//                hideStepView()
            } else {
//                supportActionBar?.hide()
                when (destination.id) {
//                    R.id.fragmentLogComplainForm1RapeVictim -> showStepView(0)
//                    R.id.fragmentLogComplainForm2TypeOfVictim -> showStepView(1)
//                    R.id.fragmentLogComplainForm3TypeOfRape -> showStepView(2)
//                    R.id.fragmentLogComplainForm4SelectSupport -> showStepView(3)
//                    R.id.fragmentLogComplainForm5RapeDetail -> showStepView(4)
                }
            }
        }


//        val f = FragmentAsk.newInstance(Questions())
        FragmentAsk().apply {
//            show(supportFragmentManager, tag)
        }
//        f.apply {
//            show(supportFragmentManager, tag)
//        }

        val myDetails = MyDetails(1,"","","","","","42","1")
        ClassSharedPreferences(this).setCurUserDetail(Gson().toJson(myDetails))

        initializeBottomNavigation(savedInstanceState)
        mBottomNavigation.menuItemSelectionListener = this


//        mBottomNavigation.menuItemSelectionListener = object :BottomNavigation.OnMenuItemSelectionListener{
//            override fun onMenuItemReselect(itemId: Int, position: Int, fromUser: Boolean) {
//                TODO("Not yet implemented")
//            }
//
//            override fun onMenuItemSelect(itemId: Int, position: Int, fromUser: Boolean) {
//                this@MainActivity.toast("${position}...XSS")
//            }
//
//        }
    }
    lateinit var mBottomNavigation:BottomNavigation

    protected fun initializeBottomNavigation(savedInstanceState: Bundle?) {
        if (null == savedInstanceState) {
            mBottomNavigation = findViewById(R.id.bottomNavigation)
            mBottomNavigation.setDefaultSelectedIndex(0)
//            val provider: BadgeProvider? = mBottomNavigation.badgeProvider
//            provider?.show(R.id.bbn_item3)
//            provider?.show(R.id.bbn_item4)
//            provider?.remove(R.id.bbn_item2)
        }
    }



    override fun onSupportNavigateUp(): Boolean {
//        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bottom_nav_menu, menu)
        return true
    }
    override fun onMenuItemReselect(itemId: Int, position: Int, fromUser: Boolean) {
        this.toast("$itemId")
    }

    override fun onMenuItemSelect(itemId: Int, position: Int, fromUser: Boolean) {
        when(itemId){
            R.id.bbn_home->{
                navController.popBackStack()
//                navController.navigate(R.id.nav_home)
            }
            R.id.bbn_video ->{
//                navController.navigate(FragmentHomeDirections.actionNavHomeToFragmentTopicVideo())

                navNavigate(R.id.fragmentTopicVideo)
            }
            R.id.bbn_ebook ->{
                navNavigate(R.id.fragmentAsk)
            }
            R.id.bbn_qa ->{
//                findNavController(R.id.nav_host_fragment).navigate(FragmentHomeDirections.actionNavHomeToFragmentQuestion())
                navNavigate(R.id.fragmentQuestion)
            }
        }
    }

    fun navNavigate(itemId: Int){
        try {
            if (navController.currentDestination!!.id != R.id.nav_home)navController.popBackStack()
            navController.navigate(itemId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


//    fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        if (savedInstanceState == null) getChildFragmentManager()
//            .beginTransaction()
//            .setCustomAnimations(R.anim.none, 0, 0, R.anim.none)
//            .add(R.id.content, content)
//            .commitNow()
//    }

//    fun show(transaction: FragmentTransaction, tag: String?): Int {
//        initBuilderArguments()
//        return if (fullScreen) {
//            transaction.setCustomAnimations(R.anim.slide_in_bottom, 0, 0, R.anim.slide_out_bottom)
//            transaction.add(android.R.id.content, this, tag).addToBackStack(null).commit()
//        } else {
//            super.show(transaction, tag)
//        }
//    }
//
//    private fun initBuilderArguments() {
//        val builderData: Bundle = getArguments()
//        title =
//            builderData.getString(com.franmontiel.fullscreendialog.FullScreenDialogFragment.BUILDER_TITLE)
//        positiveButton =
//            builderData.getString(com.franmontiel.fullscreendialog.FullScreenDialogFragment.BUILDER_POSITIVE_BUTTON)
//        fullScreen = builderData.getBoolean(
//            com.franmontiel.fullscreendialog.FullScreenDialogFragment.BUILDER_FULL_SCREEN,
//            true
//        )
//        extraItemsResId = builderData.getInt(
//            com.franmontiel.fullscreendialog.FullScreenDialogFragment.BUILDER_EXTRA_ITEMS,
//            com.franmontiel.fullscreendialog.FullScreenDialogFragment.NO_EXTRA_ITEMS
//        )
//    }




    //    FOR CRASH REPORTS
    private fun appCrashReport(){
        if (Thread.getDefaultUncaughtExceptionHandler() !is ClassCustomExceptionHandler){
            Thread.setDefaultUncaughtExceptionHandler(ClassCustomExceptionHandler(
                Environment.getExternalStorageDirectory().path + "/"+ UrlHolder.APP_FOLDER_NAME+"/reports", "http://<desired_url>/upload.php"))
        }
    }
}