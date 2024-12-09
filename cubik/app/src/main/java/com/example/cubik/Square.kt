package com.example.cubik
import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class Square(private val context: Context) {
    private val vertexBuffer: FloatBuffer
    private val drawListBuffer: ShortBuffer
    private val textureBuffer: FloatBuffer
    private val program: Int
    private var texture: Int = 0

    private val coordsPerVertex = 3
    private val squareCoords = floatArrayOf(
        -1.0f, 1.0f, 0.0f,   // top left
        -1.0f, -1.0f, 0.0f,  // bottom left
        1.0f, -1.0f, 0.0f,   // bottom right
        1.0f, 1.0f, 0.0f     // top right
    )

    private val drawOrder = shortArrayOf(0, 1, 2, 0, 2, 3) // Порядок отрисовки вершин
    private val textureCoords = floatArrayOf(
        0.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 1.0f,
        1.0f, 0.0f
    )

    // Шейдеры
    private val vertexShaderCode = """
        attribute vec4 vPosition;
        attribute vec2 aTexCoord;
        varying vec2 vTexCoord;
        uniform mat4 uMVPMatrix;
        void main() {
          gl_Position = uMVPMatrix * vPosition;
          vTexCoord = aTexCoord;
        }
    """.trimIndent()

    private val fragmentShaderCode = """
        precision mediump float;
        uniform sampler2D uTexture;
        varying vec2 vTexCoord;
        void main() {
          gl_FragColor = texture2D(uTexture, vTexCoord);
        }
    """.trimIndent()

    init {
        // Буфер для вершин
        val bb = ByteBuffer.allocateDirect(squareCoords.size * 4)
        bb.order(ByteOrder.nativeOrder())
        vertexBuffer = bb.asFloatBuffer()
        vertexBuffer.put(squareCoords)
        vertexBuffer.position(0)

        // Буфер для порядка отрисовки
        val dlb = ByteBuffer.allocateDirect(drawOrder.size * 2)
        dlb.order(ByteOrder.nativeOrder())
        drawListBuffer = dlb.asShortBuffer()
        drawListBuffer.put(drawOrder)
        drawListBuffer.position(0)

        // Буфер для текстуры
        val tb = ByteBuffer.allocateDirect(textureCoords.size * 4)
        tb.order(ByteOrder.nativeOrder())
        textureBuffer = tb.asFloatBuffer()
        textureBuffer.put(textureCoords)
        textureBuffer.position(0)

        // Компиляция шейдеров
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        program = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }

        // Загрузка текстуры
        texture = loadTexture(context)
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        return GLES20.glCreateShader(type).also { shader ->
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }

    private fun loadTexture(context: Context): Int {
        val textureHandle = IntArray(1)
        GLES20.glGenTextures(1, textureHandle, 0)

        if (textureHandle[0] != 0) {
            val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.texture) // Укажи имя своей тWWекстуры
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0])
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
            bitmap.recycle()
        }
        return textureHandle[0]
    }

    fun draw(mvpMatrix: FloatArray) {
        GLES20.glUseProgram(program)

        // Передача координат вершин
        val positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        vertexBuffer.position(0)
        GLES20.glVertexAttribPointer(positionHandle, coordsPerVertex, GLES20.GL_FLOAT, false, 0, vertexBuffer)
        GLES20.glEnableVertexAttribArray(positionHandle)

        // Передача координат текстуры
        val textureHandle = GLES20.glGetAttribLocation(program, "aTexCoord")
        textureBuffer.position(0)
        GLES20.glVertexAttribPointer(textureHandle, 2, GLES20.GL_FLOAT, false, 0, textureBuffer)
        GLES20.glEnableVertexAttribArray(textureHandle)

        // Передача матрицы MVP
        val mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

        // Устанавливаем текстуру
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture)

        // Отрисовка квадрата
        drawListBuffer.position(0)
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.size, GLES20.GL_UNSIGNED_SHORT, drawListBuffer)

        // Отключаем атрибуты
        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(textureHandle)
    }
}