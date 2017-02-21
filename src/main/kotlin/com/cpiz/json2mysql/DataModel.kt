package com.cpiz.json2mysql

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader


/**
 * Created by caijw on 2017/2/18.
 *
 * 数据Model，进行数据的读取和解析
 */
class DataModel(filePath: String) {
    var file: File = File(filePath)

    init {
        if (!file.exists()) {
            throw Exception("file `$filePath` is not exist")
        }
        if (!file.isFile) {
            throw Exception("path `$filePath` is not a file")
        }
    }

    private fun readLines(func: (String) -> Unit) {
        var isReader: InputStreamReader? = null
        var bufferedReader: BufferedReader? = null
        try {
            isReader = InputStreamReader(FileInputStream(file), "UTF8")// 考虑到编码格式
            bufferedReader = BufferedReader(isReader)

            var line: String?
            do {
                line = bufferedReader.readLine()
                if (line != null) {
                    func(line)
                } else {
                    break
                }
            } while (true)
        } catch (e: Exception) {
            System.err.println("Task interrupted")
        } finally {
            safeDo { bufferedReader?.close() }
            safeDo { isReader?.close() }
        }
    }

    /**
     * 获得文件行数
     */
    fun getLineCount(): Int {
        var rows = 0
        val func = fun(): Unit {
            rows++
        }
        readLines { func() }
        return rows
    }

    fun parseJsonObjects(func: (JSONObject) -> Unit) {
        readLines { line ->
            try {
                val newLine = line.trim().trim(',')
                val obj = JSON.parse(newLine)
                if (newLine.isNullOrEmpty()) {
                    return@readLines
                }

                if (obj is JSONObject) {
                    func(obj)
                } else {
                    throw Exception("is not json object")
                }
            } catch (e: Exception) {
                System.err.println("parse `$line` error, ${e.message}")
            }
        }
    }
}