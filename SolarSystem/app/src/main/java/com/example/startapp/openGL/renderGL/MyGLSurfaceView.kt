package com.example.startapp.openGL.renderGL

import android.content.Context
import android.opengl.GLSurfaceView
import com.example.startapp.openGL.MyGLRenderer

class MyGLSurfaceView(context: Context, private val renderer: MyGLRenderer) : GLSurfaceView(context) {
    init {
        setEGLContextClientVersion(2)
        setRenderer(renderer)
    }

    fun setSelectedPlanet(index: Int) {
        renderer.setSelectedObjectIndex(index)
    }
}