package com.example.cubik

import android.content.Context
import android.opengl.GLES20
import android.opengl.Matrix
import com.example.cubik.openGL.ShaderCompiler
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class Cylinder(
    private val context: Context,
    private val radius: Float,
    private val height: Float,
    private val color: FloatArray, // Цвет с альфа-каналом
    private val alpha: Float = 0.4f // Альфа-канал для прозрачности
) {
    var position = floatArrayOf(0f, 0f, 0f)  // Позиция стакана в 3D пространстве

    private val vertices: FloatArray
    private val indices: ShortArray
    private val vertexCount: Int
    private val vertexBuffer: FloatBuffer
    private val indexBuffer: ShortBuffer

    init {
        val segments = 36
        val vertexList = mutableListOf<Float>()
        val indexList = mutableListOf<Short>()

        // Top circle
        for (i in 0..segments) {
            val angle = 2 * Math.PI * i / segments
            vertexList.add((radius * Math.cos(angle)).toFloat()) // x
            vertexList.add(height / 2) // y
            vertexList.add((radius * Math.sin(angle)).toFloat()) // z
        }

        // Bottom circle
        for (i in 0..segments) {
            val angle = 2 * Math.PI * i / segments
            vertexList.add((radius * Math.cos(angle)).toFloat()) // x
            vertexList.add(-height / 2) // y
            vertexList.add((radius * Math.sin(angle)).toFloat()) // z
        }

        // Indices for the sides
        for (i in 0 until segments) {
            indexList.add(i.toShort())
            indexList.add((i + segments + 1).toShort())
            indexList.add((i + 1).toShort())

            indexList.add((i + 1).toShort())
            indexList.add((i + segments + 1).toShort())
            indexList.add((i + segments + 2).toShort())
        }

        vertices = vertexList.toFloatArray()
        indices = indexList.toShortArray()
        vertexCount = vertices.size / 3

        // Initialize vertex buffer
        val bb = ByteBuffer.allocateDirect(vertices.size * 4)
        bb.order(ByteOrder.nativeOrder())
        vertexBuffer = bb.asFloatBuffer()
        vertexBuffer.put(vertices)
        vertexBuffer.position(0)

        // Initialize index buffer
        val ib = ByteBuffer.allocateDirect(indices.size * 2)
        ib.order(ByteOrder.nativeOrder())
        indexBuffer = ib.asShortBuffer()
        indexBuffer.put(indices)
        indexBuffer.position(0)
    }

    fun draw(mvpMatrix: FloatArray, shaderCompiler: ShaderCompiler) {
        // Убедитесь, что шейдерная программа активна
        shaderCompiler.use()

        // Применяем позицию объекта
        val modelMatrix = FloatArray(16)
        Matrix.setIdentityM(modelMatrix, 0)  // Инициализация матрицы

        // Применяем перемещение
        Matrix.translateM(modelMatrix, 0, position[0], position[1], position[2])  // Перемещаем объект

        // Применяем модельную матрицу
        val finalMatrix = FloatArray(16)
        Matrix.multiplyMM(finalMatrix, 0, mvpMatrix, 0, modelMatrix, 0)

        // Устанавливаем цвет с альфа-каналом
        val uColorLocation = shaderCompiler.getUniformLocation("uColor")
        GLES20.glUniform4fv(uColorLocation, 1, color, 0)

        // Устанавливаем прозрачность
        val uAlphaLocation = shaderCompiler.getUniformLocation("uAlpha")
        GLES20.glUniform1f(uAlphaLocation, alpha)

        val uMVPMatrixLocation = shaderCompiler.getUniformLocation("uMVPMatrix")
        GLES20.glUniformMatrix4fv(uMVPMatrixLocation, 1, false, finalMatrix, 0)

        GLES20.glEnableVertexAttribArray(shaderCompiler.getAttribLocation("vPosition"))
        GLES20.glVertexAttribPointer(
            shaderCompiler.getAttribLocation("vPosition"),
            3, GLES20.GL_FLOAT, false,
            3 * 4, vertexBuffer
        )

        // Включаем смешивание для прозрачности
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)

        GLES20.glDrawElements(
            GLES20.GL_TRIANGLES,
            indices.size,
            GLES20.GL_UNSIGNED_SHORT,
            indexBuffer
        )

        // Отключаем смешивание
        GLES20.glDisable(GLES20.GL_BLEND)

        GLES20.glDisableVertexAttribArray(shaderCompiler.getAttribLocation("vPosition"))
    }
}
