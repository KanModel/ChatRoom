package me.kanmodel.oct18.concurrency.server

import java.io.IOException
import java.io.PrintWriter
import java.net.Socket
import java.util.ArrayList

/**
 * ����˷�����Ϣ
 */
class SendServer @Throws(IOException::class)
constructor(userList: ArrayList<Socket>, message: Any, info: String) {
    init {
        val messages = info + message//�����Ϣͷ���
        var pwOut: PrintWriter? = null
        for (s in userList) {//����Ϣ���͸�ÿ���ͻ���
            pwOut = PrintWriter(s.getOutputStream(), true)
            pwOut.println(messages)
        }
    }
}