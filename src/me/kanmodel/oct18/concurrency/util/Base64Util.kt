package me.kanmodel.oct18.concurrency.util

import java.io.File
import java.util.*

object Base64Util{
    /*
    将文件转为base64
    */
    fun pic2Base64(file: File): String {
        return Base64.getEncoder().encodeToString(file.readBytes())
    }

    /*
    将base64转为字节流并保存在本地
     */
    fun base642pic(base64Str: String, pathFile: String) {
        val imageByteArray = Base64.getDecoder().decode(base64Str)
        File(pathFile).writeBytes(imageByteArray)
    }
}