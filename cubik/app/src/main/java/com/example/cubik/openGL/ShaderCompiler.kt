// ShaderCompiler.kt
package com.example.cubik.openGL

import android.opengl.GLES20
import java.nio.FloatBuffer

class ShaderCompiler(vertexShaderCode: String, fragmentShaderCode: String) {
    var programId: Int

    init {
        val vertexShader = compileShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        programId = GLES20.glCreateProgram()
        if (programId == 0) {
            throw RuntimeException("Failed to create OpenGL program")
        }

        GLES20.glAttachShader(programId, vertexShader)
        GLES20.glAttachShader(programId, fragmentShader)
        GLES20.glLinkProgram(programId)

        val linkStatus = IntArray(1)
        GLES20.glGetProgramiv(programId, GLES20.GL_LINK_STATUS, linkStatus, 0)
        if (linkStatus[0] == 0) {
            val errorLog = GLES20.glGetProgramInfoLog(programId)
            GLES20.glDeleteProgram(programId)
            GLES20.glDeleteShader(vertexShader)
            GLES20.glDeleteShader(fragmentShader)
            throw RuntimeException("Error linking program: $errorLog")
        }

        GLES20.glDeleteShader(vertexShader)
        GLES20.glDeleteShader(fragmentShader)
    }

    fun use() {
        GLES20.glUseProgram(programId)
    }

    fun getAttribLocation(name: String): Int {
        return GLES20.glGetAttribLocation(programId, name).also {
            if (it == -1) throw RuntimeException("Attribute not found: $name")
        }
    }

    fun getUniformLocation(name: String): Int {
        return GLES20.glGetUniformLocation(programId, name).also {
            if (it == -1) throw RuntimeException("Uniform not found: $name")
        }
    }

    private fun compileShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)

        val compileStatus = IntArray(1)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0)
        if (compileStatus[0] == 0) {
            val errorLog = GLES20.glGetShaderInfoLog(shader)
            GLES20.glDeleteShader(shader)
            throw RuntimeException("Shader compilation failed: $errorLog")
        }
        return shader
    }
}

val vertexShaderCode = """
uniform mat4 uMVPMatrix;
uniform mat4 uModelMatrix;
attribute vec4 vPosition;
attribute vec3 vNormal;

varying vec3 vNormalInterp;
varying vec3 vPositionInterp;

void main() {
    vNormalInterp = mat3(uModelMatrix) * vNormal;
    vPositionInterp = vec3(uModelMatrix * vPosition);
    gl_Position = uMVPMatrix * vPosition;
}
""".trimIndent()

val fragmentShaderCode = """
precision mediump float;
uniform vec4 uColor;
uniform float uAlpha;

void main() {
    gl_FragColor = vec4(uColor.rgb, uAlpha); // Применяем альфа-канал
}

""".trimIndent();
