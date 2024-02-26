package com.exner.tools.fototimer.ui.destinations

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.exner.tools.fototimer.BuildConfig
import com.exner.tools.fototimer.ui.theme.FotoTimerTheme
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun About() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Text(
            text = "About Foto Timer",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(8.dp)
        )
        // what's the orientation, right now?
        val configuration = LocalConfiguration.current
        when (configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                // show horizontally
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Foto Timer ${BuildConfig.VERSION_NAME}",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    AboutText()
                }
            }

            else -> {
                // show
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Foto Timer ${BuildConfig.VERSION_NAME}",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    AboutText()
                }
            }
        }
    }
}

@Composable
fun AboutText() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Foto Timer is a flexible timer application that can be used for timed tasks, simple or complex.\n",
            modifier = Modifier.padding(8.dp)
        )
        Text(
            text = "In simple terms, Foto Timer counts and beeps.",
            modifier = Modifier.padding(8.dp)
        )
        Text(
            text = "You can check out the manual if you want to see how Foto Timer works and what it can do for you.",
            modifier = Modifier.padding(8.dp)
        )
        Text(
            text = "Use cases:",
            modifier = Modifier.padding(8.dp)
        )
        Text(
            text = "HIIT training - see How to do Tabata with Foto Timer",
            modifier = Modifier.padding(32.dp, 4.dp)
        )
        Text(
            text = "Meditation (although you may want to use Meditation Timer for that)",
            modifier = Modifier.padding(32.dp, 4.dp)
        )
        Text(
            text = "Developing film",
            modifier = Modifier.padding(32.dp, 4.dp)
        )
        Text(
            text = "Taking long-exposure photos of the sky (that's what I originally built it for)",
            modifier = Modifier.padding(32.dp, 4.dp)
        )
        Text(
            text = "Foto Timer started as an app for Palm OS in the 90s. I have now finally been able to re-write it for Android.",
            modifier = Modifier.padding(8.dp)
        )
        Text(
            text = "It runs on Android phones and tablets running Android 10 or later. I aim to support the latest 3 versions of Android.",
            modifier = Modifier.padding(8.dp)
        )
    }
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
        About()
    }
}