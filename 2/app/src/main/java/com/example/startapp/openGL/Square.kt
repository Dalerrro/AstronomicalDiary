package com.example.startapp.openGL

import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLUtils
import android.content.Context
import android.util.Log
import com.example.startapp.R
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class Square(private val context: Context) {

    private val vertexBuffer: FloatBuffer
    private val texCoordBuffer: FloatBuffer

    // Координаты вершин квадрата (весь экран)
    private val squareCoords = floatArrayOf(
        -1.0f, 1.0f, 0.0f,
        -1.0f, -1.0f, 0.0f,
        1.0f, -1.0f, 0.0f,
        1.0f, 1.0f, 0.0f
    )

    // Координаты текстуры
    private val texCoords = floatArrayOf(
        0.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 1.0f,
        1.0f, 0.0f
    )

    private val vertexShaderCode = """
        attribute vec4 vPosition; 
        attribute vec2 aTexCoord; 
        varying vec2 vTexCoord;    // Передача текстурных координат во фрагментный шейдер

          void main() {
              gl_Position = vPosition; 
              vTexCoord = aTexCoord;  
            }
            """.trimIndent()

    private val fragmentShaderCode = """
        precision mediump float; 
        varying vec2 vTexCoord;  
        uniform sampler2D uTexture; 

         void main() {
                // Получаем цвет из текстуры по заданным координатам
                gl_FragColor = texture2D(uTexture, vTexCoord);
         }
       """.trimIndent()

    private val program: Int
    private val textureHandle = IntArray(1)

    init {
        // Инициализация буфера вершин
        val bb = ByteBuffer.allocateDirect(squareCoords.size * 4)
        bb.order(ByteOrder.nativeOrder())
        vertexBuffer = bb.asFloatBuffer()
        vertexBuffer.put(squareCoords)
        vertexBuffer.position(0)

        // Инициализация буфера текстурных координат
        val tb = ByteBuffer.allocateDirect(texCoords.size * 4)
        tb.order(ByteOrder.nativeOrder())
        texCoordBuffer = tb.asFloatBuffer()
        texCoordBuffer.put(texCoords)
        texCoordBuffer.position(0)

        // Компиляция шейдеров
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        // Создание программы и связывание шейдеров
        program = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }

        // Загрузка текстуры
        loadTexture()
    }

    private fun loadTexture() {
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.texture)
            ?: throw RuntimeException("Error loading texture.")


        GLES20.glGenTextures(1, textureHandle, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0])


        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)


        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
        bitmap.recycle() // Освобождаем память, так как данные уже загружены в OpenGL


        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D)

        Log.d("Square", "Texture loaded: ${bitmap.width} x ${bitmap.height}")
    }

    fun draw(mvpMatrix: FloatArray) {
        GLES20.glUseProgram(program)

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0])

        val positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 12, vertexBuffer)

        val texCoordHandle = GLES20.glGetAttribLocation(program, "aTexCoord")
        GLES20.glEnableVertexAttribArray(texCoordHandle)
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 8, texCoordBuffer)

        val textureUniformHandle = GLES20.glGetUniformLocation(program, "uTexture")
        GLES20.glUniform1i(textureUniformHandle, 0)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4)

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(texCoordHandle)
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        return GLES20.glCreateShader(type).also { shader ->
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }
}
