package com.allangon4lves.agcast.presentation.viewmodels

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.allangon4lves.agcast.domain.model.VideoInfo

class BrowserScreenViewModel : ViewModel() {
    var favicon by mutableStateOf<Bitmap?>(null)
        private set

    var url by mutableStateOf("file:///android_asset/index.html")

    var videoList by mutableStateOf<List<VideoInfo>>(emptyList())
        private set

    var showSheet by mutableStateOf(false)
        private set

    fun updateFavicon(icon: Bitmap?){
        favicon = icon
    }

    fun updateUrl(newUrl: String) {
        url = newUrl
        videoList = emptyList()
    }

    fun addVideo(video: VideoInfo) {
        if (videoList.none { it.url == video.url }) {
            videoList = videoList + video
            showSheet = true
        }
    }

    fun toggleSheet(show: Boolean) {
        showSheet = show
    }
}