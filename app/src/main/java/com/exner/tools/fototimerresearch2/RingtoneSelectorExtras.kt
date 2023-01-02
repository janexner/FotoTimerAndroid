package com.exner.tools.fototimerresearch2

import android.content.Intent
import android.media.RingtoneManager

class RingtoneSelectorExtras {
    var ringtoneDefaultUri: String = "none"
    var ringtoneExistingUri: String = "none"
    var ringtoneShowDefault: Boolean = true
    var ringtoneShowSilent: Boolean = true
    var ringtoneTitle: String = "Title"
    var ringtoneType: Int = 0 // TYPE_RINGTONE, TYPE_NOTIFICATION, TYPE_ALARM, or TYPE_ALL

    fun getIntent(): Intent {
        val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, ringtoneTitle)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, ringtoneExistingUri)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, ringtoneShowSilent)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, ringtoneType)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, ringtoneDefaultUri)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, ringtoneShowDefault)
        return intent
    }
}
