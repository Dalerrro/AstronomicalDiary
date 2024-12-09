package com.example.startapp.openGL.renderGL

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.example.startapp.R
import com.example.startapp.openGL.solarSystem.InfoScreenObject
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class InfoScreenRenderer(private val context: Context, private val selectedPlanetIndex: Int) : GLSurfaceView.Renderer {

    private lateinit var celestiaObject: InfoScreenObject
    private val viewMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)

    private val lightPosition = floatArrayOf(2.0f, 2.0f, 2.0f)

    private val ambientColor = floatArrayOf(0.3f, 0.3f, 0.3f, 1.0f)  // Окружающий свет
    private val diffuseColor = floatArrayOf(0.8f, 0.8f, 0.8f, 1.0f)  // Диффузное освещение
    private val specularColor = floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f) // Зеркальное освещение
    private val shininess = 32.0f                                     // Блеск

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0f, 0f, 0f, 1f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)

        val textureResId = getObjectTexture(selectedPlanetIndex)
        celestiaObject = InfoScreenObject(
            context,
            radius = 1.0f,
            lightPosition = lightPosition,
            ambientColor = ambientColor,
            diffuseColor = diffuseColor,
            specularColor = specularColor,
            shininess = shininess,
            textureResId = textureResId
        )

        if (selectedPlanetIndex == 8) {
            celestiaObject.shaderCompiler = ShaderCompiler(VERTEX_SHADER_CODE_PHONG, FRAGMENT_SHADER_CODE_PHONG)
        } else {
            celestiaObject.shaderCompiler = ShaderCompiler(BASIC_VERTEX_SHADER_CODE, BASIC_FRAGMENT_SHADER_CODE)
        }

    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 5f, 0f, 0.5f, 0f, 0f, 1f, 0f)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.rotateM(modelMatrix, 0, 20f, 0f, 1f, 0f)

        celestiaObject.draw(mvpMatrix, modelMatrix, viewMatrix)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio: Float = width.toFloat() / height.toFloat()
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 2f, 7f)
    }

    fun getObjectTexture(index: Int): Int {
        return when (index) {
            0 -> R.drawable.mercury8k
            1 -> R.drawable.venus_atmosphere4k
            2 -> R.drawable.earth_daymap8k
            3 -> R.drawable.mars8k
            4 -> R.drawable.jupiter8k
            5 -> R.drawable.saturn8k
            6 -> R.drawable.uranus2k
            7 -> R.drawable.neptune2k
            8 -> R.drawable.moon8k
            9 -> R.drawable.sun8k
            else -> R.drawable.milky_way
        }
    }

    companion object {
        private const val VERTEX_SHADER_CODE_PHONG = """
            uniform mat4 u_MVPMatrix;
            uniform mat4 u_ModelMatrix;
            uniform mat4 u_ViewMatrix;
            
            attribute vec3 a_Position;
            attribute vec3 a_Normal;
            attribute vec2 a_TexCoord;
            
            varying vec3 v_Normal;
            varying vec3 v_FragPos;
            varying vec2 v_TexCoord;
            
            void main() {
                v_FragPos = vec3(u_ModelMatrix * vec4(a_Position, 1.0));
                v_Normal = normalize(vec3(u_ModelMatrix * vec4(a_Normal, 0.0)));
                v_TexCoord = a_TexCoord;
                gl_Position = u_MVPMatrix * vec4(a_Position, 1.0);
            }
        """

        private const val FRAGMENT_SHADER_CODE_PHONG = """
            precision mediump float;

            uniform vec3 u_LightPos;
            uniform vec4 u_AmbientColor;
            uniform vec4 u_DiffuseColor;
            uniform vec4 u_SpecularColor;
            uniform float u_Shininess;
            
            uniform sampler2D u_Texture; 
            varying vec3 v_Normal;
            varying vec3 v_FragPos;
            varying vec2 v_TexCoord;
            
            void main() {
                vec3 norm = normalize(v_Normal);
                vec3 lightDir = normalize(u_LightPos - v_FragPos);
                
                vec4 ambient = u_AmbientColor;
                
                float diff = max(dot(norm, lightDir), 0.0);
                vec4 diffuse = diff * u_DiffuseColor;
                
                vec3 viewDir = normalize(-v_FragPos);
                vec3 reflectDir = reflect(-lightDir, norm);
                float spec = pow(max(dot(viewDir, reflectDir), 0.0), u_Shininess);
                vec4 specular = spec * u_SpecularColor;
                
                vec4 textureColor = texture2D(u_Texture, v_TexCoord);
                vec4 finalColor = (ambient + diffuse + specular) * textureColor;
                gl_FragColor = finalColor;
            }
        """
        const val BASIC_VERTEX_SHADER_CODE = """
        uniform mat4 u_MVPMatrix;
        attribute vec3 a_Position;
        attribute vec2 a_TexCoord;
        varying vec2 v_TexCoord;

        void main() {
            v_TexCoord = a_TexCoord;
            gl_Position = u_MVPMatrix * vec4(a_Position, 1.0);
        }
    """

        const val BASIC_FRAGMENT_SHADER_CODE = """
        precision mediump float;
        uniform sampler2D u_Texture;
        varying vec2 v_TexCoord;

        void main() {
            gl_FragColor = texture2D(u_Texture, v_TexCoord);
        }
    """
    }
}