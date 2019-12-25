package me.kanmodel.oct18.concurrency.util

import java.io.File
import java.util.*

object Base64Util{
    /*
    ���ļ�תΪbase64
    */
    fun pic2Base64(file: File): String {
        return Base64.getEncoder().encodeToString(file.readBytes())
    }

    /*
    ��base64תΪ�ֽ����������ڱ���
     */
    fun base642pic(base64Str: String, pathFile: String) {
        val imageByteArray = Base64.getDecoder().decode(base64Str)
        File(pathFile).writeBytes(imageByteArray)
    }
}