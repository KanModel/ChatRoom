package spam

import java.awt.BorderLayout
import java.awt.Dimension
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JTextArea
import javax.swing.WindowConstants

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: kgdwhsk
 * Date: 2018-10-07
 * Time: 20:26
 */
class SpamClient {
    //�ͻ��˷�����Ϣ��
    internal class SendClient @Throws(IOException::class)
    constructor(s: Socket?, message: Any, info: String) {
        init {
            val messages = info + message
            val pwOut = PrintWriter(s!!.getOutputStream(), true)
            pwOut.println(messages)
        }
    }

    //�ͻ��˽����߳�
    internal class ReceiveClient(private val s: Socket?) : Runnable {
        override fun run() {
            try {
                //��Ϣ������
                val brIn = BufferedReader(InputStreamReader(s!!.getInputStream()))
                while (true) {
                    val info = brIn.read().toChar()//��ȡ��Ϣ�����ַ����ж���Ϣ����
                    val line = brIn.readLine()//��ȡ��Ϣ������

                    if (info == '1') {//�����͵�����Ϣ
                        //������Ϣ��ʾ����һ�У�Ҳ���ǹ�����������ĩβ����ʾ����һ���������Ϣ
                    }

                    if (info == '2' || info == '3') {//�����û�������˳���2Ϊ���룬3Ϊ�˳�
//                        val sub = line.substring(1, line.length - 1)
//                        val data = sub.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    }

                    if (info == '4') {//4���������˳�
                        s.close()
                        throw InterruptedException()
                    }
                }
            }  catch (ei: InterruptedException) {
                println("�������ر�")
                btn2.isEnabled = false
                btn3.isEnabled = false
                flag = false
            }
        }
    }


    companion object {
        var amount = 10
        val sockets = ArrayList<Socket?>()

        val frame = JFrame()
        val btn = JButton("����/�Ͽ�")
        val btn2 = JButton("SPAM")
        val btn3 = JButton("SPAM^2")

        var flag = false

        @JvmStatic
        fun main(arg: Array<String>) {

            btn2.isEnabled = false
            btn3.isEnabled = false
            val amountJTA = JTextArea()
            amountJTA.text = amount.toString()
            frame.setLocationRelativeTo(null)
            frame.size = Dimension(600, 600)
            frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
            frame.add(btn)
            frame.add(BorderLayout.EAST, btn2)
            frame.add(BorderLayout.SOUTH, btn3)
            frame.add(BorderLayout.NORTH, amountJTA)
//            frame.pack()


            btn.addActionListener {
                amount = amountJTA.text.toInt()
                if (flag) {
                    flag = !flag
                    spamStop()
                    btn2.isEnabled = false
                    btn3.isEnabled = false
                } else {
                    flag = !flag
                    sockets.clear()
                    spamStart()
                    btn2.isEnabled = true
                    btn3.isEnabled = true
                }
            }

            btn2.addActionListener {
                sendSpam()
            }

            btn3.addActionListener {
                sendNSpam()
            }


            frame.isVisible = true
        }

        private fun spamStart() {
            for (i in 0 until amount) {
//                sockets[i] = Socket("127.0.0.1", 20018)
                sockets.add(Socket("127.0.0.1", 20018))
                SendClient(sockets[i], "�ͻ���$i", "2")//���͸ÿͻ���������������
                Thread(ReceiveClient(sockets[i])).start()//���������߳�
            }
        }

        private fun spamStop() {
            for (i in 0 until amount) {
                SendClient(sockets[i], "�ͻ���$i", "3")//���͸ÿͻ���������������
                sockets[i]!!.close()
            }
            sockets.clear()
        }

        private fun sendSpam(){
//            SendClient(socket, getName() + "��" + messages, "1")

            for (i in 0 until amount) {
                SendClient(sockets[i], "gg!$i", "1")
            }
        }

        private fun sendNSpam(){
//            SendClient(socket, getName() + "��" + messages, "1")

            for (j in 0 until amount) {
                for (i in 0 until amount) {
                    SendClient(sockets[i], "gg!$i $j", "1")
                }
            }
        }
    }
}