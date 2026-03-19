package com.allangon4lves.agcast.ui.theme.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.mediarouter.app.MediaRouteButton
import com.google.android.gms.cast.framework.CastButtonFactory

@Composable
fun CastButton() {
    val context = LocalContext.current
    AndroidView(
        factory = {
            MediaRouteButton(it).apply {
                CastButtonFactory.setUpMediaRouteButton(context, this)
            }
        },
        modifier = Modifier.padding(end = 4.dp)
    )
}