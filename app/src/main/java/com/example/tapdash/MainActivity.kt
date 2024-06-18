// MainActivity.kt
package com.example.tapdash

import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var gridLayout: GridLayout
    private lateinit var textViewTimer: TextView
    private lateinit var textViewMoves: TextView
    private lateinit var buttonShuffle: Button

    private var timerRunning = false
    private var moveCount = 0
    private var startTime: Long = 0

    private val puzzleSize = 3

    private val tiles = Array(puzzleSize) { IntArray(puzzleSize) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gridLayout = findViewById(R.id.gridLayout)
        textViewTimer = findViewById(R.id.textViewTimer)
        textViewMoves = findViewById(R.id.textViewMoves)
        buttonShuffle = findViewById(R.id.buttonShuffle)

        buttonShuffle.setOnClickListener {
            shuffleTiles()
            startTimer()
            moveCount = 0
            updateMovesCounter()
        }

        initializeGrid()
    }

    private fun initializeGrid() {
        val width = resources.displayMetrics.widthPixels / puzzleSize
        val height = resources.displayMetrics.heightPixels / puzzleSize

        for (row in 0 until puzzleSize) {
            for (col in 0 until puzzleSize) {
                val tile = Button(this)
                tile.textSize = 24F
                tile.width = width
                tile.height = height
                tile.setOnClickListener { moveTile(row, col) }
                gridLayout.addView(tile)
                tiles[row][col] = row * puzzleSize + col + 1
            }
        }
        tiles[puzzleSize - 1][puzzleSize - 1] = 0
        shuffleTiles()
    }

    private fun shuffleTiles() {
        val random = Random()
        for (i in 0 until puzzleSize * puzzleSize * 10) {
            val row1 = random.nextInt(puzzleSize)
            val col1 = random.nextInt(puzzleSize)
            val row2 = random.nextInt(puzzleSize)
            val col2 = random.nextInt(puzzleSize)
            val temp = tiles[row1][col1]
            tiles[row1][col1] = tiles[row2][col2]
            tiles[row2][col2] = temp
        }
        updateGrid()
    }

    private fun moveTile(row: Int, col: Int) {
        val directions = arrayOf(
            Pair(-1, 0), // Up
            Pair(1, 0),  // Down
            Pair(0, -1), // Left
            Pair(0, 1)   // Right
        )

        for ((dr, dc) in directions) {
            val newRow = row + dr
            val newCol = col + dc
            if (isValidPosition(newRow, newCol) && tiles[newRow][newCol] == 0) {
                tiles[newRow][newCol] = tiles[row][col]
                tiles[row][col] = 0
                moveCount++
                updateMovesCounter()
                updateGrid()
                if (checkCompletion()) {
                    stopTimer()
                    buttonShuffle.visibility = View.INVISIBLE
                    break
                }
                return
            }
        }
    }

    private fun isValidPosition(row: Int, col: Int): Boolean {
        return row in 0 until puzzleSize && col in 0 until puzzleSize
    }

    private fun updateGrid() {
        for (row in 0 until puzzleSize) {
            for (col in 0 until puzzleSize) {
                val tile = gridLayout.getChildAt(row * puzzleSize + col) as Button
                tile.text = if (tiles[row][col] == 0) "" else tiles[row][col].toString()
            }
        }
    }

    private fun checkCompletion(): Boolean {
        var value = 1
        for (row in 0 until puzzleSize) {
            for (col in 0 until puzzleSize) {
                if (tiles[row][col] != value % (puzzleSize * puzzleSize)) {
                    return false
                }
                value++
            }
        }
        return true
    }

    private fun startTimer() {
        if (!timerRunning) {
            startTime = SystemClock.elapsedRealtime()
            textViewTimer.postDelayed(timerRunnable, 0)
            timerRunning = true
        }
    }

    private val timerRunnable = object : Runnable {
        override fun run() {
            val elapsedTime = SystemClock.elapsedRealtime() - startTime
            val minutes = (elapsedTime / 60000).toInt()
            val seconds = ((elapsedTime / 1000) % 60).toInt()
            val timeFormatted = String.format("%02d:%02d", minutes, seconds)
            textViewTimer.text = "Time: $timeFormatted"
            textViewTimer.postDelayed(this, 1000)
        }
    }

    private fun stopTimer() {
        textViewTimer.removeCallbacks(timerRunnable)
        timerRunning = false
    }

    private fun updateMovesCounter() {
        textViewMoves.text = "Moves: $moveCount"
    }
}
