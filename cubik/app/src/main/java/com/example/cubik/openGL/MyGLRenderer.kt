package com.example.cubik.openGL

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.example.cubik.R
import com.example.cubik.Sphere
import com.example.cubik.table.Table
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyGLRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val viewProjectionMatrix = FloatArray(16)

    private lateinit var table: Table
    private lateinit var apple: Sphere
    private lateinit var watermelon: Sphere
    private val fruits = mutableListOf<Sphere>()
    private val fruitPositions = mutableListOf<FloatArray>()

    private lateinit var shaderCompiler: ShaderCompiler

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1f)

        table = Table(context)

        apple = Sphere(context, 0.1f, R.drawable.appletex1)
        watermelon = Sphere(context, 0.18f, R.drawable.arbuz)

        fruits.add(apple)
        fruits.add(watermelon)

        fruitPositions.add(floatArrayOf(-0.3f, 0.2f, 0.2f))
        fruitPositions.add(floatArrayOf(0.3f, 0.2f, 0.2f))

        shaderCompiler = ShaderCompiler(vertexShaderCode, fragmentShaderCode)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        shaderCompiler.use()
        table.draw(viewProjectionMatrix)
        drawFruits()

    }

    private fun drawFruits() {
        for (i in fruits.indices) {
            val fruit = fruits[i]
            val position = fruitPositions[i]
            val modelMatrix = FloatArray(16)
            Matrix.setIdentityM(modelMatrix, 0)
            Matrix.translateM(modelMatrix, 0, position[0], position[1], position[2])
            val mvpMatrix = FloatArray(16)
            Matrix.multiplyMM(mvpMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0)
            GLES20.glUniformMatrix4fv(
                shaderCompiler.getUniformLocation("uMVPMatrix"),
                1, false, mvpMatrix, 0
            )
            fruit.draw(mvpMatrix)
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio: Float = width.toFloat() / height.toFloat()
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 1f, 50f)
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0.8f, -2.5f, 0f, 0f, 0f, 0f, 1f, 0f)
    }
}
