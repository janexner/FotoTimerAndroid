package com.exner.tools.fototimerresearch2.ui.process

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.exner.tools.fototimerresearch2.R
import com.exner.tools.fototimerresearch2.data.persistence.FotoTimerProcess

@Composable
fun ProcessDetailDescription(process: FotoTimerProcess) {
    Surface {
        Column(
            Modifier
                .padding(dimensionResource(id = R.dimen.normal_padding))
                .fillMaxSize()
        ) {
            ProcessName(process.name)
            ProcessLeadIn(hasLeadIn = process.hasLeadIn, leadInSeconds = process.leadInSeconds)
            ProcessTimes(process.processTime, process.intervalTime)
            Spacer(modifier = Modifier.weight(1f))
            ElevatedButton(onClick = { /*TODO*/ }, Modifier.fillMaxWidth()) {
                Text(
                    text = "Start Process",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(24.dp)
                )
            }
        }
    }
}

@Composable
private fun ProcessName(name: String) {
    Text(
        text = name,
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(R.dimen.margin_small))
            .wrapContentWidth(Alignment.CenterHorizontally)
    )
}

@Composable
private fun ProcessLeadIn(hasLeadIn: Boolean, leadInSeconds: Int?) {
    if (hasLeadIn && null != leadInSeconds) {
        Text(
            text = "Process has a $leadInSeconds second lead-in.",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun ProcessTimes(processTime: Long, intervalTime: Long) {
    Text(
        text = "Total Process time is $processTime seconds, with intervals evey $intervalTime seconds.",
        style = MaterialTheme.typography.bodyLarge
    )
}

@Preview
@Composable
private fun PDDPreview() {
    MaterialTheme {
        ProcessDetailDescription(
            FotoTimerProcess(
                "Test Process",
                30,
                10,
                false,
                0,
                false,
                0,
                false,
                0,
                false,
                true,
                5,
                false,
                false,
                0,
                0,
                0
            )
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PDDNPreview() {
    MaterialTheme {
        ProcessDetailDescription(
            FotoTimerProcess(
                "Test Process",
                30,
                10,
                false,
                0,
                false,
                0,
                false,
                0,
                false,
                false,
                0,
                false,
                false,
                0,
                0,
                0
            )
        )
    }
}
