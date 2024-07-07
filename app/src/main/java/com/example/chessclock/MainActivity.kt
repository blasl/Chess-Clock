package com.example.chessclock

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.chessclock.databinding.ActivityMainBinding
import com.example.chessclock.utils.ClockDialog
import com.example.chessclock.utils.Sound
import com.example.chessclock.utils.UiController

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var countDownTimer: CountDownTimer
    lateinit var uiController: UiController
    lateinit var clockDialog: ClockDialog
    lateinit var sound: Sound
    var player1Moves = 0
    var player2Moves = 0
    var isPlayer1Turn = true
    var isClockRunning = false
    var isSilentMode = false
    var isFinished = false
    var increment: Long = 5000
    var startTimeInMillis1: Long = 5 * 60 * 1000
    var startTimeInMillis2: Long = 5 * 60 * 1000
    var timeLeftInMillis1 = startTimeInMillis1
    var timeLeftInMillis2 = startTimeInMillis2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiController = UiController(binding)
        clockDialog = ClockDialog(binding, this)
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
        binding.viewRestart.setOnClickListener { clockDialog.restartQuestion() }
        binding.viewPauseResume.setOnClickListener { pauseResumeClock() }
        binding.viewSound.setOnClickListener { sound.setMode() }
        binding.viewAdjustTime1.setOnClickListener { clockDialog.adjustTime(binding.tvPlayer1Time) }
        binding.viewAdjustTime2.setOnClickListener { clockDialog.adjustTime(binding.tvPlayer2Time) }
    }

    private fun changeTurn(playerCV: CardView) {
        if(!isFinished) {
            isPlayer1Turn = if (playerCV == binding.cvPlayer1) !isPlayer1Turn else true
            if(isClockRunning) if(isPlayer1Turn) {
                timeLeftInMillis2+=increment
                uiController.updateCountDownText(binding.tvPlayer2Time, timeLeftInMillis2)
            } else {
                timeLeftInMillis1+=increment
                uiController.updateCountDownText(binding.tvPlayer1Time, timeLeftInMillis1)
            }
            isClockRunning = true
            uiController.updateMoves(this)
            timer()
        } else clockDialog.restartQuestion()
    }

    private fun timer() {
        isClockRunning = true
        try {
            countDownTimer.cancel()
        } catch (e: Exception) { }

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

    fun pauseResumeClock() {
        if (isClockRunning) {
            countDownTimer.cancel()
            sound.reproduce(R.raw.tap)
            isClockRunning = false

            binding.cvPlayer1.setCardBackgroundColor(
                ContextCompat.getColor(this, R.color.player_off)
            )
            binding.cvPlayer2.setCardBackgroundColor(
                ContextCompat.getColor(this, R.color.player_off)
            )
            binding.tvPlayer1Time.setTextColor(ContextCompat.getColor(this, R.color.num_color))
            binding.tvPlayer2Time.setTextColor(ContextCompat.getColor(this, R.color.num_color))
            binding.ivPauseResumeIcon.setImageResource(R.drawable.ic_play)
            binding.viewAdjustTime1.visibility = View.VISIBLE
            binding.viewAdjustTime2.visibility = View.VISIBLE
            binding.ivAdjustTime1.visibility = View.VISIBLE
            binding.ivAdjustTime2.visibility = View.VISIBLE
        } else {
            timer()
            uiController.updateUI(isPlayer1Turn)
        }
    }

    fun timeToMillis(h: Editable, min: Editable, seg: Editable): Long {
        return ((h.toString().toInt()*60*60*1000)+(min.toString().toInt()*60*1000)+(seg.toString().toInt()*1000)).toLong()
    }
}