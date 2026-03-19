package com.allangon4lves.agcast.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

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
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(30.dp)),
                shape = RoundedCornerShape(30.dp),
                placeholder = { Text("Digite o site...") },
                leadingIcon = {
                    Icon(Icons.Default.Public, contentDescription = "Site")
                },
                trailingIcon = {
                    if (siteUrl.isNotEmpty()) {
                        IconButton(onClick = { siteUrl = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Limpar")
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
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Voltar"
                )
            }
        },
        actions = {
            IconButton(onClick = {
                var finalUrl = siteUrl
                if (!finalUrl.startsWith("http")) {
                    finalUrl = "https://$finalUrl"
                }
                onUrlChange(finalUrl)
            }) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Ir"
                )
            }
            CastButton()
        }
    )
}
/*
(
onClick = { showSheet = true },
modifier = Modifier
.fillMaxWidth()
.padding(horizontal = 24.dp, vertical = 12.dp)
.height(56.dp), // altura fixa
colors = ButtonDefaults.buttonColors(
containerColor = Color(0xFF6200EE), // cor de fundo
contentColor = Color.White          // cor do texto/ícone
),
shape = RoundedCornerShape(12.dp), // cantos arredondados
elevation = ButtonDefaults.buttonElevation(
defaultElevation = 6.dp,
pressedElevation = 10.dp
)
)*/