package me.kanmodel.oct18.concurrency.net

import me.kanmodel.oct18.concurrency.net.DataManager.socketsMutex
import me.kanmodel.oct18.concurrency.net.DataManager.userSockets
import java.io.IOException
import java.io.PrintWriter
import java.net.Socket

/**
 * description: �����пͻ��˷������ݣ����+��Ϣ��
 * @param message ��Ϣ
 * @param info ���
 */
class SendServer @Throws(IOException::class)
constructor() {

    constructor(message: Any, info: String) : this() {
        val messages = info + message//�����Ϣͷ���
        var pwOut: PrintWriter?

        socketsMutex.acquire()
        try {
            for (s in userSockets) {//����Ϣ���͸�ÿ���ͻ���
                pwOut = PrintWriter(s.getOutputStream(), true)
                pwOut.println(messages)
            }
        }finally {
            socketsMutex.release()
        }
    }

    constructor(socket: Socket, message: Any, info: String) : this() {
        val messages = info + message//�����Ϣͷ���

        val pwOut = PrintWriter(socket.getOutputStream(), true)
        pwOut.println(messages)
    }
}