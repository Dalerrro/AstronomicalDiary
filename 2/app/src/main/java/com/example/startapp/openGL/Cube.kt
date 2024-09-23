package com.example.startapp.openGL

import android.opengl.GLES20
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class Cube {


    private val cubeCoords = floatArrayOf(

        -0.5f, -0.5f, 0.5f,  // 0
        0.5f, -0.5f, 0.5f,   // 1
        0.5f, 0.5f, 0.5f,    // 2
        -0.5f, 0.5f, 0.5f,   // 3

        -0.5f, -0.5f, -0.5f, // 4
        0.5f, -0.5f, -0.5f,  // 5
        0.5f, 0.5f, -0.5f,   // 6
        -0.5f, 0.5f, -0.5f   // 7
    )


    private val drawOrder = shortArrayOf(
        0, 1, 2, 0, 2, 3,  // передняя грань
        4, 5, 6, 4, 6, 7,  // задняя грань
        0, 1, 5, 0, 5, 4,  // нижняя грань
        3, 2, 6, 3, 6, 7,  // верхняя грань
        0, 3, 7, 0, 7, 4,  // левая грань
        1, 2, 6, 1, 6, 5   // правая грань
    )


    private val color = floatArrayOf(0.1f, 0.1f, 0.2f, 1.0f) // темный сине-фиолетовый

    // Матрицы и угол вращения
    private val rotationMatrix = FloatArray(16)
    private var angle = 0.0f

    // Буферы
    private val vertexBuffer: FloatBuffer
    private val indexBuffer: ShortBuffer

    // Шейдеры
    private val vertexShaderCode = """
        uniform mat4 uMVPMatrix;
        attribute vec4 vPosition;
        void main() {
            gl_Position = uMVPMatrix * vPosition;
        }
        """.trimIndent()

    private val fragmentShaderCode = """
        precision mediump float;
        uniform vec4 vColor;
        void main() {
            gl_FragColor = vColor;
        }
        """.trimIndent()

    private val program: Int

    init {

        val bb = ByteBuffer.allocateDirect(cubeCoords.size * 4)
        bb.order(ByteOrder.nativeOrder())
        vertexBuffer = bb.asFloatBuffer()
        vertexBuffer.put(cubeCoords)
        vertexBuffer.position(0)

        val ib = ByteBuffer.allocateDirect(drawOrder.size * 2)
        ib.order(ByteOrder.nativeOrder())
        indexBuffer = ib.asShortBuffer()
        indexBuffer.put(drawOrder)
        indexBuffer.position(0)

        // Компиляция шейдеров
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        // Создание программы и связывание шейдеров
        program = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }

        Matrix.setRotateM(rotationMatrix, 0, 0f, 0f, 1f, 0f)
    }

    fun draw(mvpMatrix: FloatArray) {
        // Используем программу
        GLES20.glUseProgram(program)


        val positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 12, vertexBuffer)


        val colorHandle = GLES20.glGetUniformLocation(program, "vColor")
        GLES20.glUniform4fv(colorHandle, 1, color, 0)


        angle += 0.5f
        Matrix.setRotateM(rotationMatrix, 0, angle, 0.5f, 1f, 0.3f)
        val modelMatrix = FloatArray(16)
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.scaleM(modelMatrix, 0, 0.5f, 0.5f, 0.5f) // Уменьшаем куб
        val scratch = FloatArray(16)
        Matrix.multiplyMM(scratch, 0, mvpMatrix, 0, rotationMatrix, 0)
        Matrix.multiplyMM(scratch, 0, scratch, 0, modelMatrix, 0)


        val mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, scratch, 0)


        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.size, GLES20.GL_UNSIGNED_SHORT, indexBuffer)

        
        GLES20.glDisableVertexAttribArray(positionHandle)
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        return GLES20.glCreateShader(type).also { shader ->
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }
}
