package com.example.chessclock.utils

import android.app.Activity
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.chessclock.MainActivity
import com.example.chessclock.R
import com.example.chessclock.databinding.ActivityMainBinding

class UiController(private var binding: ActivityMainBinding){
    fun updateUI(isPlayer1Turn: Boolean) {
        if (isPlayer1Turn) {
            binding.cvPlayer2.isClickable = false
            binding.cvPlayer1.isClickable = true
            binding.cvPlayer1.setCardBackgroundColor(
                ContextCompat.getColor(binding.cvPlayer1.context, R.color.player_panel)
            )
            binding.cvPlayer2.setCardBackgroundColor(
                ContextCompat.getColor(binding.cvPlayer2.context, R.color.player_off)
            )
            binding.tvPlayer1Time.setTextColor(ContextCompat.getColor(binding.tvPlayer1Time.context, R.color.white))
            binding.tvPlayer2Time.setTextColor(ContextCompat.getColor(binding.tvPlayer2Time.context, R.color.num_color))
        } else {
            binding.cvPlayer1.isClickable = false
            binding.cvPlayer2.isClickable = true
            binding.cvPlayer2.setCardBackgroundColor(
                ContextCompat.getColor(binding.cvPlayer2.context, R.color.player_panel)
            )
            binding.cvPlayer1.setCardBackgroundColor(
                ContextCompat.getColor(binding.cvPlayer1.context, R.color.player_off)
            )
            binding.tvPlayer2Time.setTextColor(ContextCompat.getColor(binding.tvPlayer2Time.context, R.color.white))
            binding.tvPlayer1Time.setTextColor(ContextCompat.getColor(binding.tvPlayer1Time.context, R.color.num_color))
        }
        binding.ivPauseResumeIcon.setImageResource(R.drawable.ic_pause)
        binding.tvPlayer1Moves.isVisible = true
        binding.tvPlayer2Moves.isVisible = true
        binding.viewAdjustTime1.visibility = View.GONE
        binding.viewAdjustTime2.visibility = View.GONE
        binding.ivAdjustTime1.visibility = View.GONE
        binding.ivAdjustTime2.visibility = View.GONE
    }

    fun hideSystemUI(activity: Activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity.window.insetsController?.let {
                it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            // Para versiones anteriores a Android 11
            @Suppress("DEPRECATION")
            activity.window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        }
    }

    fun updateCountDownText(textView: TextView, timeLeftInMillis: Long) {
        val hours = (timeLeftInMillis / (1000 * 60 * 60))
        val minutes = ((timeLeftInMillis / (1000 * 60) % 60))
        val seconds = (timeLeftInMillis / 1000) % 60

        val timeFormatted = if(hours < 1) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 120f)
            if (seconds >= 10) "$minutes:$seconds" else "$minutes:0$seconds"
        } else {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 90f)
            if (minutes >= 10) {
                if (seconds >= 10) "$hours:$minutes:$seconds" else "$hours:$minutes:0$seconds"
            } else {
                if (seconds >= 10) "$hours:0$minutes:$seconds" else "$hours:0$minutes:0$seconds"
            }
        }

        textView.text = timeFormatted
    }

    fun updateMoves(activity: MainActivity) {
        binding.tvPlayer1Moves.text = activity.getString(R.string.moves, activity.player1Moves)
        binding.tvPlayer2Moves.text = activity.getString(R.string.moves, activity.player2Moves)

        if (activity.isClockRunning) {
            if (activity.isPlayer1Turn) activity.player1Moves++ else activity.player2Moves++
        }
    }
}