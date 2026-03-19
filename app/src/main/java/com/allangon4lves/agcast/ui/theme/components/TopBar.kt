package com.allangon4lves.agcast.ui.theme.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.allangon4lves.agcast.cast.CastButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    url: String,
    onUrlChange: (String) -> Unit,
    onBack: () -> Unit
) {

    var siteUrl by remember { mutableStateOf(url) }

    TopAppBar(
        title = {
            TextField(
                value = siteUrl,
                onValueChange = { siteUrl = it },
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
                var finalUrl = siteUrl
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