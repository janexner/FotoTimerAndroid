package com.exner.tools.fototimer.ui.destinations

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.exner.tools.fototimer.BuildConfig
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph

@Destination<RootGraph>
@Composable
fun About() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .consumeWindowInsets(PaddingValues(8.dp))
            .padding(8.dp)
            .imePadding()
    ) {
        Text(
            text = "About Foto Timer",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(8.dp)
        )
        val localContext = LocalContext.current
        Spacer(modifier = Modifier.width(8.dp))
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
                    AboutVersionAndButton(localContext)
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
                    AboutVersionAndButton(localContext)
                    Spacer(modifier = Modifier.height(8.dp))
                    AboutText()
                }
            }
        }
    }
}

@Composable
private fun AboutVersionAndButton(localContext: Context) {
    Column {
        Text(
            text = "Foto Timer ${BuildConfig.VERSION_NAME}",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                val webpage: Uri =
                    Uri.parse("https://jan-exner.de/software/android/fototimer/")
                val intent = Intent(Intent.ACTION_VIEW, webpage)
                localContext.startActivity(intent)
            },
        ) {
            Text(text = "Visit the Foto Timer web site")
        }
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                val webpage: Uri =
                    Uri.parse("https://jan-exner.de/software/android/fototimer/manual/")
                val intent = Intent(Intent.ACTION_VIEW, webpage)
                localContext.startActivity(intent)
            },
        ) {
            Text(text = "Peruse the Foto Timer manual")
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
            text = "Foto Timer is a flexible timer application that can be used for timed tasks, simple or complex.",
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
