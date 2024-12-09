package com.example.cubik

import android.content.Context
import android.opengl.GLES20
import android.opengl.Matrix
import com.example.cubik.openGL.ShaderCompiler
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class Apple(context: Context, radius: Float, textureResId: Int) {

    private val vertices: FloatBuffer
    private val normals: FloatBuffer
    private val indices: ShortBuffer
    private val vertexCount: Int
    private val textureResId = textureResId
    private var textureId: Int = 0

    init {

        val appleVertices = mutableListOf<Float>()
        val appleNormals = mutableListOf<Float>()
        val appleIndices = mutableListOf<Short>()

        val latSegments = 30
        val longSegments = 30
        for (i in 0..latSegments) {
            val theta = i * Math.PI / latSegments
            for (j in 0..longSegments) {
                val phi = j * 2 * Math.PI / longSegments

                // Изменение радиуса для создания формы яблока
                val x = (radius * (1 + 0.1 * Math.sin(theta)) * Math.cos(phi)).toFloat()
                val y = (radius * (1 + 0.1 * Math.sin(theta)) * Math.sin(phi)).toFloat()
                val z = (radius * (1 + 0.1 * Math.cos(theta))).toFloat()

                appleVertices.add(x)
                appleVertices.add(y)
                appleVertices.add(z)

                appleNormals.add(x)
                appleNormals.add(y)
                appleNormals.add(z)
            }
        }

        for (i in 0 until latSegments) {
            for (j in 0 until longSegments) {
                val first = (i * (longSegments + 1) + j).toShort()
                val second = first + 1
                val third = first + (longSegments + 1).toShort()
                val fourth = third + 1

                appleIndices.add(first)
                appleIndices.add(second.toShort())
                appleIndices.add(third.toShort())
                appleIndices.add(second.toShort())
                appleIndices.add(fourth.toShort())
                appleIndices.add(third.toShort())
            }
        }

        vertexCount = appleIndices.size

        val vertexBuffer = ByteBuffer.allocateDirect(appleVertices.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        vertexBuffer.put(appleVertices.toFloatArray())
        vertexBuffer.position(0)

        val normalBuffer = ByteBuffer.allocateDirect(appleNormals.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        normalBuffer.put(appleNormals.toFloatArray())
        normalBuffer.position(0)

        val indexBuffer = ByteBuffer.allocateDirect(appleIndices.size * 2)
            .order(ByteOrder.nativeOrder()).asShortBuffer()
        indexBuffer.put(appleIndices.toShortArray())
        indexBuffer.position(0)

        vertices = vertexBuffer
        normals = normalBuffer
        indices = indexBuffer
    }

    fun draw(shader: ShaderCompiler) {
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)

        val positionHandle = shader.getAttribLocation("vPosition")
        val normalHandle = shader.getAttribLocation("vNormal")

        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glEnableVertexAttribArray(normalHandle)

        GLES20.glVertexAttribPointer(
            positionHandle,
            3,
            GLES20.GL_FLOAT,
            false,
            3 * 4,
            vertices
        )
        GLES20.glVertexAttribPointer(
            normalHandle,
            3,
            GLES20.GL_FLOAT,
            false,
            3 * 4,
            normals
        )

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount)

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(normalHandle)
    }
}
