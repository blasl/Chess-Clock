package com.example.chessclock.utils

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.chessclock.R

class UiController{
    fun updateUI(
        isPlayer1Turn: Boolean,
        cvPlayer1: CardView,
        cvPlayer2: CardView,
        tvPlayer1Time: TextView,
        tvPlayer2Time: TextView,
        ivPauseResumeIcon: ImageView,
        tvPlayer1Moves: TextView,
        tvPlayer2Moves: TextView
    ) {
        if (isPlayer1Turn) {
            cvPlayer2.isClickable = false
            cvPlayer1.isClickable = true
            cvPlayer1.setCardBackgroundColor(
                ContextCompat.getColor(cvPlayer1.context, R.color.player_panel)
            )
            cvPlayer2.setCardBackgroundColor(
                ContextCompat.getColor(cvPlayer2.context, R.color.player_off)
            )
            tvPlayer1Time.setTextColor(ContextCompat.getColor(tvPlayer1Time.context, R.color.white))
            tvPlayer2Time.setTextColor(ContextCompat.getColor(tvPlayer2Time.context, R.color.num_color))
        } else {
            cvPlayer1.isClickable = false
            cvPlayer2.isClickable = true
            cvPlayer2.setCardBackgroundColor(
                ContextCompat.getColor(cvPlayer2.context, R.color.player_panel)
            )
            cvPlayer1.setCardBackgroundColor(
                ContextCompat.getColor(cvPlayer1.context, R.color.player_off)
            )
            tvPlayer2Time.setTextColor(ContextCompat.getColor(tvPlayer2Time.context, R.color.white))
            tvPlayer1Time.setTextColor(ContextCompat.getColor(tvPlayer1Time.context, R.color.num_color))
        }
        ivPauseResumeIcon.setImageResource(R.drawable.ic_pause)
        tvPlayer1Moves.isVisible = true
        tvPlayer2Moves.isVisible = true
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
        val minutes = (timeLeftInMillis / 1000) / 60
        val seconds = (timeLeftInMillis / 1000) % 60
        val timeFormatted = if (seconds >= 10) "$minutes:$seconds" else "$minutes:0$seconds"

        textView.text = timeFormatted
    }
}