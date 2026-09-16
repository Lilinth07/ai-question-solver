package com.lilinth.questionsolver.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    navController: NavController,
    isConfigured: Boolean,
    onImageSelected: (Uri) -> Unit
) {
    var showConfigDialog by remember { mutableStateOf(!isConfigured) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            onImageSelected(it)
            navController.navigate("answer")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI 搜题") },
                actions = {
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(Icons.Default.Settings, contentDescription = "设置")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!isConfigured) {
                Text(
                    text = "请先配置 API",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Button(
                onClick = { imagePickerLauncher.launch("image/*") },
                enabled = isConfigured,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("选择图片")
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { /* TODO: 拍照功能 */ },
                enabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("拍照（开发中）")
            }
        }
    }

    if (showConfigDialog && !isConfigured) {
        AlertDialog(
            onDismissRequest = { showConfigDialog = false },
            title = { Text("需要配置") },
            text = { Text("请先在设置中配置 API 信息") },
            confirmButton = {
                TextButton(onClick = {
                    showConfigDialog = false
                    navController.navigate("settings")
                }) {
                    Text("去设置")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfigDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}
