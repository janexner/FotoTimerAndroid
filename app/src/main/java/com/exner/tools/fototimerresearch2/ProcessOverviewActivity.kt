package com.exner.tools.fototimerresearch2

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.exner.tools.fototimerresearch2.data.model.FotoTimerProcessViewModel
import com.exner.tools.fototimerresearch2.data.model.FotoTimerProcessViewModelFactory

class ProcessOverviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_process_overview)

        actionBar?.setDisplayHomeAsUpEnabled(true)

        // find which process whe should show
        val arguments =
            requireNotNull(intent?.extras) { "The ProcessOverviewActivity must be called with a process ID so it knows which process to show!" }
        var processID: Long
        with(arguments) {
            processID = getLong("PROCESS_ID")
        }

        // initialise the Activity
        populateUiFromProcessId(processID)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.process_overview_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                super.onBackPressed()
                return true
            }
            R.id.menu_item_edit_process -> {
                // TODO
                return true
            }
            R.id.menu_item_start_process -> {
                // TODO
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun populateUiFromProcessId(processID: Long) {
        // get a handle on the DB
        val fotoTimerProcessViewModel: FotoTimerProcessViewModel by viewModels {
            FotoTimerProcessViewModelFactory((application as FotoTimerApplication).repository)
        }

        // retrieve said process
        val process =
            requireNotNull(fotoTimerProcessViewModel.getProcessById(processID)) { "The process ID passed is not valid" }

        // make the UI
        val nameView = findViewById<TextView>(R.id.overviewProcessNameView)
        nameView.text = process.name

        val processTimeView = findViewById<TextView>(R.id.overviewProcessTimeView)
        processTimeView.text = process.processTime.toString()
    }
}
