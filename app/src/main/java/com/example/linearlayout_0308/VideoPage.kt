package com.example.linearlayout_0308

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView

class VideoPage : AppCompatActivity() {
    private lateinit var videoView: VideoView;
    private lateinit var mediaController: MediaController;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_page)
        VideoLoad()
    }

    private fun VideoLoad()
    {
        videoView = findViewById<VideoView>(R.id.videoView);
        videoView.setVideoPath(getString(R.string.ServerIP)+intent.getStringExtra("URL"));
        mediaController = MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.setOnPreparedListener {
                mp-> mp.isLooping = false;
            Log.i("VideoPlayer","Duration = "+videoView.duration)
            Toast.makeText(this,"Video Load Success",Toast.LENGTH_SHORT).show()
        }
        videoView.start();
    }

    override fun onStop() {
        super.onStop()
        videoView.stopPlayback()
    }

    public fun play(view: View)
    {
        VideoLoad();
    }

    public fun back(view: View)
    {
        finish();
    }
}