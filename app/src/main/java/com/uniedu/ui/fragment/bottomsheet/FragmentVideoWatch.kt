package com.uniedu.ui.fragment.bottomsheet

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import com.google.android.youtube.player.YouTubeStandalonePlayer
import com.google.gson.Gson
import com.uniedu.R
import com.uniedu.adapter.AdapterVideo
import com.uniedu.databinding.FragmentVideoWatchBinding
import com.uniedu.model.Videos
import com.uniedu.ui.fragment.BaseFragmentBottomSheet
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList

private const val VIDEO = "cur_video"

class FragmentVideoWatch : BaseFragmentBottomSheet(){
    lateinit var binding: FragmentVideoWatchBinding
    private var currentVideoURL:String? =null
    private var currentVideoId:String? =null
    private var backgroundMusicPlayStatus = false
    var videoPlayer: YouTubePlayer? = null
    lateinit var yPlayerFrag:YouTubePlayerSupportFragment
    var fullScreen:Boolean = false

    lateinit var currentVDetails:Videos

    lateinit var ADAPTER : AdapterVideo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(VIDEO)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_video_watch, container, false)

        return binding.root
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_video_watch)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        thisContext = this
        sqLiteDBHelper = SQLiteDBHelper(this)
        prefs = ClassSharedPreferences(thisContext)


        initViews()
        loadTopics()
        loadTopicVideos()

        initializeVideoDetailSlideUp()
        initializeTopicSlideUp()


        tapToRetry.setOnClickListener {
            loadTopicVideos()
        }

        //background music
        backgroundSongPlayToggle?.setOnClickListener {
            if (!backgroundMusicPlayStatus){
                playBackgroundMusic()
            }else{
                stopBackgroundMusic()
            }
        }

        videoTitleWrapper.setOnClickListener {//videoTitleWrapper
            if(videoDetailsSlideUp.isVisible){
                videoDetailsSlideUp.hide()
                drop_down_toggle?.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp)
            }else{
                videoDetailsSlideUp.show()
                drop_down_toggle?.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp)
            }

        }

        changeTopicView.setOnClickListener {
            if(topicsSlideUp.isVisible){
                topicsSlideUp.hide()
            }else{
                topicsSlideUp.show()
            }
            addAutoScroll()
        }

        full_screen_view.setOnClickListener{
            val intent = YouTubeStandalonePlayer.createVideoIntent(this, UrlHolder.YOUTUBE_DEVELOPER_KEY, currentVideoURL)
            startActivity(intent)
        }
    }

    private fun initializeTopicSlideUp(){
        topicsSlideUp = SlideUpBuilder(topicsWrapper)
            .withListeners(object : SlideUp.Listener.Events {
                override fun onSlide(percent: Float) {}
                override fun onVisibilityChanged(visibility: Int) {}
            })
            .withStartGravity(Gravity.BOTTOM)
            .withLoggingEnabled(true)
            .withGesturesEnabled(true)
//                .withInterpolator()
            .withStartState(SlideUp.State.HIDDEN)
            .withAutoSlideDuration(0)
//                .withTouchableAreaPx(30f)//touchable area in px
            .withTouchableAreaDp(50f)//touchable area in dp
            .withSlideFromOtherView(watchVideo)
            .build()
    }
    private fun initializeVideoDetailSlideUp(){
        videoDetailsSlideUp = SlideUpBuilder(videoDetailsWrapper)
            .withListeners(object : SlideUp.Listener.Events {
                override fun onSlide(percent: Float) {

                }
                override fun onVisibilityChanged(visibility: Int) {
                    if (visibility == View.GONE) {
                    }
                }
            })
            .withStartGravity(Gravity.TOP)
            .withLoggingEnabled(true)
            .withGesturesEnabled(true)
//                .withInterpolator()
            .withStartState(SlideUp.State.HIDDEN)
//                .withAutoSlideDuration(0)
//                .withTouchableAreaPx(30f)//touchable area in px
//                .withSlideFromOtherView(videoTitleWrapper)
            .build()
    }
    @SuppressLint("SetTextI18n")
    private fun initViews(){
        val vDetails = ClassSharedPreferences(this).getCurrentVideoDetails()
        val video = Gson().fromJson(vDetails, Array<VideoList>::class.java).asList()

        currentVDetails = video[0]
        currentVideoId = currentVDetails.video_id
        currentVideoURL = currentVDetails.video_url
        val subName = sqLiteDBHelper.getSubjects(prefs.getCurrentSubjectId()!!)[0].subject_name?.toUpperCase()
        val topicName = prefs.getCurrentTopicName()

        watch_video_title.text = currentVDetails.video_title
        watch_no_of_views.text = currentVDetails.no_of_views+"+ Views"
        video_tutor.text    = "By ${currentVDetails.video_tutor} | $subName($topicName)"



        Handler().postDelayed({
            initializeYoutubePlayer()
        }, 1000) // 1 second delay (takes millis)} (CAN BE REMOVED...)


    }
    fun addAutoScroll(){
        val vIndex = topicVidList.indexOf(currentVDetails)
        val t = sqLiteDBHelper.getTopics()
        var tIndex = 0
        for(tt in topicList){
            if(tt.topic_id == ClassSharedPreferences(this).getCurrentTopicId()!!){
                tIndex = topicList.indexOf(tt);break
            }
        }
        linearLayoutManager.scrollToPositionWithOffset(vIndex,20)
        tLinearLayoutManager.scrollToPositionWithOffset(tIndex,20)
    }


    override fun onVidSelChanged() {
        destroyTimeHandler()
        videoPlayer?.release()
        initViews()
    }

    val videoTimerHandler = Handler()
    private val videoTimerRunnable = object:Runnable {
        override fun run() {
            if(videoPlayer==null)return

            if(videoPlayer!!.durationMillis>1000){
                if(videoSeekBar?.max!=videoPlayer!!.durationMillis){
                    videoSeekBar?.max = videoPlayer!!.durationMillis
                    video_timer?.text = ClassUtilities().getRemainingTime((videoPlayer!!.durationMillis/1000))//in seconds
                }

//                if(!videoPlayer!!.isPlaying)return

                if (videoPlayer!!.currentTimeMillis < videoPlayer!!.durationMillis){
                    videoSeekBar?.progress = videoPlayer!!.currentTimeMillis//in secs
//                video_timer.text = ClassUtilities().getRemainingTime((videoPlayer!!.durationMillis-videoPlayer!!.currentTimeMillis))


                }else{
                    videoSeekBar?.progress = 0
                }
            }


            videoTimerHandler.postDelayed(this, 1000)
        }
    }

    private fun destroyTimeHandler(){
        videoTimerHandler.removeCallbacks(videoTimerRunnable)
        videoTimerHandler.removeCallbacks(null)
        videoTimerHandler.removeCallbacksAndMessages(null)
    }

    private  fun initializeYoutubePlayer(){
        yPlayerFrag = supportFragmentManager.findFragmentById(R.id.youtubeVideoPlayerFragment) as YouTubePlayerSupportFragment


        try {
            yPlayerFrag.initialize(UrlHolder.YOUTUBE_DEVELOPER_KEY, object:YouTubePlayer.OnInitializedListener {
                override fun onInitializationSuccess(provider: YouTubePlayer.Provider, youTubePlayer: YouTubePlayer, wasRestored: Boolean) {

                    videoPlayer = youTubePlayer
//                    seekBarInitialize()


                    if (!wasRestored) {
                        youTubePlayer.cueVideo(currentVideoURL)
                    }else {
                        youTubePlayer.loadVideo(currentVideoURL)
                    }

                    //FULLSCREEN CONTROL(ADD finish(); IN THE onPause())
                    youTubePlayer.fullscreenControlFlags = YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION
                    youTubePlayer.setOnFullscreenListener {
                        fullScreen = it
                    }

                    //                to remove the fullscreen icon (button) 1
//                    val style = YouTubePlayer.PlayerStyle.MINIMAL
//                    youTubePlayer.setPlayerStyle(style)
                    //                youTubePlayer.setFullscreen(true)

                    //                to remove the fullscreen icon (button) 2
                    //                youTubePlayer.setShowFullscreenButton(false);


                }
                override fun onInitializationFailure(provider: YouTubePlayer.Provider, error: YouTubeInitializationResult) {

                    val REQUEST_CODE = 1
                    if (error.isUserRecoverableError) {
                        error.getErrorDialog(this@ActivityWatchVideo, REQUEST_CODE).show()
                    } else {
                        val errorMessage = String.format("Unable to load video... (%1\$s)", error.toString())
                        ClassAlertDialog(thisContext).toast(errorMessage)
                    }
                }
            })
        } catch (e: Exception) {

        }

    }


    private fun playBackgroundMusic(){
        val serviceBackgroundMusic = Intent(this, ServiceBackgroundMusic::class.java)
        startService(serviceBackgroundMusic)
        backgroundMusicPlayStatus = true
        playPauseTextToggler.text="Pause Music"
        playPauseImgToggler.setImageResource(R.drawable.ic_volume_off_black_24dp)
    }
    private fun stopBackgroundMusic(){
        val serviceBackgroundMusic = Intent(this, ServiceBackgroundMusic::class.java)
        stopService(serviceBackgroundMusic)
        backgroundMusicPlayStatus = false
        playPauseTextToggler.text="Play Music"
        playPauseImgToggler.setImageResource(R.drawable.ic_slow_motion_video_black_24dp)
    }

    override fun onBackPressed() {
        if (fullScreen){
            videoPlayer?.setFullscreen(false);
        }else if(topicsSlideUp.isVisible){
            topicsSlideUp.hide()
        } else{
            super.finish()
        }
    }

    public override fun onDestroy() {
        destroyTimeHandler()
        if (videoPlayer != null) {
            videoPlayer?.release()
            videoPlayer = null
        }
        super.onDestroy()
    }
    override fun finish() {
        super.finish()
        stopBackgroundMusic()
        destroyTimeHandler()


        startActivity(Intent(thisContext, ActivityTopicVideoList::class.java))
        overridePendingTransition(R.anim.slide_activity_no_change, R.anim.slide_activity_down)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            super.finish()
        }
        return super.onOptionsItemSelected(item);
    }


    private fun loadTopicVideos(isReloading:Boolean = false){
        linearLayoutManager = LinearLayoutManager(thisContext)
        VIDEOS_ADAPTER = TopicVideosAdapter(topicVidList,thisContext,"watch_video")
        topic_videos_recyclerview?.layoutManager = linearLayoutManager
        topic_videos_recyclerview?.itemAnimator = DefaultItemAnimator()
        topic_videos_recyclerview?.adapter = VIDEOS_ADAPTER

        //creating volley string request
        loadingProgressbar?.visibility = View.VISIBLE
        no_internet_tag?.visibility = View.GONE
        val stringRequest = object : StringRequest(Request.Method.POST, UrlHolder.URL_GET_TOPIC_VIDEOS,
            Response.Listener<String> { sub ->
                loadingProgressbar?.visibility = View.GONE

                try {
                    val obj = JSONObject(sub)
                    if (!obj.getBoolean("error")) {
                        val noOfSub = obj.getJSONArray("topic_videoz_array")

                        if ((noOfSub.length()!=0)){
                            videoTitleWrapper.visibility = View.VISIBLE
                            no_video_found_tag?.visibility = View.GONE

                            val qDataArray = mutableListOf<VideoList>()
                            for (i in 0 until noOfSub.length()) {
                                val objectSubject = noOfSub.getJSONObject(i)
                                qDataArray.add(VideoList(
                                    objectSubject.getString("video_id"),
                                    objectSubject.getString("topic_id"),
                                    objectSubject.getString("video_url"),
                                    objectSubject.getString("video_title"),
                                    objectSubject.getString("video_cover"),
                                    objectSubject.getString("no_of_views"),
                                    objectSubject.getString("video_tutor")
                                ))
                            }
                            VIDEOS_ADAPTER.addItems(qDataArray)
                            addAutoScroll()//scroll to current item

                            if (isReloading){
                                val curVidDetails = ArrayList<VideoList>()
                                curVidDetails.add(qDataArray[0])
                                val jsonText = Gson().toJson(curVidDetails)
                                prefs.setCurrentVideoDetails(jsonText)

                                initViews()
                            }

                        }else{
                            ClassAlertDialog(thisContext).toast("No Video Found...")
                            no_video_found_tag?.visibility = View.VISIBLE
                            videoTitleWrapper?.visibility = View.GONE
                            videoDetailsSlideUp.hide()
                        }
                    } else {
                        ClassAlertDialog(thisContext).toast("An error occurred, try again...")
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener {_ ->
                loadingProgressbar?.visibility = View.GONE
                no_internet_tag?.visibility = View.VISIBLE
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String?> {
                val params = HashMap<String, String?>()
                params["request_type"] = "get_topic_videos"
                params["topic_id"] = prefs.getCurrentTopicId()
                return params
            }
        }
        VolleySingleton.instance?.addToRequestQueue(stringRequest)//adding request to queue
        //volley interactions end

    }

    private fun loadTopics(){
        tLinearLayoutManager = LinearLayoutManager(thisContext)
        TOPICS_ADAPTER = TopicsAdapter(topicList,thisContext,"watch_video")
        topics_recyclerview?.layoutManager = tLinearLayoutManager
        topics_recyclerview?.itemAnimator = DefaultItemAnimator()
        topics_recyclerview?.adapter = TOPICS_ADAPTER

        //creating volley string request
        topicLoadingProgressbar?.visibility = View.VISIBLE
        topic_no_internet_tag?.visibility = View.GONE
        val stringRequest = object : StringRequest(Request.Method.POST, UrlHolder.URL_GET_SUBJECT_VIDEO_TOPICS,
            Response.Listener<String> { sub ->
                topicLoadingProgressbar?.visibility = View.GONE

                try {
                    val obj = JSONObject(sub)
                    if (!obj.getBoolean("error")) {
                        val noOfSub = obj.getJSONArray("subject_topicz_array")

                        if ((noOfSub.length()!=0)){

                            val qDataArray = mutableListOf<TopicList>()
                            for (i in 0 until noOfSub.length()) {
                                val objectSubject = noOfSub.getJSONObject(i)
                                qDataArray.add(TopicList(
                                    objectSubject.getString("topic_id"),
                                    objectSubject.getString("topic_name"),
                                    objectSubject.getString("topic_subject_id"),
                                    objectSubject.getString("topic_logo"),
                                    objectSubject.getString("arrangement_order")
                                ))
                            }
                            TOPICS_ADAPTER.addItems(qDataArray)

                        }else{
                            ClassAlertDialog(thisContext).toast("No Topic Found...")
                        }
                    } else {
                        ClassAlertDialog(thisContext).toast("An error occurred, try again...")
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { _ ->
                topicLoadingProgressbar?.visibility = View.GONE
                topic_no_internet_tag?.visibility = View.VISIBLE
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String?> {
                val params = HashMap<String, String?>()
                params["request_type"] = "get_subject_topics"
                params["subject_id"] = prefs.getCurrentSubjectId()
                return params
            }
        }
        VolleySingleton.instance?.addToRequestQueue(stringRequest)//adding request to queue
        //volley interactions end

    }




    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentVideoWatch.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentVideoWatch().apply {
                arguments = Bundle().apply {
                    putString(VIDEO, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}