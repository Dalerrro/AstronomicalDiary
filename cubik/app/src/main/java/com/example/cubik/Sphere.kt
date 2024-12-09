package com.example.cubik

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class Sphere(private val context: Context, private val radius: Float, private val textureResourceId: Int) {

    private val vertexBuffer: FloatBuffer
    private val texCoordBuffer: FloatBuffer
    private val indexBuffer: ShortBuffer
    private var position = floatArrayOf(0f, 0f, 0f)
    private var program = -1
    private var textureHandle = -1

    init {
        // Генерация вершин, индексов и UV-координат
        val (vertices, indices, texCoords) = generateSphere(radius, 30, 30)
        vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(vertices)
        vertexBuffer.position(0)

        texCoordBuffer = ByteBuffer.allocateDirect(texCoords.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(texCoords)
        texCoordBuffer.position(0)

        indexBuffer = ByteBuffer.allocateDirect(indices.size * 2)
            .order(ByteOrder.nativeOrder())
            .asShortBuffer()
            .put(indices)
        indexBuffer.position(0)

        // Загрузка текстуры
        textureHandle = loadTexture(context, textureResourceId)

        // Компиляция шейдеров
        program = createProgram()
    }

    fun setPosition(newPosition: FloatArray) {
        position = newPosition
    }

    fun draw(mvpMatrix: FloatArray) {
        GLES20.glUseProgram(program)

        // Получение дескрипторов шейдеров
        val mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        val textureHandleLocation = GLES20.glGetUniformLocation(program, "uTexture")
        val positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        val texCoordHandle = GLES20.glGetAttribLocation(program, "aTexCoord")

        // Передача матрицы и текстуры
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle)
        GLES20.glUniform1i(textureHandleLocation, 0)

        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)

        GLES20.glEnableVertexAttribArray(texCoordHandle)
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 0, texCoordBuffer)

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexBuffer.capacity(), GLES20.GL_UNSIGNED_SHORT, indexBuffer)

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(texCoordHandle)
    }

    private fun createProgram(): Int {
        val vertexShaderCode = """
            uniform mat4 uMVPMatrix;
            attribute vec4 vPosition;
            attribute vec2 aTexCoord;
            varying vec2 vTexCoord;
            void main() {
                vTexCoord = aTexCoord;
                gl_Position = uMVPMatrix * vPosition;
            }
        """.trimIndent()

        val fragmentShaderCode = """
            precision mediump float;
            varying vec2 vTexCoord;
            uniform sampler2D uTexture;
            void main() {
                gl_FragColor = texture2D(uTexture, vTexCoord);
            }
        """.trimIndent()

        val vertexShader = compileShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        val program = GLES20.glCreateProgram()
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)

        return program
    }

    private fun compileShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)

        val compileStatus = IntArray(1)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0)
        if (compileStatus[0] == 0) {
            GLES20.glDeleteShader(shader)
            throw RuntimeException("Error compiling shader: ${GLES20.glGetShaderInfoLog(shader)}")
        }

        return shader
    }

    private fun loadTexture(context: Context, resourceId: Int): Int {
        val options = BitmapFactory.Options()
        options.inScaled = false

        val bitmap = BitmapFactory.decodeResource(context.resources, resourceId, options) ?: return 0

        val textureHandle = IntArray(1)
        GLES20.glGenTextures(1, textureHandle, 0)

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0])
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)

        bitmap.recycle()
        return textureHandle[0]
    }

    private fun generateSphere(radius: Float, latitudeBands: Int, longitudeBands: Int): Triple<FloatArray, ShortArray, FloatArray> {
        val vertices = mutableListOf<Float>()
        val texCoords = mutableListOf<Float>()
        val indices = mutableListOf<Short>()

        for (lat in 0..latitudeBands) {
            val theta = lat * Math.PI / latitudeBands
            val sinTheta = Math.sin(theta).toFloat()
            val cosTheta = Math.cos(theta).toFloat()

            for (lon in 0..longitudeBands) {
                val phi = lon * 2 * Math.PI / longitudeBands
                val sinPhi = Math.sin(phi).toFloat()
                val cosPhi = Math.cos(phi).toFloat()

                val x = cosPhi * sinTheta
                val y = cosTheta
                val z = sinPhi * sinTheta

                vertices.add(x * radius)
                vertices.add(y * radius)
                vertices.add(z * radius)

                texCoords.add(lon / longitudeBands.toFloat())
                texCoords.add(lat / latitudeBands.toFloat())
            }
        }

        for (lat in 0 until latitudeBands) {
            for (lon in 0 until longitudeBands) {
                val first = (lat * (longitudeBands + 1) + lon).toShort()
                indices.add(first)
                indices.add((first + 1).toShort())
                indices.add((first + longitudeBands + 1).toShort())

                indices.add((first + 1).toShort())
                indices.add((first + longitudeBands + 2).toShort())
                indices.add((first + longitudeBands + 1).toShort())
            }
        }

        return Triple(vertices.toFloatArray(), indices.toShortArray(), texCoords.toFloatArray())
    }
}
