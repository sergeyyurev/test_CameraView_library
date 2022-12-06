package com.example.cameraview_testapp

import android.media.Image
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AppCompatActivity
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.PictureResult
import com.otaliastudios.cameraview.VideoResult
import com.otaliastudios.cameraview.controls.Mode
import com.otaliastudios.cameraview.frame.Frame
import com.otaliastudios.cameraview.frame.FrameProcessor
import com.otaliastudios.cameraview.size.Size


class MainActivity : AppCompatActivity() {
    lateinit var camera: CameraView
    lateinit var btn: Button
    lateinit var textV: TextView
    val logTag = "debug"
    val cameraModeIsPhoto = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textV = findViewById(R.id.myTextView)

        camera = findViewById<CameraView>(R.id.camera)
        camera.setLifecycleOwner(this)
        camera.addCameraListener(object : CameraListener(){
            override fun onPictureTaken(result: PictureResult) {
                Log.d(logTag, "Picture taken")
                super.onPictureTaken(result)
            }

            override fun onVideoTaken(result: VideoResult) {
                Log.d(logTag, "Video taken")
                super.onVideoTaken(result)
            }
        })

        btn = findViewById<Button>(R.id.button)
        btn.setOnClickListener {
            if(cameraModeIsPhoto) {
                camera.setMode(Mode.VIDEO)
            } else {
                camera.setMode(Mode.PICTURE)
            }
        }


        // работает в отдельном потоке
        camera.addFrameProcessor { frame ->
            val time: Long = frame.time
            val size: Size = frame.size
            val format: Int = frame.format
            val userRotation: Int = frame.rotationToUser
            val viewRotation: Int = frame.rotationToView


            if (frame.getDataClass() === ByteArray::class.java) {
                // if camera1 engine
                // до 40 миллисекунд частота логов
                // формат - ImageFormat.NV21 (его int значение - 17)
                val str = "1: time=$time, format=$format(17=>NV21), user rotation=$userRotation"
                Log.d(logTag, str)
                textV.text = str
                Thread.sleep(1000)
                val data: ByteArray = frame.getData()
                Log.d(logTag, "object № = $data")
                // Process byte array...
            } else if (frame.getDataClass() === Image::class.java) {
                // if camera2 engine
                val data: Image = frame.getData()
                Log.d(logTag, "2 engine: $time, $size, $format, $userRotation, $viewRotation")
                // Process android.media.Image...
            }
        }

    }
}