package com.example.cubik.table

import android.content.Context
import android.opengl.Matrix
import com.example.cubik.TextureLoader
import com.example.cubik.R

class Table(context: Context) {
    private val top: Cube
    private val legs: Array<Cube>

    private val topTextureId: Int
    private val legTextureId: Int

    init {

        topTextureId = TextureLoader.loadTexture(context, R.drawable.tabletex)
        legTextureId = TextureLoader.loadTexture(context, R.drawable.tabletex)


        top = Cube(topTextureId)


        legs = Array(4) { Cube(legTextureId) }
    }

    fun draw(mvpMatrix: FloatArray) {

        val topMatrix = FloatArray(16)
        Matrix.setIdentityM(topMatrix, 0)
        Matrix.translateM(topMatrix, 0, 0f, 0f, 0f)
        Matrix.scaleM(topMatrix, 0, 2f, 0.1f, 1f)
        val topMVP = FloatArray(16)
        Matrix.multiplyMM(topMVP, 0, mvpMatrix, 0, topMatrix, 0)
        top.draw(topMVP)


        val legPositions = arrayOf(
            floatArrayOf(-0.9f, -0.36f, -0.4f), // Левая задняя
            floatArrayOf(0.9f, -0.36f, -0.4f),  // Правая задняя
            floatArrayOf(-0.9f, -0.36f, 0.4f),  // Левая передняя
            floatArrayOf(0.9f, -0.36f, 0.4f)    // Правая передняя
        )


        for (i in legs.indices) {
            val legMatrix = FloatArray(16)
            Matrix.setIdentityM(legMatrix, 0)
            Matrix.translateM(legMatrix, 0, legPositions[i][0], legPositions[i][1], legPositions[i][2])
            Matrix.scaleM(legMatrix, 0, 0.1f, 0.8f, 0.1f) // Размер ножек
            val legMVP = FloatArray(16)
            Matrix.multiplyMM(legMVP, 0, mvpMatrix, 0, legMatrix, 0)
            legs[i].draw(legMVP)
        }
    }
}
