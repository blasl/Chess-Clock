package com.example.chessclock.utils

import android.media.MediaPlayer
import com.example.chessclock.MainActivity
import com.example.chessclock.R
import com.example.chessclock.databinding.ActivityMainBinding

class Sound(private var binding: ActivityMainBinding, private var activity: MainActivity) {
    fun setMode() {
        activity.isSilentMode = !activity.isSilentMode
        if (activity.isSilentMode) binding.ivSoundIcon.setImageResource(R.drawable.ic_sound_off) else {
            binding.ivSoundIcon.setImageResource(R.drawable.ic_sound)
        }
    }

    fun reproduce(sound: Int) {
        if (!activity.isSilentMode) {
            val mp = MediaPlayer.create(activity, sound)
            mp.start()
        }
    }
}