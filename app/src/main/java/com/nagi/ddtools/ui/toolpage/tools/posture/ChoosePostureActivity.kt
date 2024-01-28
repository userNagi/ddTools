package com.nagi.ddtools.ui.toolpage.tools.posture

import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.nagi.ddtools.data.ProgressListener
import com.nagi.ddtools.databinding.ActivityChoosePostureBinding
import com.nagi.ddtools.ui.base.DdToolsBindingBaseActivity
import com.nagi.ddtools.utils.FileUtils
import com.nagi.ddtools.utils.NetUtils
import com.nagi.ddtools.utils.UiUtils.dialog
import com.nagi.ddtools.utils.UiUtils.toast
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class ChoosePostureActivity : DdToolsBindingBaseActivity<ActivityChoosePostureBinding>() {
    private val url = ""
//        "https://wiki.chika-idol.live/request/ddtools/ddtools.zip"
    private val imageUpdateExecutor: ScheduledExecutorService =
        Executors.newSingleThreadScheduledExecutor()
    private var imageUpdateFuture: Future<*>? = null
    private val mainThreadHandler = Handler(Looper.getMainLooper())
    private var pngImageFiles: List<File>? = null

    override fun createBinding(): ActivityChoosePostureBinding {
        return ActivityChoosePostureBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        checkResources()
        binding.apply {
            titleInclude.apply {
                titleText.text = "什么姿势"
                titleBack.setOnClickListener {
                    finish()
                }
            }
            confirmButton.visibility = View.GONE
        }
    }

    private fun checkResources() {
        val path = filesDir.path + "/posture"
        FileUtils.checkOrCreatePostureDirectory(path)
        if (!FileUtils.doesFileExist(path, "resources.info")
            || FileUtils.countFilesInDirectory(path) < 10
        ) {
            dialog(
                "下载提示",
                "为了减小包体积，当前包内不含相关的图片文件，需要额外下载，下载内容约10m，是否下载？",
                "是",
                "否",
                {
                    val progressListener = object : ProgressListener {
                        override fun onProgress(progress: Int) {
                            runOnUiThread {
                                binding.progressBar.progress = progress
                            }
                        }
                    }
                    NetUtils.downloadFileWithProgress(url, path, progressListener) { file, e ->
                        runOnUiThread { binding.progressBar.visibility = View.GONE }
                        if (e != null) {
                            toast(" 下载失败，请检查网络并重试")

                        } else if (file != null) {
                            binding.progressBar.visibility = View.VISIBLE
                            toast(" 下载成功，即将开始解压")
                            val listener = object : ProgressListener {
                                override fun onProgress(progress: Int) {
                                    runOnUiThread {
                                        binding.progressBar.visibility = View.VISIBLE
                                        binding.progressBar.progress = progress
                                    }
                                }
                            }
                            if (FileUtils.unzipFile(file, path, listener)) {
                                runOnUiThread {
                                    binding.progressBar.visibility = View.GONE
                                    toast("解压成功")
                                    startImageRotation()
                                    binding.confirmButton.apply {
                                        visibility = View.VISIBLE
                                        stopImageRotation()
                                    }
                                }
                            }
                        }
                    }
                }, {
                }
            )
        }

    }

    private fun getPngImagesFromDirectory(directory: File): List<File> {
        return directory.listFiles { _, name -> name.endsWith(".png") }?.toList() ?: emptyList()
    }

    private fun startImageRotation() {
        val path = filesDir.path + "/posture"
        pngImageFiles = getPngImagesFromDirectory(File(path))

        val imageUpdateTask = Runnable {
            val nextImageIndex = Random.nextInt(pngImageFiles!!.size)
            val nextImageFile = pngImageFiles!![nextImageIndex]

            mainThreadHandler.post {
                binding.shapeableImageView.setImageBitmap(BitmapFactory.decodeFile(nextImageFile.path))
            }
        }

        imageUpdateFuture =
            imageUpdateExecutor.scheduleAtFixedRate(imageUpdateTask, 0, 200, TimeUnit.MILLISECONDS)
    }

    private fun stopImageRotation() {
        imageUpdateFuture?.cancel(true)

        pngImageFiles?.let { imageFiles ->
            if (imageFiles.isNotEmpty()) {
                val finalImageIndex = Random.nextInt(imageFiles.size)
                val finalImageFile = imageFiles[finalImageIndex]

                mainThreadHandler.post {
                    binding.shapeableImageView.setImageBitmap(
                        BitmapFactory.decodeFile(
                            finalImageFile.path
                        )
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopImageRotation()
        imageUpdateExecutor.shutdownNow()
    }
}
