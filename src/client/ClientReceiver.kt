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
import java.net.Socket

import javax.swing.JOptionPane
import sun.text.normalizer.UTF16.append
import javax.swing.SwingUtilities



//�ͻ��˽����߳�
class ClientReceiver(private val s: Socket) : Runnable {
    override fun run() {
        try {
            val brIn = BufferedReader(InputStreamReader(s.getInputStream()))//��Ϣ������
            while (true) {
                val info = brIn.read().toChar()//��ȡ��Ϣ�����ַ����ж���Ϣ����
                val line = brIn.readLine()//��ȡ��Ϣ������

                if (info == '1') {//�����͵�����Ϣ
                    if (line != "") {
                        SwingUtilities.invokeLater {
                            WindowClient.textMessage.append(line + "\r\n")    //����Ϣ��ӵ��ı�����
                            WindowClient.textMessage.caretPosition = WindowClient.textMessage.text.length//������Ϣ��ʾ����һ�У�Ҳ���ǹ�����������ĩβ����ʾ����һ���������Ϣ
                        }
                    }
                }

                if (info == '2' || info == '3') {//�����û�������˳���2Ϊ���룬3Ϊ�˳�
                    val sub = line.substring(1, line.length - 1)//ȥ���ַ���ͷβ������
                    SwingUtilities.invokeLater{
                        val data = sub.split(", ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()//�ָ������б�
                        WindowClient.userJL.clearSelection()
                        WindowClient.userJL.setListData(data)
                    }
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


