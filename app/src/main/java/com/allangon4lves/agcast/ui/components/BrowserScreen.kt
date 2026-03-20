package com.allangon4lves.agcast.ui.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import androidx.lifecycle.viewmodel.compose.viewModel
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.allangon4lves.agcast.data.VideoInfo
import com.allangon4lves.agcast.cast.castVideo
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import com.allangon4lves.agcast.viewmodels.BrowserScreenViewModel

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowserScreen(browserScreenViewModel: BrowserScreenViewModel = viewModel()) {
    val context = LocalContext.current
    val webView = remember { WebView(context) }

    val sheetState = rememberModalBottomSheetState()

    // Estados vindos da ViewModel
    val url = browserScreenViewModel.url
    val videoList = browserScreenViewModel.videoList
    val showSheet = browserScreenViewModel.showSheet

    BackHandler(enabled = webView.canGoBack()) {
        webView.goBack()
        webView.url?.let { currentUrl ->
            browserScreenViewModel.updateUrl(currentUrl)
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                url = url,
                onUrlChange = { newUrl -> browserScreenViewModel.updateUrl(newUrl) },
                onBack = {
                    if (webView.canGoBack()) {
                        webView.goBack()
                        webView.url?.let { currentUrl ->
                            browserScreenViewModel.updateUrl(currentUrl)
                        }
                    }
                },
                onReload = { webView.reload() }
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
                                val type = when {
                                    url.contains(".m3u8") -> "HLS"
                                    url.contains(".mp4") -> "MP4"
                                    url.contains(".webm") -> "WEBM"
                                    else -> "VIDEO"
                                }
                                val title = url.substringAfterLast("/").take(30)
                                browserScreenViewModel.addVideo(VideoInfo(url, title, type))
                            }
                        }, "Android")

                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true

                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                browserScreenViewModel.updateUrl(url ?: "")

                                // 🔥 Injeção de JavaScript para detectar <video> e <source>
                                val js = """
                                    (function() {
                                        function sendVideo(src) {
                                            if (src && src.startsWith("http")) {
                                                Android.onVideoDetected(src);
                                            }
                                        }
                                        var videos = document.querySelectorAll("video");
                                        videos.forEach(v => {
                                            if (v.src) sendVideo(v.src);
                                            v.addEventListener('play', function() {
                                                sendVideo(v.currentSrc);
                                            });
                                        });
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
                                val reqUrl = request?.url.toString()
                                if (reqUrl.contains(".mp4") || reqUrl.contains(".m3u8") || reqUrl.contains(".webm")) {
                                    val type = when {
                                        reqUrl.contains(".m3u8") -> "HLS"
                                        reqUrl.contains(".mp4") -> "MP4"
                                        reqUrl.contains(".webm") -> "WEBM"
                                        else -> "VIDEO"
                                    }
                                    val title = reqUrl.substringAfterLast("/").take(30)
                                    browserScreenViewModel.addVideo(VideoInfo(reqUrl, title, type))
                                }
                                return super.shouldInterceptRequest(view, request)
                            }
                        }

                        webChromeClient = object : WebChromeClient() {
                            override fun onReceivedIcon(view: WebView?, icon: Bitmap?) {
                                super.onReceivedIcon(view, icon)
                                browserScreenViewModel.updateFavicon(icon)
                            }
                        }

                        loadUrl(url)
                    }
                },
                update = { it.loadUrl(url) },
                modifier = Modifier.weight(1f)
            )

            if (videoList.isNotEmpty()) {
                Button(
                    onClick = { browserScreenViewModel.toggleSheet(true) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .height(56.dp)
                ) {
                    Icon(Icons.Filled.Movie, contentDescription = "Vídeos")
                    Spacer(Modifier.width(8.dp))
                    Text("Vídeos encontrados (${videoList.size})")
                }
            }
        }
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { browserScreenViewModel.toggleSheet(false) },
            sheetState = sheetState
        ) {
            Text("Selecione um video para iniciar a transmissão", Modifier.padding(16.dp))
            videoList.forEach { video ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    onClick = {
                        castVideo(context, video.url)
                        browserScreenViewModel.toggleSheet(false)
                    }
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(video.title)
                        Text(video.type)
                        Text(video.url.take(60))
                    }
                }
            }
        }
    }
}