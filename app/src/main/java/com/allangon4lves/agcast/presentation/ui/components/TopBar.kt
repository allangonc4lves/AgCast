package com.allangon4lves.agcast.presentation.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.allangon4lves.agcast.presentation.viewmodels.BrowserScreenViewModel

@SuppressLint("Range")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    url: String,
    onUrlChange: (String) -> Unit,
    onBack: () -> Unit,
    onReload: () -> Unit,
    browserScreenViewModel: BrowserScreenViewModel = viewModel()
) {
    var siteUrl by remember { mutableStateOf(url) }
    var isEditing by remember { mutableStateOf(false) }

    // Sincroniza sempre que o parâmetro url mudar
    LaunchedEffect(url) {
        siteUrl = url
    }

    TopAppBar(
        title = {
            TextField(
                value = siteUrl,
                onValueChange = { siteUrl = it },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp)
                    .onFocusChanged { focusState ->
                        isEditing = focusState.isFocused
                    },
                textStyle = TextStyle(
                    fontSize = 14.sp,
                ),
                shape = RoundedCornerShape(50.dp),
                placeholder = { Text("") },
                leadingIcon = {
                    val favicon = browserScreenViewModel.favicon
                    if (favicon != null) {
                        Image(
                            bitmap = favicon.asImageBitmap(),
                            contentDescription = "Favicon",
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Icon(Icons.Default.Public, contentDescription = "Site")
                    }
                },
                trailingIcon = {
                    if (isEditing && siteUrl.isNotEmpty()) {
                        // Quando está editando → botão de limpar
                        IconButton(onClick = { siteUrl = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Limpar")
                        }
                    } else {
                        // Quando não está editando → botão de reload
                        IconButton(onClick = {
                            onReload() // recarrega a página atual
                        }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Recarregar")
                        }
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Go),
                keyboardActions = KeyboardActions(
                    onGo = {
                        var finalUrl = siteUrl
                        if (!finalUrl.startsWith("http")) {
                            finalUrl = "https://$finalUrl"
                        }
                        onUrlChange(finalUrl)
                    }
                )
            )
        },
        actions = {
            CastButton()
        }
    )
}