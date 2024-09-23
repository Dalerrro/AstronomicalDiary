package com.example.startapp.openGL

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.content.Context
import android.util.Log
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyGLRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private lateinit var square: Square
    private lateinit var cube: Cube

    private val mvpMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)

        square = Square(context)
        cube = Cube()


        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 5f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
        Log.d("MyGLRenderer", "Surface created: Square and Cube initialized.")
    }

    override fun onDrawFrame(gl: GL10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        GLES20.glEnable(GLES20.GL_DEPTH_TEST)

        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        // Рисуем квадрат
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, 0f, 0f, -1.5f)


        val scratchSquare = FloatArray(16)
        Matrix.multiplyMM(scratchSquare, 0, mvpMatrix, 0, modelMatrix, 0)

        GLES20.glDisable(GLES20.GL_DEPTH_TEST)

        Log.d("MyGLRenderer", "Drawing square with MVP matrix: ${scratchSquare.joinToString()}")
        square.draw(scratchSquare)


        Log.d("MyGLRenderer", "Square drawn.")

        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, 0f, 0f, 0.0f)
        val scratchCube = FloatArray(16)
        Matrix.multiplyMM(scratchCube, 0, mvpMatrix, 0, modelMatrix, 0)

        cube.draw(scratchCube)
        Log.d("MyGLRenderer", "Cube drawn.")
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        val ratio: Float = width.toFloat() / height.toFloat()
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)

        Log.d("MyGLRenderer", "Surface changed: width = $width, height = $height")
    }
}