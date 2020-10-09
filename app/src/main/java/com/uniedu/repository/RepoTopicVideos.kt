package com.uniedu.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.uniedu.model.*
import com.uniedu.network.RetrofitConstantGET
import com.uniedu.room.DatabaseRoom
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.http.GET
import retrofit2.http.Query

class RepoTopicVideos(private val database: DatabaseRoom) {

    val topicVideos : LiveData<List<TopicVideos>> = database.topicsDao().getAll()
    val videos : LiveData<List<Videos>> = database.videosDao().getAll()


    val feedBack:LiveData<String> get() = _feedBack
    private val _feedBack  = MutableLiveData<String>().apply {
        value = "success"
    }

    suspend fun getTopicVideos(myDetails: MyDetails){
        val topic = RetrofitConstantGET.retrofit
            .create(TopicVideoService::class.java)
            .getTopicVideosAsync(myDetails.user_school)
        val video = RetrofitConstantGET.retrofit
            .create(TopicVideoService::class.java)
            .getVideosAsync(myDetails.user_school)

        withContext(Dispatchers.IO) {
            try {
                val listResult_Topic = topic.await()
                val listResult_Video = video.await()
                database.topicsDao().deleteNotMySchools(myDetails.user_school.toInt())
                database.topicsDao().upSert(listResult_Topic)
                database.videosDao().upSert(listResult_Video)

                if (listResult_Topic.isEmpty()) _feedBack.postValue("empty")
            } catch (e: Exception) {
                e.printStackTrace()
                _feedBack.postValue("network_error")
            }
        }
    }
}

interface TopicVideoService {

    @GET("get_video_topics.php")
    fun getTopicVideosAsync(
        @Query("school_id") school_id: String
    ): Deferred<List<TopicVideos>>

    @GET("get_videos.php")
    fun getVideosAsync(
        @Query("school_id") school_id: String
    ): Deferred<List<Videos>>
}
