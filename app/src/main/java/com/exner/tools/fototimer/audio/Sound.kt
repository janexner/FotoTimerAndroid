package com.exner.tools.fototimer.audio

import android.content.Context
import android.media.SoundPool
import android.util.Log
import com.exner.tools.fototimer.R

object SoundIDs {
    const val SOUND_ID_PROCESS_START: Long = 1
    const val SOUND_ID_PROCESS_END: Long = 2
    const val SOUND_ID_INTERVAL: Long = 3
    const val SOUND_ID_METRONOME: Long = 4
    const val SOUND_ID_LEAD_IN: Long = 5
    const val SOUND_ID_PRE_BEEPS: Long = 6
    const val SOUND_ID_TICK_WHILE_CHAINING: Long = 7
}

object SoundPoolHolder {
    private lateinit var soundPool: SoundPool
    private val soundMap: MutableMap<Long, Int> = HashMap()
    private var isReady: Boolean = false

    fun loadSounds(context: Context) {
        soundPool = SoundPool.Builder().build()
        // load all default sounds
        soundMap[SoundIDs.SOUND_ID_PROCESS_START] = soundPool.load(context, R.raw.start, 1)
        soundMap[SoundIDs.SOUND_ID_PROCESS_END] = soundPool.load(context, R.raw.stop, 1)
        soundMap[SoundIDs.SOUND_ID_INTERVAL] = soundPool.load(context, R.raw.ping, 1)
        soundMap[SoundIDs.SOUND_ID_METRONOME] = soundPool.load(context, R.raw.sound_250552_metronome, 1)
        soundMap[SoundIDs.SOUND_ID_LEAD_IN] = soundPool.load(context, R.raw.finger_snap_179180, 1)
        soundMap[SoundIDs.SOUND_ID_PRE_BEEPS] = soundPool.load(context, R.raw.mouth_bass_102317, 1)
        soundMap[SoundIDs.SOUND_ID_TICK_WHILE_CHAINING] = 0
        // sounds are loaded, so we are ready to play!
        isReady = true
    }

    fun release() {
        soundPool.release()
    }

    fun playSound(soundId: Long) {
        Log.d("Sound", "playSound $soundId...")
        if (isReady) {
            val soundPoolId = soundMap[soundId]
            Log.d("Sound", "translate $soundId -> $soundPoolId")
            if (null != soundPoolId && 0 < soundPoolId) {
                soundPool.play(soundPoolId, 1F, 1F, 0, 0, 1F)
            } else {
                // invalid!
                Log.w("Foto Timer", "Trying to play a non-existing sound!")
            }
        } else {
            Log.w("jexner SoundPoolHolder", "Not yet ready to play sounds.")
        }
    }
}