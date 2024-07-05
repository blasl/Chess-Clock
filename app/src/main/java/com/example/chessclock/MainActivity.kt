package com.example.chessclock

import android.app.Dialog
import android.media.MediaPlayer
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
import com.example.chessclock.utils.UiController

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var countDownTimer: CountDownTimer
    private var uiController = UiController()
    private var player1Moves = 0
    private var player2Moves = 0
    private var isPlayer1Turn = true
    private var isClockRunning = false
    private var isSilentMode = false
    private var isFinished = false
    private var startTimeInMillis: Long = 5 * 60 * 1000
    private var timeLeftInMillis1 = startTimeInMillis
    private var timeLeftInMillis2 = startTimeInMillis

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
        binding.viewSound.setOnClickListener { setSoundMode() }
    }

    private fun changeTurn(playerCV: CardView) {
        if(!isFinished) {
            isPlayer1Turn = if (playerCV == binding.cvPlayer1) !isPlayer1Turn else true
            isClockRunning = true
            updateMoves()
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
                reproduceSound(R.raw.timeout)
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

        uiController.updateUI(isPlayer1Turn,binding.cvPlayer1, binding.cvPlayer2, binding.tvPlayer1Time,
            binding.tvPlayer2Time, binding.ivPauseResumeIcon, binding.tvPlayer1Moves, binding.tvPlayer2Moves)
        reproduceSound(R.raw.clock_tac)
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
                reproduceSound(R.raw.restart_clock)
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

                updateMoves()
                dialog.hide()
            }
        }

        tvRestartNo.setOnClickListener { dialog.hide() }
        dialog.show()
    }

    private fun pauseResumeClock() {
        if (isClockRunning) {
            countDownTimer.cancel()
            reproduceSound(R.raw.tap)
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
            uiController.updateUI(isPlayer1Turn,binding.cvPlayer1, binding.cvPlayer2, binding.tvPlayer1Time,
                binding.tvPlayer2Time, binding.ivPauseResumeIcon, binding.tvPlayer1Moves, binding.tvPlayer2Moves)
        }
    }

    private fun reproduceSound(sound: Int) {
        if (!isSilentMode) {
            val mp = MediaPlayer.create(this, sound)
            mp.start()
        }
    }

    private fun updateMoves() {
        binding.tvPlayer1Moves.text = getString(R.string.moves, player1Moves)
        binding.tvPlayer2Moves.text = getString(R.string.moves, player2Moves)

        if (isClockRunning) {
            if (isPlayer1Turn) player1Moves++ else player2Moves++
        }
    }

    private fun setSoundMode() {
        isSilentMode = !isSilentMode
        if (isSilentMode) binding.ivSoundIcon.setImageResource(R.drawable.ic_sound_off) else {
            binding.ivSoundIcon.setImageResource(R.drawable.ic_sound)
        }
    }
}