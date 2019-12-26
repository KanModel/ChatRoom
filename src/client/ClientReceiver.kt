package client

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: kgdwhsk
 * Date: 2018-10-05
 * Time: 20:08
 */
import me.kanmodel.oct18.concurrency.util.Base64Util.base642pic
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.Socket

import javax.swing.JOptionPane
import sun.text.normalizer.UTF16.append
import java.awt.Image
import javax.swing.ImageIcon
import javax.swing.SwingUtilities
import javax.swing.text.StyledDocument




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
                            val doc = WindowClient.textMessage.getStyledDocument()
                            doc.insertString(doc.getLength(), line+"\r\n", null );
                        }
                    }
                }

                if (info == '5') {//�յ�ͼƬ
                    if (line != "") {
                        WindowClient.tmpCount = WindowClient.tmpCount?.plus(1)
                        println("${WindowClient.tmpDir}\\t${WindowClient.tmpCount}.png")
                        base642pic(line, "${WindowClient.tmpDir}\\t${WindowClient.tmpCount}.png")
                        SwingUtilities.invokeLater {
                            val doc = WindowClient.textMessage.getStyledDocument()
                            WindowClient.textMessage.setCaretPosition(doc.getLength()) // ���ò���λ��

                            val image = ImageIcon("${WindowClient.tmpDir}\\t${WindowClient.tmpCount}.png")
                            image.setImage(image.getImage().getScaledInstance(200, 150, Image.SCALE_DEFAULT ));
                            WindowClient.textMessage.insertIcon(image) // ����ͼƬ
                            doc.insertString(doc.getLength(), "\r\n", null );

//                            WindowClient.textMessage.append("ͼƬ${WindowClient.tmpCount}\r\n")    //����Ϣ��ӵ��ı�����
                        }
                    }
                }

                if (info == '2' || info == '3') {//�����û�������˳���2Ϊ���룬3Ϊ�˳�
                    val sub = line.substring(1, line.length - 1)//ȥ���ַ���ͷβ������
                    SwingUtilities.invokeLater {
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
                    val data = Array<String>(0) { "" }
                    WindowClient.userJL.setListData(data)
                    throw InterruptedException()
                }
            }
        } catch (e: IOException) {
            JOptionPane.showMessageDialog(WindowClient.window, "�ͻ������˳�����")
        } catch (ei: InterruptedException) {
            JOptionPane.showMessageDialog(WindowClient.window, "�������ر�")
        }
    }
}


