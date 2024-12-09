package com.example.startapp.openGL

import BlackHole
import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.example.startapp.R
import com.example.startapp.openGL.solarSystem.Cube
import com.example.startapp.openGL.solarSystem.Moon
import com.example.startapp.openGL.solarSystem.Planet
import com.example.startapp.openGL.solarSystem.Square
import com.example.startapp.openGL.solarSystem.Sun
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.random.Random


class MyGLRenderer(private val context: Context) : GLSurfaceView.Renderer {
    private var width = 0
    private var height = 0
    private lateinit var square: Square
    private lateinit var sun: Sun
    private lateinit var moon: Moon
    private lateinit var planets: List<Planet>
    private lateinit var cube: Cube


    private lateinit var blackHole: BlackHole
    private var blackHolePosition = -15f
    private var blackHoleSpeed = 0.01f

    private var startPos = -10f
    private var endPos = 10f


    private var selectedPlanetIndex = 0

    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)



    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        GLES20.glClearColor(0f, 0f, 0f, 1f)

        square = Square(context)
        cube = Cube()
        sun = Sun(context, 0.6f, R.drawable.sun)

        planets = listOf(
            Planet(context, 0.15f ,R.drawable.mercury, 1.0f, 1.1f, 0.1f),
            Planet(context, 0.19f, R.drawable.venus, 1.7f, 0.5f, 0.1f),
            Planet(context, 0.2f, R.drawable.earth, 2.4f, 0.4f, 3f),
            Planet(context, 0.18f, R.drawable.mars, 3.3f, 0.3f, 3f),
            Planet(context, 0.4f, R.drawable.jupiter, 4.8f, 0.22f, 2f),
            Planet(context, 0.3f, R.drawable.saturn, 6f, 0.15f, 2f),
            Planet(context, 0.28f, R.drawable.uranus, 7f, 0.12f, 2f),
            Planet(context, 0.28f, R.drawable.neptune, 8f, 0.08f, 2f),
        )

        moon = Moon(context, .13f, R.drawable.moon, planets[2], .5f, 7f)

        blackHole = BlackHole(context, R.drawable.tophole)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        GLES20.glDisable(GLES20.GL_DEPTH_TEST)

        square.draw()
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)

        sun.draw(mvpMatrix)
        planets.forEach { it.draw(mvpMatrix) }
        moon.draw(mvpMatrix)

        blackHolePosition += blackHoleSpeed
        if (blackHolePosition < -15f) {
            blackHolePosition = 15f
        }


        val objectRadius : Float
        val objectPosition : FloatArray
        if (selectedPlanetIndex < 8){
            objectPosition = planets[selectedPlanetIndex].getPosition()
            objectRadius = planets[selectedPlanetIndex].radius + 0.03f
        } else if (selectedPlanetIndex == 8){
            objectPosition = moon.getPosition()
            objectRadius = moon.radius

        } else {
            objectPosition = floatArrayOf(0f,0f,0f)
            objectRadius = sun.radius
        }
        cube.draw(mvpMatrix, objectPosition, objectRadius)

        blackHole.draw(mvpMatrix, blackHolePosition)

        blackHole.setScale(1.5f)


    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        this.height = height
        this.width = width
        GLES20.glViewport(0, 0, width, height)

        val ratio: Float = width.toFloat() / height.toFloat()

        Matrix.setLookAtM(viewMatrix, 0, 0f,  3.5f, -12.5f, 0f, 0f, 0f, 0f, 1f, 0f)

        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 1f, 50f)
    }

    fun setSelectedObjectIndex(index: Int) {
        selectedPlanetIndex = index
    }

    val vertexShaderCode = """
    uniform mat4 uMVPMatrix;
    attribute vec4 aPosition;
    void main() {
        gl_Position = uMVPMatrix * aPosition;
    }
""".trimIndent()

    val fragmentShaderCode = """
    precision mediump float;
    uniform vec4 uColor;
    void main() {
        gl_FragColor = uColor;
    }
""".trimIndent()
}