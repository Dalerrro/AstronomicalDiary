package com.example.cubik.obj

import android.content.Context
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class ObjModelData(
    val vertices: FloatBuffer,
    val indices: ShortBuffer
) {
    companion object {
        fun loadFromStream(context: Context, fileName: String): ObjModelData {
            val inputStream: InputStream = context.assets.open(fileName)

            val vertices = mutableListOf<Float>()
            val indices = mutableListOf<Short>()

            inputStream.bufferedReader().useLines { lines ->
                lines.forEach { line ->
                    val tokens = line.trim().split(" ")
                    when {
                        tokens.isNotEmpty() && tokens[0] == "v" -> {
                            // Считываем вершины
                            vertices.addAll(tokens.drop(1).map { it.toFloat() })
                        }
                        tokens.isNotEmpty() && tokens[0] == "f" -> {
                            // Считываем индексы
                            indices.addAll(tokens.drop(1).map { it.toShort() })
                        }
                    }
                }
            }

            val vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .apply { put(vertices.toFloatArray()); position(0) }

            val indexBuffer = ByteBuffer.allocateDirect(indices.size * 2)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer()
                .apply { put(indices.toShortArray()); position(0) }

            return ObjModelData (vertexBuffer, indexBuffer)
        }
    }
}
