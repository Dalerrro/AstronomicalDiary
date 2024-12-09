package com.example.cubik.openGL

import android.content.Context
import android.opengl.GLSurfaceView

class MyGLSurfaceView(context: Context, private val renderer: MyGLRenderer) : GLSurfaceView(context) {
    init {
        setEGLContextClientVersion(2)
        setRenderer(renderer)
    }
}