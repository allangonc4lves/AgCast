package com.allangon4lves.agcast

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.mediarouter.app.MediaRouteButton
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
            BrowserScreen()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TopBar(
        url: String,
        onUrlChange: (String) -> Unit,
        onBack: () -> Unit
    ) {

        var text by remember { mutableStateOf(url) }

        TopAppBar(
            title = {
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Digite o site...") }
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Text("←")
                }
            },
            actions = {

                Button(onClick = {
                    var finalUrl = text
                    if (!finalUrl.startsWith("http")) {
                        finalUrl = "https://$finalUrl"
                    }
                    onUrlChange(finalUrl)
                }) {
                    Text("Ir")
                }

                CastButton()
            }
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun BrowserScreen() {

        val sheetState = rememberModalBottomSheetState()
        var showSheet by remember { mutableStateOf(false) }

        var url by remember { mutableStateOf("https://www.google.com") }
        var videoList by remember { mutableStateOf<List<VideoItem>>(emptyList()) }

        val context = LocalContext.current

        val webView = remember {
            WebView(context)
        }

        Scaffold(
            topBar = {
                TopBar(
                    url = url,
                    onUrlChange = { newUrl ->
                        url = newUrl
                        videoList = emptyList()
                    },
                    onBack = {
                        if (webView.canGoBack()) {
                            webView.goBack()
                        }
                    }
                )
            }
        ) { padding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {

                AndroidView(
                    factory = {
                        webView.apply {

                            addJavascriptInterface(object {

                                @android.webkit.JavascriptInterface
                                fun onVideoDetected(url: String) {

                                    if (videoList.none { it.url == url }) {

                                        val type = when {
                                            url.contains(".m3u8") -> "HLS"
                                            url.contains(".mp4") -> "MP4"
                                            else -> "VIDEO"
                                        }

                                        val title = url.substringAfterLast("/").take(30)

                                        videoList = videoList + VideoItem(url, title, type)

                                        Log.d("JS_VIDEO", url)

                                        showSheet = true
                                    }
                                }

                            }, "Android")

                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true

                            webViewClient = object : WebViewClient() {

                                override fun onPageFinished(view: WebView?, url: String?) {
                                    super.onPageFinished(view, url)

                                    val js = """
        (function() {

            function sendVideo(src) {
                if (src && src.startsWith("http")) {
                    Android.onVideoDetected(src);
                }
            }

            // 🎬 pega <video>
            var videos = document.querySelectorAll("video");
            videos.forEach(v => {
                if (v.src) sendVideo(v.src);

                v.addEventListener('play', function() {
                    sendVideo(v.currentSrc);
                });
            });

            // 🎥 intercepta source
            var sources = document.querySelectorAll("source");
            sources.forEach(s => {
                if (s.src) sendVideo(s.src);
            });

        })();
    """.trimIndent()

                                    view?.evaluateJavascript(js, null)
                                }

                                override fun shouldInterceptRequest(
                                    view: WebView?,
                                    request: WebResourceRequest?
                                ): android.webkit.WebResourceResponse? {

                                    val url = request?.url.toString()

                                    if (
                                        url.contains(".mp4") ||
                                        url.contains(".m3u8") ||
                                        url.contains(".webm")
                                    ) {

                                        if (videoList.none { it.url == url }) {

                                            val type = when {
                                                url.contains(".m3u8") -> "HLS"
                                                url.contains(".mp4") -> "MP4"
                                                url.contains(".webm") -> "WEBM"
                                                else -> "VIDEO"
                                            }

                                            val title = url.substringAfterLast("/").take(30)

                                            videoList = videoList + VideoItem(url, title, type)

                                            Log.d("VIDEO_DETECTADO", url)

                                            showSheet = true // 🔥 abre automático
                                        }
                                    }

                                    return super.shouldInterceptRequest(view, request)
                                }

                                override fun shouldOverrideUrlLoading(
                                    view: WebView?,
                                    request: WebResourceRequest?
                                ): Boolean {
                                    view?.loadUrl(request?.url.toString())
                                    return true
                                }
                            }

                            loadUrl(url)
                        }
                    },
                    update = {
                        it.loadUrl(url)
                    },
                    modifier = Modifier.weight(1f)
                )

                if (videoList.isNotEmpty()) {
                    Button(
                        onClick = { showSheet = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("🎬 Vídeos encontrados (${videoList.size})")
                    }
                }
            }
        }

        // 🎬 BottomSheet PRO
        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                sheetState = sheetState
            ) {

                Text(
                    "Vídeos detectados",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )

                videoList.forEach { video ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        onClick = {
                            castVideo(context, video.url)
                            showSheet = false
                        }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {

                            Text(
                                text = video.title,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = video.type,
                                style = MaterialTheme.typography.labelSmall
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = video.url.take(60),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
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
                    CastButtonFactory.setUpMediaRouteButton(context, this)
                }
            },
            modifier = Modifier.padding(end = 8.dp)
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