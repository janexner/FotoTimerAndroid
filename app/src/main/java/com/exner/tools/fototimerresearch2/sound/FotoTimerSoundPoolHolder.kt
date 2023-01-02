package com.exner.tools.fototimerresearch2.sound

import android.content.Context
import android.media.SoundPool
import android.util.Log
import com.exner.tools.fototimerresearch2.R

class FotoTimerSoundPoolHolder(context: Context) {
    var soundPool: SoundPool = SoundPool.Builder().build()
    private val soundMap: MutableMap<Long, Int> = HashMap()

    init {
        // load all default sounds
        soundMap[SoundStuff.SOUND_ID_PROCESS_START] = 0
        soundMap[SoundStuff.SOUND_ID_PROCESS_END] = 0
        soundMap[SoundStuff.SOUND_ID_INTERVAL] = 0
        soundMap[SoundStuff.SOUND_ID_METRONOME] = soundPool.load(context, R.raw.sound_250552_metronome, 0)
        soundMap[SoundStuff.SOUND_ID_LEAD_IN] = 0
    }

    public fun playSound(soundId: Long) {
        val soundPoolId = soundMap[soundId]
        if (0 != soundPoolId) {
            // invalid!
            Log.w("Foto Timer", "Trying to play a non-existing sound!")
        } else {
            soundPool.play(soundPoolId, 1F, 1F, 0, 0, 1F)
        }
    }
}
