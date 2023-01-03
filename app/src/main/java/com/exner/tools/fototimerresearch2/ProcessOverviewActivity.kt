package com.exner.tools.fototimerresearch2

import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.exner.tools.fototimerresearch2.data.model.FotoTimerProcessViewModel
import com.exner.tools.fototimerresearch2.data.model.FotoTimerProcessViewModelFactory

class ProcessOverviewActivity : AppCompatActivity() {

    private var processID: Long = 0

    private val fotoTimerProcessViewModel: FotoTimerProcessViewModel by viewModels {
        FotoTimerProcessViewModelFactory((application as FotoTimerApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_process_overview)

        actionBar?.setTitle(R.string.title_activity_process_overview)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        // find which process whe should show
        val arguments =
            requireNotNull(intent?.extras) { "The ProcessOverviewActivity must be called with a process ID so it knows which process to show!" }
        with(arguments) {
            processID = getLong("PROCESS_ID")
        }
        // retrieve said process
        val process =
            requireNotNull(fotoTimerProcessViewModel.getProcessById(processID)) { "The process ID passed is not valid" }

        val nameView = findViewById<TextView>(R.id.overviewProcessNameView)
        nameView.text = process.name
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
