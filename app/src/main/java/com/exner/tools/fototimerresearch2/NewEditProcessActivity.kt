package com.exner.tools.fototimerresearch2

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.view.get
import androidx.preference.PreferenceManager
import com.exner.tools.fototimerresearch2.data.model.FotoTimerProcessViewModel
import com.exner.tools.fototimerresearch2.data.model.FotoTimerProcessViewModelFactory
import com.exner.tools.fototimerresearch2.data.persistence.FotoTimerProcess
import com.exner.tools.fototimerresearch2.databinding.ActivityNewProcessBinding
import com.exner.tools.fototimerresearch2.sound.SoundStuff
import com.exner.tools.fototimerresearch2.ui.models.FotoTimerProcessArrayAdapter
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.serialization.json.Json

class NewEditProcessActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewProcessBinding

    private val fotoTimerProcessViewModel: FotoTimerProcessViewModel by viewModels {
        FotoTimerProcessViewModelFactory((application as FotoTimerApplication).repository)
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewProcessBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // setup the Spinner for goto ID
        val allProcesses = FotoTimerProcessArrayAdapter(this, R.layout.spinner_item)
        fotoTimerProcessViewModel.allProcesses.observe(this) { processes ->
            processes.forEach {
                allProcesses.add(it)
            }
        }
        allProcesses.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.spinnerGotoId.adapter = allProcesses

        // get some defaults from the app's settings
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this /* Activity context */)
        val processTimeDefault = sharedPreferences.getString("preference_process_time", "30")?.toLongOrNull() ?: 30
        val intervalTimeDefault = sharedPreferences.getString("preference_interval_time", "10")?.toLongOrNull() ?: 10
        val leadInTimeDefault = sharedPreferences.getString("preference_lead_in_time", "0")?.toIntOrNull() ?: 0
        val pauseTimeDefault = sharedPreferences.getString("preference_pause_time", "5")?.toIntOrNull() ?: 5

        // set defaults in the UI (they may be overwritten later if this is edit)
        binding.editProcessTime.setText(sharedPreferences.getString("preference_process_time", "30"))
        binding.editIntervalTime.setText(sharedPreferences.getString("preference_interval_time", "10"))
        binding.switchSoundStart.isChecked = ("none" != sharedPreferences.getString("preference_process_start_default_sound", "none"))
        binding.switchSoundEnd.isChecked = ("none" != sharedPreferences.getString("preference_process_end_default_sound", "none"))
        binding.switchSoundInterval.isChecked = ("none" != sharedPreferences.getString("preference_interval_default_sound", "none"))
        binding.switchSoundMetronome.isChecked = ("none" != sharedPreferences.getString("preference_metronome_sound", "none"))
        binding.editLeadInTime.setText(sharedPreferences.getString("preference_lead_in_time", "0"))
        binding.editGotoPauseTime.setText(sharedPreferences.getString("preference_pause_time", "5"))

        // new or edit?
        val processId = intent.getLongExtra("processId", -1)
        if (processId >= 0) {
            // a valid process, so this is edit, not new!
            val process = fotoTimerProcessViewModel.getProcessById(processId)
            if (process != null) {
                binding.editProcessName.setText(process.name)
                binding.editProcessTime.setText(process.processTime.toString())
                binding.editIntervalTime.setText(process.intervalTime.toString())
                binding.switchSoundStart.isChecked = process.hasSoundStart
                binding.switchSoundEnd.isChecked = process.hasSoundEnd
                binding.switchSoundInterval.isChecked = process.hasSoundInterval
                binding.switchSoundMetronome.isChecked = process.hasSoundMetronome
                binding.switchLeadIn.isChecked = process.hasLeadIn
                binding.editLeadInTime.setText((process.leadInSeconds ?: leadInTimeDefault).toString())
                binding.switchChain.isChecked = process.hasAutoChain
                binding.switchChainPause.isChecked = process.hasPauseBeforeChain ?: false
                binding.editGotoPauseTime.setText((process.pauseTime ?: pauseTimeDefault).toString())
                if ((process.gotoId != null) && (process.gotoId > 0)) {
                    for (i in 0..binding.spinnerGotoId.count) {
                        if (process.gotoId == binding.spinnerGotoId.getItemIdAtPosition(i)) {
                            binding.spinnerGotoId.setSelection(i)
                            break
                        }
                    }
                }
                hideAndShow()
            }
        }

        // listeners
        // save button
        val button = findViewById<Button>(R.id.button_save)
        button.setOnClickListener {
            // create process, using default values where fields make no sense
            val replyIntent = Intent()

            val processName = binding.editProcessName.text.toString()
            val processTime: Long = binding.editProcessTime.text.toString().toLongOrNull() ?: processTimeDefault
            val intervalTime: Long = binding.editIntervalTime.text.toString().toLongOrNull() ?: intervalTimeDefault
            val hasSoundStart = binding.switchSoundStart.isChecked
            val soundStartId: Long = SoundStuff.SOUND_ID_PROCESS_START
            val hasSoundEnd = binding.switchSoundEnd.isChecked
            val soundEndId: Long = SoundStuff.SOUND_ID_PROCESS_END
            val hasSoundInterval = binding.switchSoundInterval.isChecked
            val soundIntervalId: Long = SoundStuff.SOUND_ID_INTERVAL
            val hasSoundMetronome = binding.switchSoundMetronome.isChecked
            val hasLeadIn = binding.switchLeadIn.isChecked
            val leadInSeconds = binding.editLeadInTime.text.toString().toIntOrNull() ?: leadInTimeDefault
            val hasAutoChain = binding.switchChain.isChecked
            val hasPauseBeforeChain = binding.switchChainPause.isChecked
            val pauseTime = binding.editGotoPauseTime.text.toString().toIntOrNull() ?: pauseTimeDefault
            val gotoId: Long = binding.spinnerGotoId.selectedItemId ?: -1
            val fotoTimerProcess = FotoTimerProcess(
                processName,
                processTime,
                intervalTime,
                hasSoundStart,
                soundStartId,
                hasSoundEnd,
                soundEndId,
                hasSoundInterval,
                soundIntervalId,
                hasSoundMetronome,
                hasLeadIn,
                leadInSeconds,
                hasAutoChain,
                hasPauseBeforeChain,
                pauseTime,
                gotoId
            )
            val jsonRepresentation =
                Json.encodeToString(FotoTimerProcess.serializer(), fotoTimerProcess)
            replyIntent.putExtra(EXTRA_REPLY, jsonRepresentation)
            setResult(RESULT_OK, replyIntent)

            finish()
        }

        // sound start switch
        binding.switchSoundStart.setOnCheckedChangeListener { _, _ ->
            hideAndShow()
        }
        // sound end switch
        binding.switchSoundEnd.setOnCheckedChangeListener { _, _ ->
            hideAndShow()
        }
        // sound interval switch
        binding.switchSoundInterval.setOnCheckedChangeListener { _, _ ->
            hideAndShow()
        }
        // lead-in switch
        binding.switchLeadIn.setOnCheckedChangeListener { _, _ ->
            hideAndShow()
        }
        // auto-goto switch
        binding.switchChain.setOnCheckedChangeListener { _, _ ->
            hideAndShow()
        }
        // auto-goto pause before switch
        binding.switchChainPause.setOnCheckedChangeListener { _, _ ->
            hideAndShow()
        }
    }

    companion object {
        const val EXTRA_REPLY = "com.exner.tools.fototimerresearch2.REPLY"
    }

    private fun hideAndShow() {
        // hide and/or show elements based on process settings
        if (binding.switchLeadIn.isChecked) {
            binding.lblLeadInTime.visibility = VISIBLE
            binding.editLeadInTime.visibility = VISIBLE
        } else {
            binding.lblLeadInTime.visibility = INVISIBLE
            binding.editLeadInTime.visibility = INVISIBLE
        }
        if (binding.switchChain.isChecked) {
            binding.switchChainPause.visibility = VISIBLE
            if (binding.switchChainPause.isChecked) {
                binding.lblChainPauseTime.visibility = VISIBLE
                binding.editGotoPauseTime.visibility = VISIBLE
            } else {
                binding.lblChainPauseTime.visibility = INVISIBLE
                binding.editGotoPauseTime.visibility = INVISIBLE
            }
            binding.lblGotoId.visibility = VISIBLE
            binding.spinnerGotoId.visibility = VISIBLE
        } else {
            binding.switchChainPause.visibility = INVISIBLE
            binding.lblChainPauseTime.visibility = INVISIBLE
            binding.editGotoPauseTime.visibility = INVISIBLE
            binding.lblGotoId.visibility = INVISIBLE
            binding.spinnerGotoId.visibility = INVISIBLE
        }
    }
}