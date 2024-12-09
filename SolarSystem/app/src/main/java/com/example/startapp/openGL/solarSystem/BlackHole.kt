import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLUtils
import android.opengl.Matrix
import com.example.startapp.openGL.renderGL.ShaderCompiler
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class BlackHole(context: Context, private val textureResId: Int) {
    private var vertexBuffer: FloatBuffer
    private var textureBuffer: FloatBuffer
    private var textureId: Int

    private var scale = 1.0f

    private val numSegments = 64 // Количество сегментов круга
    private val radius = 1f // Радиус круга

    private var shaderProgram: ShaderCompiler

    init {
        val vertices = generateCircleVertices(radius, numSegments)
        val textureCoords = generateCircleTextureCoords(numSegments)

        vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(vertices)
                position(0)
            }
        }

        textureBuffer = ByteBuffer.allocateDirect(textureCoords.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(textureCoords)
                position(0)
            }
        }

        val vertexShaderCode = """
            uniform mat4 u_MVPMatrix;
            attribute vec4 a_Position;
            attribute vec2 a_TexCoord;
            
            varying vec2 v_TexCoord;
            
            void main() {
                v_TexCoord = a_TexCoord;
                gl_Position = u_MVPMatrix * a_Position;
            }
        """.trimIndent()

        val fragmentShaderCode = """
            precision mediump float;
           
            uniform sampler2D u_Texture;
            varying vec2 v_TexCoord;
            
            void main() {
                vec4 textureColor = texture2D(u_Texture, v_TexCoord);
                if (textureColor.a < 0.1) {
                    discard;
                }
                gl_FragColor = textureColor;
            }
        """.trimIndent()

        shaderProgram = ShaderCompiler(vertexShaderCode, fragmentShaderCode)
        textureId = loadTexture(context, textureResId)
    }

    private fun generateCircleVertices(radius: Float, numSegments: Int): FloatArray {
        val vertices = mutableListOf<Float>()

        // Центральная точка круга
        vertices.add(0f) // x
        vertices.add(0f) // y
        vertices.add(0f) // z

        // Точки по окружности
        for (i in 0..numSegments) {
            val angle = 2.0 * Math.PI * i / numSegments
            vertices.add((radius * Math.cos(angle)).toFloat()) // x
            vertices.add((radius * Math.sin(angle)).toFloat()) // y
            vertices.add(0f) // z
        }

        return vertices.toFloatArray()
    }

    private fun generateCircleTextureCoords(numSegments: Int): FloatArray {
        val texCoords = mutableListOf<Float>()

        // Центральная точка текстуры
        texCoords.add(0.5f) // u
        texCoords.add(0.5f) // v

        // Точки по окружности
        for (i in 0..numSegments) {
            val angle = 2.0 * Math.PI * i / numSegments
            texCoords.add((0.5 + 0.5 * Math.cos(angle)).toFloat()) // u
            texCoords.add((0.5 + 0.5 * Math.sin(angle)).toFloat()) // v
        }

        return texCoords.toFloatArray()
    }

    private fun loadTexture(context: Context, resId: Int): Int {
        val textureHandle = IntArray(1)
        GLES20.glGenTextures(1, textureHandle, 0)

        if (textureHandle[0] != 0) {
            val options = BitmapFactory.Options()
            options.inScaled = false
            val bitmap = BitmapFactory.decodeResource(context.resources, resId, options)

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0])
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST)

            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
            bitmap.recycle()
        }

        return textureHandle[0]
    }

    fun setScale(newScale: Float) {
        scale = newScale
    }

    fun draw(mvpMatrix: FloatArray, tr: Float) {
        val modelMatrix = FloatArray(16)
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, tr, -tr + 1f, 10f)
        Matrix.scaleM(modelMatrix, 0, scale, scale, scale)

        val finalMatrix = FloatArray(16)
        Matrix.multiplyMM(finalMatrix, 0, mvpMatrix, 0, modelMatrix, 0)
        shaderProgram.use()

        val positionHandle = GLES20.glGetAttribLocation(shaderProgram.programId, "a_Position")
        val texCoordHandle = GLES20.glGetAttribLocation(shaderProgram.programId, "a_TexCoord")
        val mvpMatrixHandle = GLES20.glGetUniformLocation(shaderProgram.programId, "u_MVPMatrix")

        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)

        GLES20.glEnableVertexAttribArray(texCoordHandle)
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 0, textureBuffer)

        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, finalMatrix, 0)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, numSegments + 2)

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(texCoordHandle)
    }

}
