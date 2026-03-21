package com.allangon4lves.agcast.data.cast

import android.content.Context
import android.util.Log
import com.google.android.gms.cast.MediaInfo
import com.google.android.gms.cast.MediaLoadRequestData
import com.google.android.gms.cast.framework.CastContext

fun castVideo(context: Context, videoUrl: String) {

    val castSession = CastContext.getSharedInstance(context)
        .sessionManager.currentCastSession

    if (castSession == null) {
        Log.e("CAST", "Nenhum dispositivo conectado!")
        return
    }

    val remoteMediaClient = castSession.remoteMediaClient

    if (remoteMediaClient == null) {
        Log.e("CAST", "RemoteMediaClient é null")
        return
    }

    val mediaInfo = MediaInfo.Builder(videoUrl)
        .setContentType("video/mp4")
        .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
        .build()

    val request = MediaLoadRequestData.Builder()
        .setMediaInfo(mediaInfo)
        .build()

    remoteMediaClient.load(request)

    Log.d("CAST", "Enviando vídeo para TV")
}