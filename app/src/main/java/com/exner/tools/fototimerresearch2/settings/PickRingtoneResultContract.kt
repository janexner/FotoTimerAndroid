package com.exner.tools.fototimerresearch2
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.util.Log
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.net.toUri

class PickRingtoneResultContract : ActivityResultContract<RingtoneSelectorExtras, Uri>() {

    override fun parseResult(resultCode: Int, intent: Intent?): Uri {
        Log.i("jexner", "parseResult")
        val result = intent?.getStringExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
        return if (result.isNullOrEmpty()) {
            "none".toUri()
        } else {
            result.toUri()
        }
    }

    override fun createIntent(context: Context, input: RingtoneSelectorExtras): Intent {
        return input.getIntent()
    }
}
