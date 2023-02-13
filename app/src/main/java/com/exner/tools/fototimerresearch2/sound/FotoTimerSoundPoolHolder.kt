package com.exner.tools.fototimerresearch2.sound

import android.content.Context
import android.media.SoundPool
import android.util.Log
import com.exner.tools.fototimerresearch2.R

object FotoTimerSoundPoolHolder {
    private val soundPool: SoundPool = SoundPool.Builder().build()
    private val soundMap: MutableMap<Long, Int> = HashMap()
    private var isReady: Boolean = false

    fun loadSounds(context: Context) {
        // load all default sounds
        soundMap[SoundStuff.SOUND_ID_PROCESS_START] = 0
        soundMap[SoundStuff.SOUND_ID_PROCESS_END] = 0
        soundMap[SoundStuff.SOUND_ID_INTERVAL] = soundPool.load(context, R.raw.zapsplat_multimedia_beep_digital_high_pitched_scanner_or_similar_87478, 0)
        soundMap[SoundStuff.SOUND_ID_METRONOME] = soundPool.load(context, R.raw.sound_250552_metronome, 0)
        soundMap[SoundStuff.SOUND_ID_LEAD_IN] = soundPool.load(context, R.raw.zapsplat_multimedia_beep_digital_soft_click_delayed_87490, 0)
        soundMap[SoundStuff.SOUND_ID_PRE_BEEPS] = 0
        soundMap[SoundStuff.SOUND_ID_TICK_WHILE_CHAINING] = 0
        // sounds are loaded, so we are ready to play!
        isReady = true
    }

    fun playSound(soundId: Long) {
        if (isReady) {
            val soundPoolId = soundMap[soundId]
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
