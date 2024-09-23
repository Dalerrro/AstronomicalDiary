package com.example.startapp.openGL.renderGL

import android.content.Context
import android.opengl.GLSurfaceView
import com.example.startapp.openGL.MyGLRenderer

class MyGLSurfaceView(context: Context) : GLSurfaceView(context) {

    private val renderer: MyGLRenderer

    init {
        setEGLContextClientVersion(2)

        renderer = MyGLRenderer(context)
        setRenderer(renderer)


    }
}