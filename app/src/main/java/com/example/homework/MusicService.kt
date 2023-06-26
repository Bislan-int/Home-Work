package com.example.homework

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class MusicService : Service() {
    private val binder: IBinder = LocalBinder()
    private lateinit var listMusic: List<DataMusic>
    private var numMusic = 0

    var musicName: ((String) -> Unit)? = null

    override fun onCreate() {
        super.onCreate()
        initListMusic()
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    inner class LocalBinder : Binder() {
        fun getService() = this@MusicService
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        notification(listMusic[numMusic].name)
        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Music player",
                NotificationManager.IMPORTANCE_LOW
            )

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun notification(nameMusic: String) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Music Player")
            .setContentText(nameMusic)
            .setSmallIcon(R.drawable.ic_music)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    private fun initListMusic() {
        listMusic = listOf(
            DataMusic("Don't Leave Me Here ", MediaPlayer.create(this, R.raw.music1)),
            DataMusic("Дорогу молодым", MediaPlayer.create(this, R.raw.music2)),
            DataMusic("Memories", MediaPlayer.create(this, R.raw.music3))
        )
    }

    fun playPauseMusic() {
        if (!listMusic[numMusic].music.isPlaying) {
            listMusic[numMusic].music.start()
            musicName?.invoke(listMusic[numMusic].name)
            notification(listMusic[numMusic].name)
        } else {
            listMusic[numMusic].music.pause()
        }
    }

    fun nextSong () {
        if (numMusic <= 1) {
            if (listMusic[numMusic].music.isPlaying) {
                setPauseAndSeek()
                ++numMusic
                listMusic[numMusic].music.start()
                musicName?.invoke(listMusic[numMusic].name)
                notification(listMusic[numMusic].name)
            } else {
                setPauseAndSeek()
                ++numMusic
                musicName?.invoke(listMusic[numMusic].name)
                notification(listMusic[numMusic].name)
            }
        }
    }

    fun previousSong () {
        if (numMusic >= 1) {
            if (listMusic[numMusic].music.isPlaying) {
                setPauseAndSeek()
                --numMusic
                listMusic[numMusic].music.start()
                musicName?.invoke(listMusic[numMusic].name)
                notification(listMusic[numMusic].name)
            } else {
                setPauseAndSeek()
                --numMusic
                musicName?.invoke(listMusic[numMusic].name)
                notification(listMusic[numMusic].name)
            }
        }
    }

    private fun setPauseAndSeek() {
        listMusic[numMusic].music.pause()
        listMusic[numMusic].music.seekTo(0)
    }

    override fun onDestroy() {
        super.onDestroy()
        listMusic[numMusic].music.stop()
    }

    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "Music Player"

        fun newIntent(context: Context): Intent {
            return Intent(context, MusicService::class.java)
        }
    }
}