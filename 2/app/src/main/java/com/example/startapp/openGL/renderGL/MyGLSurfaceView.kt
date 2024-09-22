package com.example.startapp.openGL.renderGL

import android.content.Context
import android.opengl.GLSurfaceView
import com.example.startapp.openGL.MyGLRenderer

class MyGLSurfaceView(context: Context) : GLSurfaceView(context) {

    private val renderer: MyGLRenderer

    init {
        // Устанавливаем OpenGL ES 2.0
        setEGLContextClientVersion(2)

        // Создаем и устанавливаем рендерер
        renderer = MyGLRenderer(context)
        setRenderer(renderer)
    }
}