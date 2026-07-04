package me.blog.korn123.easydiary.activities

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.MediaController
import me.blog.korn123.easydiary.databinding.ActivityMediaViewerBinding
import java.io.File

class MediaViewerActivity : EasyDiaryActivity() {
    private lateinit var mBinding: ActivityMediaViewerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMediaViewerBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        val path = intent.getStringExtra("path") ?: ""
        val mimeType = intent.getStringExtra("mimeType") ?: ""

        val file = File(path)
        if (!file.exists()) {
            finish()
            return
        }

        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
        mBinding.toolbar.setNavigationOnClickListener {
            finish()
        }

        if (mimeType.startsWith("video")) {
            mBinding.videoView.setVideoPath(path)
            val mediaController = MediaController(this)
            mediaController.setAnchorView(mBinding.videoView)
            mBinding.videoView.setMediaController(mediaController)
            mBinding.videoView.start()
        } else if (mimeType.startsWith("audio")) {
            mBinding.audioIcon.visibility = View.VISIBLE
            mBinding.videoView.setVideoPath(path)
            val mediaController = MediaController(this)
            mediaController.setAnchorView(mBinding.videoView)
            mBinding.videoView.setMediaController(mediaController)
            mBinding.videoView.start()
        }
        
        window.statusBarColor = Color.BLACK
    }
}
