package com.lilinth.questionsolver.ui.component

import android.webkit.WebView
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

/**
 * Markdown + LaTeX 渲染器组件
 * 使用 WebView + Marked.js + KaTeX 实现混合渲染
 *
 * @param content Markdown 内容（支持 LaTeX 公式：行内 $...$ 和块级 $$...$$）
 * @param modifier Compose Modifier
 */
@Composable
fun MarkdownRenderer(
    content: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()

    val webView = remember {
        WebView(context).apply {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
            }
            loadUrl("file:///android_asset/markdown_template.html")
        }
    }

    // 监听主题变化
    LaunchedEffect(isDarkTheme) {
        webView.evaluateJavascript("setTheme($isDarkTheme);", null)
    }

    // 监听内容变化
    LaunchedEffect(content) {
        if (content.isNotEmpty()) {
            val escapedContent = content
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "")
                .replace("'", "\\'")

            webView.evaluateJavascript("updateContent(\"$escapedContent\");", null)
        }
    }

    AndroidView(
        factory = { webView },
        modifier = modifier
    )

    DisposableEffect(Unit) {
        onDispose {
            webView.destroy()
        }
    }
}
