package com.example.cubik.table

import android.opengl.GLES20
import com.example.cubik.openGL.ShaderCompiler
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer


class Cube(private val textureId: Int) {
    private val vertexBuffer: FloatBuffer
    private val indexBuffer: ShortBuffer
    private val uvBuffer: FloatBuffer
    private val shaderCompiler: ShaderCompiler

    init {
        // Вершины куба
        val vertices = floatArrayOf(
            // Передняя грань
            -0.5f,  0.5f,  0.5f,
            -0.5f, -0.5f,  0.5f,
            0.5f, -0.5f,  0.5f,
            0.5f,  0.5f,  0.5f,
            // Задняя грань
            -0.5f,  0.5f, -0.5f,
            -0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
            0.5f,  0.5f, -0.5f
        )

        // UV-координаты для каждой грани
        val uvs = floatArrayOf(
            // Передняя грань (обычная текстура 1:1)
            0f, 0f,
            0f, 1f,
            1f, 1f,
            1f, 0f,

            // Задняя грань
            0f, 0f,
            0f, 1f,
            1f, 1f,
            1f, 0f,

            // Левая грань
            0f, 0f,
            0f, 1f,
            1f, 1f,
            1f, 0f,

            // Правая грань
            0f, 0f,
            0f, 1f,
            1f, 1f,
            1f, 0f,

            // Верхняя грань (текстура повторяется 2x2)
            0f, 0f,
            0f, 2f,
            2f, 2f,
            2f, 0f,

            // Нижняя грань (обычная текстура 1:1)
            0f, 0f,
            0f, 1f,
            1f, 1f,
            1f, 0f
        )

        // Индексы для отрисовки граней куба
        val indices = shortArrayOf(
            0, 1, 2, 0, 2, 3, // Передняя грань
            4, 5, 6, 4, 6, 7, // Задняя грань
            0, 4, 5, 0, 5, 1, // Левая грань
            3, 7, 6, 3, 6, 2, // Правая грань
            0, 3, 7, 0, 7, 4, // Верхняя грань
            1, 5, 6, 1, 6, 2  // Нижняя грань
        )

        // Конвертируем данные в буферы
        vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        vertexBuffer.put(vertices).position(0)

        uvBuffer = ByteBuffer.allocateDirect(uvs.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        uvBuffer.put(uvs).position(0)

        indexBuffer = ByteBuffer.allocateDirect(indices.size * 2)
            .order(ByteOrder.nativeOrder())
            .asShortBuffer()
        indexBuffer.put(indices).position(0)

        // Создаем шейдерную программу с помощью ShaderCompiler
        shaderCompiler = ShaderCompiler(vertexShaderCode, fragmentShaderCode)
    }

    fun draw(mvpMatrix: FloatArray) {
        shaderCompiler.use()

        // Передача данных в шейдеры
        val positionHandle = GLES20.glGetAttribLocation(shaderCompiler.programId, "aPosition")
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)

        val uvHandle = GLES20.glGetAttribLocation(shaderCompiler.programId, "aTexCoord")
        GLES20.glEnableVertexAttribArray(uvHandle)
        GLES20.glVertexAttribPointer(uvHandle, 2, GLES20.GL_FLOAT, false, 0, uvBuffer)

        val mvpMatrixHandle = GLES20.glGetUniformLocation(shaderCompiler.programId, "uMVPMatrix")
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

        val textureHandle = GLES20.glGetUniformLocation(shaderCompiler.programId, "uTexture")
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        GLES20.glUniform1i(textureHandle, 0)

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 36, GLES20.GL_UNSIGNED_SHORT, indexBuffer)

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(uvHandle)
    }

    companion object {
        private const val vertexShaderCode = """
            uniform mat4 uMVPMatrix;
            attribute vec4 aPosition;
            attribute vec2 aTexCoord;
            varying vec2 vTexCoord;
            void main() {
                gl_Position = uMVPMatrix * aPosition;
                vTexCoord = aTexCoord;
            }
        """

        private const val fragmentShaderCode = """
            precision mediump float;
            uniform sampler2D uTexture;
            varying vec2 vTexCoord;
            void main() {
                gl_FragColor = texture2D(uTexture, vTexCoord);
            }
        """
    }
}

