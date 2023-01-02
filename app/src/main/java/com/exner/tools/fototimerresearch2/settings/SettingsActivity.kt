package com.exner.tools.fototimerresearch2.settings

import android.app.Activity
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import com.exner.tools.fototimerresearch2.PickRingtoneResultContract
import com.exner.tools.fototimerresearch2.R
import com.exner.tools.fototimerresearch2.RingtoneSelectorExtras


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                super.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        private val ringtonePicker = registerForActivityResult(PickRingtoneResultContract()) { uri: Uri? ->
            // use resulting Uri
            Log.i("jexner", "register $uri")
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            // put listeners on those sound selector preferences
            val metronomeSoundPreference: EditTextPreference? =
                findPreference("preference_metronome_sound")
            metronomeSoundPreference?.setOnPreferenceClickListener {
                // call the thing
                val extras = RingtoneSelectorExtras()
                extras.ringtoneTitle = "Select Metronome Sound"
                extras.ringtoneDefaultUri = "none"
                extras.ringtoneExistingUri = "none"
                extras.ringtoneShowSilent = true
                extras.ringtoneShowDefault = true
                extras.ringtoneType = RingtoneManager.TYPE_ALL
                ringtonePicker.launch(extras)

                true
            }

            // restrict numeric fields to numeric input
            val processTimePreference: EditTextPreference? =
                findPreference("preference_process_time")
            processTimePreference?.setOnBindEditTextListener { editText ->
                editText.inputType = InputType.TYPE_CLASS_NUMBER
            }
            val intervalTimePreference: EditTextPreference? =
                findPreference("preference_interval_time")
            intervalTimePreference?.setOnBindEditTextListener { editText ->
                editText.inputType = InputType.TYPE_CLASS_NUMBER
            }
            val leadInTimePreference: EditTextPreference? =
                findPreference("preference_lead_in_time")
            leadInTimePreference?.setOnBindEditTextListener { editText ->
                editText.inputType = InputType.TYPE_CLASS_NUMBER
            }
            val pauseTimePreference: EditTextPreference? = findPreference("preference_pause_time")
            pauseTimePreference?.setOnBindEditTextListener { editText ->
                editText.inputType = InputType.TYPE_CLASS_NUMBER
            }
        }

        public var ringToneSelectorLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    data?.getStringExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
                        ?.let {
                            val ringTone = RingtoneManager.getRingtone(context, it.toUri())
                            Toast.makeText(context, "Selected $ringTone", Toast.LENGTH_LONG).show()
                        }
                }
            }

    }
}