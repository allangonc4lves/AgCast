package com.allangon4lves.agcast.ui.theme.components

import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.allangon4lves.agcast.data.VideoItem
import com.allangon4lves.agcast.cast.castVideo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowserScreen() {
    val context = LocalContext.current

    val webView = remember {
        WebView(context)
    }

    val sheetState = rememberModalBottomSheetState()
    var showSheet by remember { mutableStateOf(false) }

    var url by remember { mutableStateOf("file:///android_asset/index.html") }
    var videoList by remember { mutableStateOf<List<VideoItem>>(emptyList()) }

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

                            @JavascriptInterface
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

                        webView.settings.loadWithOverviewMode = true
                        webView.settings.useWideViewPort = true
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
                            ): WebResourceResponse? {

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