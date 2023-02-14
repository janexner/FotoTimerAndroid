package com.exner.tools.fototimerresearch2.ui

import android.content.res.Configuration
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.exner.tools.fototimerresearch2.BuildConfig
import com.exner.tools.fototimerresearch2.ui.theme.FotoTimerTheme
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun FotoTimerAbout() {
    // what's the orientation, right now?
    val configuration = LocalConfiguration.current
    when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            // show horizontally
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) { AboutScreenElements() }
        }
        else -> {
            // show
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) { AboutScreenElements() }
        }
    }
}

@Composable
fun AboutScreenElements() {
    Column {
        HeaderText(text = "About Foto Timer")
        Text(text = "Foto Timer ${BuildConfig.VERSION_NAME}")
        Text(text = "Foto Timer is a flexible timer for all sorts of repeating or timed tasks. It shows time left, and can sound audible alarms at different points.")
    }
    Spacer(modifier = Modifier.height(8.dp))
    val url = "https://www.jan-exner.de/software/fototimer/about.html"
    AndroidView(factory = {context ->
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            webViewClient = WebViewClient()
            loadUrl(url)
        }
    }, update = {
        it.loadUrl(url)
    })
}

@Preview(
    showSystemUi = true,
    device = Devices.PHONE
)
@Preview(
    showSystemUi = true,
    device = Devices.NEXUS_5
)
@Preview(
    showSystemUi = true,
    device = Devices.TABLET
)
@Composable
fun FTAPreview() {
    FotoTimerTheme {
        FotoTimerAbout()
    }
}