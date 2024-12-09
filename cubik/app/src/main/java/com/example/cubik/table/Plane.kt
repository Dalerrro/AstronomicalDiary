package com.example.cubik.table

import android.opengl.GLES20
import com.example.cubik.openGL.ShaderCompiler
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class Plane(private val textureId: Int, private val repeatX: Float, private val repeatY: Float) {
    private val vertexBuffer: FloatBuffer
    private val uvBuffer: FloatBuffer
    private val shader: ShaderCompiler

    init {

        val vertices = floatArrayOf(
            -0.5f, 0.5f, 0f,
            -0.5f, -0.5f, 0f,
            0.5f, -0.5f, 0f,
            0.5f, 0.5f, 0f
        )


        val uvs = floatArrayOf(
            0f, 0f,
            0f, repeatY,
            repeatX, repeatY,
            repeatX, 0f
        )


        vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        vertexBuffer.put(vertices).position(0)

        uvBuffer = ByteBuffer.allocateDirect(uvs.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        uvBuffer.put(uvs).position(0)

        // Компиляция шейдеров
        shader = ShaderCompiler(vertexShaderCode, fragmentShaderCode)
    }

    fun draw(mvpMatrix: FloatArray) {
        shader.use()

        val positionHandle = GLES20.glGetAttribLocation(shader.programId, "aPosition")
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)

        val uvHandle = GLES20.glGetAttribLocation(shader.programId, "aTexCoord")
        GLES20.glEnableVertexAttribArray(uvHandle)
        GLES20.glVertexAttribPointer(uvHandle, 2, GLES20.GL_FLOAT, false, 0, uvBuffer)

        val mvpMatrixHandle = GLES20.glGetUniformLocation(shader.programId, "uMVPMatrix")
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

        val textureHandle = GLES20.glGetUniformLocation(shader.programId, "uTexture")
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        GLES20.glUniform1i(textureHandle, 0)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4)

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
