package com.example.chessclock.utils

import android.app.Dialog
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.chessclock.MainActivity
import com.example.chessclock.R
import com.example.chessclock.databinding.ActivityMainBinding

class ClockDialog(private var binding: ActivityMainBinding, private var activity: MainActivity) {
    fun restartQuestion() {
        val dialog = Dialog(activity)
        dialog.setContentView(R.layout.dialog_restart_confirmation)

        val tvRestartYes: TextView = dialog.findViewById(R.id.tvRestartYes)
        val tvRestartNo: TextView = dialog.findViewById(R.id.tvRestartNo)
        if (activity.isClockRunning) activity.pauseResumeClock()

        tvRestartYes.setOnClickListener {
            try {
                activity.countDownTimer.cancel()
            } catch (e: Exception) {

            } finally {
                activity.player1Moves = 0
                activity.player2Moves = 0
                activity.isPlayer1Turn = true
                activity.sound.reproduce(R.raw.restart_clock)
                activity.timeLeftInMillis1 = activity.startTimeInMillis1
                activity.timeLeftInMillis2 = activity.startTimeInMillis2
                binding.cvPlayer1.setCardBackgroundColor(
                    ContextCompat.getColor(activity, R.color.player_off)
                )
                binding.cvPlayer2.setCardBackgroundColor(
                    ContextCompat.getColor(activity, R.color.player_off)
                )
                activity.uiController.updateCountDownText(binding.tvPlayer1Time, activity.timeLeftInMillis1)
                activity.uiController.updateCountDownText(binding.tvPlayer2Time, activity.timeLeftInMillis2)
                binding.tvPlayer1Time.setTextColor(ContextCompat.getColor(activity, R.color.num_color))
                binding.tvPlayer2Time.setTextColor(ContextCompat.getColor(activity, R.color.num_color))
                binding.ivPauseResumeIcon.setImageResource(R.drawable.ic_play)
                binding.cvPlayer1.isClickable = true
                binding.cvPlayer2.isClickable = true
                binding.tvPlayer1Moves.isVisible = false
                binding.tvPlayer2Moves.isVisible = false
                binding.viewPauseResume.visibility = View.VISIBLE
                binding.ivPauseResumeIcon.visibility = View.VISIBLE
                activity.isClockRunning = false
                activity.isFinished = false

                activity.uiController.updateMoves(activity)
                dialog.hide()
            }
        }

        tvRestartNo.setOnClickListener { dialog.hide() }
        dialog.show()
    }

    fun adjustTime(playerTime: TextView) {
        activity.sound.reproduce(R.raw.tap)
        val dialog = Dialog(activity)
        dialog.setContentView(R.layout.dialog_adjust_time)

        val tvTimeSave = dialog.findViewById<TextView>(R.id.tvTimeSave)
        val tvTimeCancel = dialog.findViewById<TextView>(R.id.tvTimeCancel)
        val etHour = dialog.findViewById<EditText>(R.id.etHour)
        val etMinute = dialog.findViewById<EditText>(R.id.etMinute)
        val etSecond = dialog.findViewById<EditText>(R.id.etSecond)
        val time = playerTime.text.split(":").reversed()

        etSecond.setText(time[0])
        etMinute.setText(time[1])
        if(time.size > 2) etHour.setText(time[2]) else etHour.setText("00")

        tvTimeSave.setOnClickListener {
            if (playerTime == binding.tvPlayer1Time) {
                activity.timeLeftInMillis1 = activity.timeToMillis(etHour.text, etMinute.text, etSecond.text)
                activity.uiController.updateCountDownText(playerTime, activity.timeLeftInMillis1)
                activity.startTimeInMillis1 = activity.timeToMillis(etHour.text, etMinute.text, etSecond.text)
            } else {
                activity.timeLeftInMillis2 = activity.timeToMillis(etHour.text, etMinute.text, etSecond.text)
                activity.uiController.updateCountDownText(playerTime, activity.timeLeftInMillis2)
                activity.startTimeInMillis2 = activity.timeToMillis(etHour.text, etMinute.text, etSecond.text)
            }

            dialog.hide()
        }

        tvTimeCancel.setOnClickListener { dialog.hide() }
        dialog.show()
    }
}