package client

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: kgdwhsk
 * Date: 2018-10-05
 * Time: 20:08
 */
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.net.UnknownHostException

import javax.swing.JOptionPane

//�����ͻ��˽����߳�
class StartClient @Throws(UnknownHostException::class, IOException::class)
constructor(s: Socket) {
    init {
        Thread(ReceiveClient(s)).start()
    }
}

//�ͻ��˽����߳�
internal class ReceiveClient(private val s: Socket) : Runnable {
    override fun run() {
        try {
            //��Ϣ������
            val brIn = BufferedReader(InputStreamReader(s.getInputStream()))
            while (true) {
                val info = brIn.read().toChar()//��ȡ��Ϣ�����ַ����ж���Ϣ����
                val line = brIn.readLine()//��ȡ��Ϣ������

                if (info == '1') {//�����͵�����Ϣ
                    WindowClient.textMessage.append(line + "\r\n")    //����Ϣ��ӵ��ı�����
                    //������Ϣ��ʾ����һ�У�Ҳ���ǹ�����������ĩβ����ʾ����һ���������Ϣ
                    WindowClient.textMessage.caretPosition = WindowClient.textMessage.text.length
                }

                if (info == '2' || info == '3') {//�����û�������˳���2Ϊ���룬3Ϊ�˳�
                    val sub = line.substring(1, line.length - 1)
                    val data = sub.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    WindowClient.user.clearSelection()
                    WindowClient.user.setListData(data)
                }

                if (info == '4') {//4���������˳�
                    WindowClient.link.text = "����"
                    WindowClient.exit.text = "���˳�"
                    WindowClient.socket!!.close()
                    WindowClient.socket = null
                    break
                }
            }
        } catch (e: IOException) {
            JOptionPane.showMessageDialog(WindowClient.window, "�ͻ������˳�����")
        }

    }
}

//�ͻ��˷�����Ϣ��
internal class SendClient @Throws(IOException::class)
constructor(s: Socket, message: Any, info: String) {
    init {
        val messages = info + message
        val pwOut = PrintWriter(s.getOutputStream(), true)
        pwOut.println(messages)
    }
}


