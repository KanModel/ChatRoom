package me.kanmodel.oct18.concurrency.net

import java.io.IOException
import java.io.PrintWriter

/**
 * ����˷�����Ϣ
 */
class SendServer @Throws(IOException::class)
constructor(message: Any, info: String) {
    init {
        val messages = info + message//�����Ϣͷ���
        var pwOut: PrintWriter? = null

//        ReceiveServer.socketListSem.acquire()
        try {
            for (s in StartServer.userSocketList) {//����Ϣ���͸�ÿ���ͻ���
                pwOut = PrintWriter(s.getOutputStream(), true)
                pwOut.println(messages)
            }
        }finally {
//            ReceiveServer.socketListSem.release()
        }
    }
}