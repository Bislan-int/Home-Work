package com.example.homework

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import com.example.homework.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var service: MusicService
    private var bound: Boolean = false
    private var isPlaying: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }

        val intent = MusicService.newIntent(this)
        startService(intent)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)

        binding.buttonPlay.setOnClickListener {
            isPlaying = if (isPlaying) {
                binding.buttonPlay.setImageResource(R.drawable.ic_play)
                false
            } else {
                binding.buttonPlay.setImageResource(R.drawable.ic_pause)
                true
            }
            service.playPauseMusic()
        }

        binding.buttonNext.setOnClickListener {
            service.nextSong()
        }

        binding.buttonPrevious.setOnClickListener {
            service.previousSong()
        }
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.LocalBinder
            this@MainActivity.service = binder.getService()
            this@MainActivity.service.musicName = {
                binding.musicName.text = it
            }
            bound = true
        }

        override fun onServiceDisconnected(Name: ComponentName?) {
            bound = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (bound) {
            unbindService(connection)
            stopService(MusicService.newIntent(this))
            bound = false
        }
    }
}