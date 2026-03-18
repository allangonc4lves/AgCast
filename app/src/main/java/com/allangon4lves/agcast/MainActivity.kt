package com.allangon4lves.agcast

import androidx.compose.material3.*
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.mediarouter.app.MediaRouteButton
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.gms.cast.MediaInfo
import com.google.android.gms.cast.MediaLoadRequestData
import com.google.android.gms.cast.framework.CastButtonFactory
import com.google.android.gms.cast.framework.CastContext

class MainActivity : AppCompatActivity() {

    private lateinit var castContext: CastContext

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        castContext = CastContext.getSharedInstance(this)

        setContent {
            VideoScreen()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TopBar() {

        TopAppBar(
            title = {
                Text("AgCast")
            },
            actions = {
                CastButton()
            }
        )
    }

    @Composable
    fun VideoScreen() {

        val context = LocalContext.current

        val exoPlayer = remember {
            ExoPlayer.Builder(context).build().apply {
                val mediaItem = MediaItem.fromUri(
                    "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
                )
                setMediaItem(mediaItem)
                prepare()
                playWhenReady = true
            }
        }

        Scaffold(
            topBar = {
                TopBar()
            }
        ) { padding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {

                // 🎬 Player
                AndroidView(
                    factory = {
                        PlayerView(it).apply {
                            player = exoPlayer
                        }
                    },
                    modifier = Modifier.weight(1f)
                )

                // 🔘 Botão opcional (pode remover depois)
                Button(
                    onClick = {
                        castVideo(
                            context,
                            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Transmitir para TV")
                }
            }
        }
    }

    @Composable
    fun CastButton() {

        val context = LocalContext.current

        AndroidView(
            factory = {

                MediaRouteButton(it).apply {
                    CastButtonFactory.setUpMediaRouteButton(
                        context,
                        this
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }


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

}