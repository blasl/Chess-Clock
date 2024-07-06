package com.example.chessclock

import android.app.Dialog
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.chessclock.databinding.ActivityMainBinding
import com.example.chessclock.utils.Sound
import com.example.chessclock.utils.UiController

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var uiController: UiController
    private lateinit var sound: Sound
    var player1Moves = 0
    var player2Moves = 0
    var isPlayer1Turn = true
    var isClockRunning = false
    var isSilentMode = false
    var isFinished = false
    var startTimeInMillis: Long = 5 * 60 * 1000
    var timeLeftInMillis1 = startTimeInMillis
    var timeLeftInMillis2 = startTimeInMillis

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiController = UiController(binding)
        sound = Sound(binding, this)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        uiController.hideSystemUI(this)
        setListeners()
    }

    private fun setListeners() {
        binding.cvPlayer1.setOnClickListener { changeTurn(binding.cvPlayer1) }
        binding.cvPlayer2.setOnClickListener { changeTurn(binding.cvPlayer2) }
        binding.viewRestart.setOnClickListener { showRestartDialog() }
        binding.viewPauseResume.setOnClickListener { pauseResumeClock() }
        binding.viewSound.setOnClickListener { sound.setMode() }
    }

    private fun changeTurn(playerCV: CardView) {
        if(!isFinished) {
            isPlayer1Turn = if (playerCV == binding.cvPlayer1) !isPlayer1Turn else true
            isClockRunning = true
            uiController.updateMoves(this)
            timer()
        } else showRestartDialog()
    }

    private fun timer() {
        isClockRunning = true
        try {
            countDownTimer.cancel()
        } catch (e: Exception) {
        }
        countDownTimer = object :
            CountDownTimer(if (isPlayer1Turn) timeLeftInMillis1 else timeLeftInMillis2, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (isPlayer1Turn) {
                    timeLeftInMillis1 = millisUntilFinished
                    uiController.updateCountDownText(binding.tvPlayer1Time, timeLeftInMillis1)
                } else {
                    timeLeftInMillis2 = millisUntilFinished
                    uiController.updateCountDownText(binding.tvPlayer2Time, timeLeftInMillis2)
                }
            }

            override fun onFinish() {
                isFinished = true
                sound.reproduce(R.raw.timeout)
                binding.viewPauseResume.visibility = View.GONE
                binding.ivPauseResumeIcon.visibility = View.GONE
                if (isPlayer1Turn) {
                    binding.cvPlayer1.setCardBackgroundColor(
                        ContextCompat.getColor(binding.cvPlayer1.context, R.color.timeout))
                    binding.tvPlayer1Time.setTextColor(
                        ContextCompat.getColor(binding.cvPlayer1.context, R.color.num_color))
                } else {
                    binding.cvPlayer2.setCardBackgroundColor(
                        ContextCompat.getColor(binding.cvPlayer2.context, R.color.timeout))
                    binding.tvPlayer2Time.setTextColor(
                        ContextCompat.getColor(binding.cvPlayer2.context, R.color.num_color))
                }
            }
        }.start()

        uiController.updateUI(isPlayer1Turn)
        sound.reproduce(R.raw.clock_tac)
    }

    private fun showRestartDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_restart_confirmation)

        val tvRestartYes: TextView = dialog.findViewById(R.id.tvRestartYes)
        val tvRestartNo: TextView = dialog.findViewById(R.id.tvRestartNo)
        if (isClockRunning) pauseResumeClock()

        tvRestartYes.setOnClickListener {
            try {
                countDownTimer.cancel()
            } catch (e: Exception) {

            } finally {
                player1Moves = 0
                player2Moves = 0
                isPlayer1Turn = true
                sound.reproduce(R.raw.restart_clock)
                timeLeftInMillis1 = startTimeInMillis
                timeLeftInMillis2 = startTimeInMillis
                binding.cvPlayer1.setCardBackgroundColor(
                    ContextCompat.getColor(this, R.color.player_off)
                )
                binding.cvPlayer2.setCardBackgroundColor(
                    ContextCompat.getColor(this, R.color.player_off)
                )
                uiController.updateCountDownText(binding.tvPlayer1Time, timeLeftInMillis1)
                uiController.updateCountDownText(binding.tvPlayer2Time, timeLeftInMillis2)
                binding.tvPlayer1Time.setTextColor(ContextCompat.getColor(this, R.color.num_color))
                binding.tvPlayer2Time.setTextColor(ContextCompat.getColor(this, R.color.num_color))
                binding.ivPauseResumeIcon.setImageResource(R.drawable.ic_play)
                binding.cvPlayer1.isClickable = true
                binding.cvPlayer2.isClickable = true
                binding.tvPlayer1Moves.isVisible = false
                binding.tvPlayer2Moves.isVisible = false
                binding.viewPauseResume.visibility = View.VISIBLE
                binding.ivPauseResumeIcon.visibility = View.VISIBLE
                isClockRunning = false
                isFinished = false

                uiController.updateMoves(this)
                dialog.hide()
            }
        }

        tvRestartNo.setOnClickListener { dialog.hide() }
        dialog.show()
    }

    private fun pauseResumeClock() {
        if (isClockRunning) {
            countDownTimer.cancel()
            sound.reproduce(R.raw.tap)
            isClockRunning = false

            binding.cvPlayer1.setCardBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.player_off
                )
            )
            binding.cvPlayer2.setCardBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.player_off
                )
            )
            binding.tvPlayer1Time.setTextColor(ContextCompat.getColor(this, R.color.num_color))
            binding.tvPlayer2Time.setTextColor(ContextCompat.getColor(this, R.color.num_color))
            binding.ivPauseResumeIcon.setImageResource(R.drawable.ic_play)
        } else {
            timer()
            uiController.updateUI(isPlayer1Turn)
        }
    }
}