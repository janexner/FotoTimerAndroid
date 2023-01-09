package com.exner.tools.fototimerresearch2.ui.process

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.exner.tools.fototimerresearch2.FotoTimerApplication
import com.exner.tools.fototimerresearch2.NewEditProcessActivity
import com.exner.tools.fototimerresearch2.R
import com.exner.tools.fototimerresearch2.data.model.FotoTimerProcessViewModel
import com.exner.tools.fototimerresearch2.data.model.FotoTimerProcessViewModelFactory

class ProcessOverviewActivity : AppCompatActivity() {

    private var processID: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_process_overview)

        actionBar?.setDisplayHomeAsUpEnabled(true)

        // find which process whe should show
        val arguments =
            requireNotNull(intent?.extras) { "The ProcessOverviewActivity must be called with a process ID so it knows which process to show!" }
        with(arguments) {
            processID = getLong("PROCESS_ID")
        }

        // initialise the Activity
        populateUiFromProcessId(processID)

        // the "start" button needs a listener!
        val startButton = findViewById<Button>(R.id.overviewProcessStart)
        startButton.setOnClickListener {
            startProcessById(processID)
        }
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
                val intent = Intent(this, NewEditProcessActivity::class.java)
                intent.putExtra("PROCESS_ID", processID)
                startActivity(intent)
                return true
            }
            R.id.menu_item_start_process -> {
                startProcessById(processID)
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
        val intervalTimeView = findViewById<TextView>(R.id.overviewIntervalTimeView)
        intervalTimeView.text = process.intervalTime.toString()
    }

    private fun startProcessById(processID: Long) {
        Log.i("jexner", "Starting process $processID...")
        // TODO
    }
}
