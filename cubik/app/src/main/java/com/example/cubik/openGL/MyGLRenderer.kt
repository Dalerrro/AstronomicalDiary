package com.example.cubik.openGL

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.example.cubik.Cylinder
import com.example.cubik.Ellipsoid
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

    private lateinit var orange: Sphere
    private lateinit var orange1: Sphere
    private lateinit var orange2: Sphere

    private lateinit var lemon: Ellipsoid

    private lateinit var apple: Sphere
    private lateinit var watermelon: Sphere

    private lateinit var glass: Cylinder
    private lateinit var water: Cylinder

    private val fruits = mutableListOf<Sphere>()
    private val fruitPositions = mutableListOf<FloatArray>()

    private lateinit var shaderCompiler: ShaderCompiler

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glClearColor(0.4f, 0.4f, 0.4f, 1f)

        table = Table(context)

        apple = Sphere(context, 0.078f, R.drawable.appletex1)
        watermelon = Sphere(context, 0.18f, R.drawable.arbuz)

        orange = Sphere(context, 0.08f, R.drawable.orange)
        orange1 = Sphere(context, 0.08f, R.drawable.orange)
        orange2 = Sphere(context, 0.08f, R.drawable.orange)

        val lemon = Ellipsoid(context, 0.1f, 0.07f, 0.1f, R.drawable.lemin)


        fruits.add(apple)
        fruits.add(watermelon)

        fruits.add(orange)
        fruits.add(orange1)
        fruits.add(orange2)

        fruits.add(lemon)


        fruitPositions.add(floatArrayOf(0.4f, 0.16f, -0.25f))
        fruitPositions.add(floatArrayOf(0.5f, 0.24f, -0.08f))

        fruitPositions.add(floatArrayOf(-.4f, 0.16f, -.4f))
        fruitPositions.add(floatArrayOf(-.58f, 0.16f, -.4f))
        fruitPositions.add(floatArrayOf(-.5f, 0.16f, -.25f))

        fruitPositions.add(floatArrayOf(0.7f, 0.16f, -.55f))






        glass = Cylinder(context, 0.1f, 0.45f, floatArrayOf(1f, 1f, 1f, 0.5f)) // Полупрозрачный стакан
        water = Cylinder(context, 0.079f, 0.35f, floatArrayOf(0.2f, 0.5f, 1.0f, 0.7f)) // Вода


        water.position = floatArrayOf(0f, 0.2f, 0.2f)
        glass.position = floatArrayOf(0f, 0.2f, 0.2f)

        shaderCompiler = ShaderCompiler(vertexShaderCode, fragmentShaderCode)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        // Используем шейдер
        shaderCompiler.use()


        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glDepthFunc(GLES20.GL_LESS)

        table.draw(viewProjectionMatrix)
        drawFruits()


        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)

        GLES20.glDisable(GLES20.GL_DEPTH_TEST)

        glass.draw(viewProjectionMatrix, shaderCompiler)

        water.draw(viewProjectionMatrix, shaderCompiler)

        GLES20.glDisable(GLES20.GL_BLEND)

        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
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


