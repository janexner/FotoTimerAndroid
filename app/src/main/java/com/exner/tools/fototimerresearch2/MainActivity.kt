package com.exner.tools.fototimerresearch2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.exner.tools.fototimerresearch2.data.model.FotoTimerProcessViewModel
import com.exner.tools.fototimerresearch2.data.model.FotoTimerProcessViewModelFactory
import com.exner.tools.fototimerresearch2.data.persistence.FotoTimerProcess
import com.exner.tools.fototimerresearch2.settings.SettingsActivity
import com.exner.tools.fototimerresearch2.ui.models.FotoTimerProcessListAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.serialization.json.Json

class MainActivity : AppCompatActivity() {

    private val fotoTimerProcessViewModel: FotoTimerProcessViewModel by viewModels {
        FotoTimerProcessViewModelFactory((application as FotoTimerApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = FotoTimerProcessListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)


        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@MainActivity, NewEditProcessActivity::class.java)
            newProcessResultLauncher.launch(intent)
        }

        fotoTimerProcessViewModel.allProcesses.observe(this, Observer { process ->
            // Update the cached copy of the words in the adapter.
            process?.let { adapter.submitList(it) }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {

        // add process
        R.id.menu_item_add_process -> {
            val intent = Intent(this@MainActivity, NewEditProcessActivity::class.java)
            newProcessResultLauncher.launch(intent)
            true
        }

        // go to settings
        R.id.menu_item_settings -> {
            val intent = Intent(this, SettingsActivity::class.java)
            this.startActivity(intent)
            true
        }

        // nope, don't know this one
        else -> super.onOptionsItemSelected(item)
    }

    private var newProcessResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.getStringExtra(NewEditProcessActivity.EXTRA_REPLY)?.let {
                    val fotoTimerProcess = Json.decodeFromString(FotoTimerProcess.serializer(), it)
                    fotoTimerProcessViewModel.insert(fotoTimerProcess)
                    Toast.makeText(applicationContext, R.string.process_saved, Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    R.string.empty_not_saved,
                    Toast.LENGTH_LONG
                ).show()
            }
        }

}
