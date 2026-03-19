package com.allangon4lves.agcast.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.allangon4lves.agcast.data.VideoData

class BrowserScreenViewModel : ViewModel() {
    var url by mutableStateOf("file:///android_asset/index.html")
        private set

    var videoList by mutableStateOf<List<VideoData>>(emptyList())
        private set

    var showSheet by mutableStateOf(false)
        private set

    fun updateUrl(newUrl: String) {
        url = newUrl
        videoList = emptyList()
    }

    fun addVideo(video: VideoData) {
        if (videoList.none { it.url == video.url }) {
            videoList = videoList + video
            showSheet = true
        }
    }

    fun toggleSheet(show: Boolean) {
        showSheet = show
    }
}