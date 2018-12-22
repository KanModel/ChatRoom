package client

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: kgdwhsk
 * Date: 2018-10-05
 * Time: 20:08
 */
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.IOException
import java.net.Socket
import java.text.SimpleDateFormat
import java.util.*
import javax.swing.*
import javax.swing.border.TitledBorder

class WindowClient {
    internal lateinit var port: JTextField
    internal lateinit var name: JTextField
    internal lateinit var ip: JTextField
    internal lateinit var message: JTextField
    internal lateinit var send: JButton

    //��ʼ������
    init {
        init()
    }

    //�����ʼ������
    fun init() {//���þ��Բ���
        window = JFrame("�ͻ���")
        window.layout = null
        window.setBounds(200, 200, 500, 400)
        window.isResizable = false

        val label = JLabel("����IP:")
        label.setBounds(10, 8, 50, 30)
        window.add(label)

        ip = JTextField()
        ip.setBounds(55, 8, 60, 30)
        ip.text = "127.0.0.1"
        window.add(ip)


        val label1 = JLabel("�˿ں�:")
        label1.setBounds(125, 8, 50, 30)
        window.add(label1)

        port = JTextField()
        port.setBounds(170, 8, 40, 30)
        port.text = "20018"
        window.add(port)

        val names = JLabel("�û���:")
        names.setBounds(220, 8, 55, 30)
        window.add(names)

        name = JTextField()
        name.setBounds(265, 8, 60, 30)
        name.text = "�ͻ���1"
        window.add(name)

        link = JButton("����")
        link.setBounds(335, 8, 75, 30)
        window.add(link)

        exit = JButton("�˳�")
        exit.setBounds(415, 8, 75, 30)
        window.add(exit)

//        val label2 = JLabel("�û��б�")
//        label2.setBounds(40, 40, 80, 30)
//        window.add(label2)

        userJL = JList()
        userJL.border = TitledBorder("�û��б�")
        val scrollPane = JScrollPane(userJL)//���ù�����
        scrollPane.setBounds(10, 50, 120, 240)
        window.add(scrollPane)

        textMessage = JTextArea()
        textMessage.setBounds(135, 50, 340, 240)
        textMessage.isEditable = false//�ı����ɱ༭
        textMessage.border = TitledBorder("�����¼")//���ñ���
        //�ı����ݻ��е�������Ҫ�������
        textMessage.lineWrap = true//�����ı������Զ����У��ڳ����ı�����ʱ�����ܻ��жϵ���
        textMessage.wrapStyleWord = true//�������Զ����У��Ե���Ϊ���壬��֤���ʲ��ᱻ�ж�
        val scrollPane1 = JScrollPane(textMessage)//���ù�����
        scrollPane1.setBounds(135, 50, 340, 240)
        window.add(scrollPane1)

        message = JTextField()
        message.setBounds(10, 300, 360, 50)
        message.text = null
        window.add(message)

        send = JButton("����")
        send.setBounds(380, 305, 70, 40)
        window.add(send)

        myEvent()//��Ӽ����¼�
        window.isVisible = true//���ô���ɼ�
    }


    fun myEvent() {//�¼�����
        window.addWindowListener(object : WindowAdapter() {
            //�˳�����
            override fun windowClosing(e: WindowEvent?) {
                //����������ӣ�����Ϣ������ˣ����˳�
                if (socket != null && socket!!.isConnected) {
                    try {
                        ClientSender(socket!!, getName(), "3")
                    } catch (e1: IOException) {
                        e1.printStackTrace()
                    }

                }
                System.exit(0)
            }
        })

        //�ر�����
        exit.addActionListener {
            Thread{
                //����������ӣ�����Ϣ���������
                if (socket == null) {
                    JOptionPane.showMessageDialog(window, "�ѹر�����")
                } else if (socket != null && socket!!.isConnected) {
                    try {
                        userJL.setListData(Vector<String>())
                        ClientSender(socket!!, getName(), "3")//������Ϣ�������
                        link.text = "����"
                        exit.text = "���˳�"
                        socket!!.close()//�ر�socket
                        socket = null
                    } catch (e1: IOException) {
                        e1.printStackTrace()
                    }

                }
            }.start()
        }

        //��������
        link.addActionListener {
            Thread {
                //�ж��Ƿ��Ѿ����ӳɹ�
                if (socket != null && socket!!.isConnected) {
                    JOptionPane.showMessageDialog(window, "�Ѿ����ӳɹ���")
                } else {
                    val ipString = ip.text//��ȡip��ַ
                    val portClient = port.text//��ȡ�˿ں�

                    if ("" == ipString || "" == portClient) {//�жϻ�ȡ�����Ƿ�Ϊ��
                        JOptionPane.showMessageDialog(window, "ip��˿ں�Ϊ�գ�")
                    } else {
                        try {

                            val ports = Integer.parseInt(portClient)//���˿ں�תΪ����
                            socket = Socket(ipString, ports)//��������
                            socket!!.tcpNoDelay = true
                            link.text = "������"//����button��ʾ��Ϣ
                            exit.text = "�˳�"
                            ClientSender(socket!!, getName(), "2")//���͸ÿͻ���������������
                            Thread(ClientReceiver(socket!!)).start()//���������߳�
                            textMessage.text = ""

                        } catch (e2: Exception) {
                            JOptionPane.showMessageDialog(window, "����δ�ɹ���������ip��˿ںŸ�ʽ���ԣ��������δ������")
                        }
                    }
                }
            }.start()
        }

        //�����ť������Ϣ
        send.addActionListener { sendMsg() }

        //���س�������Ϣ
        message.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent?) {
                if (e!!.keyCode == KeyEvent.VK_ENTER) {
                    sendMsg()
                }
            }
        })
    }

    //������Ϣ�ķ���
    fun sendMsg() {
        val messages = message.text//��ȡ�ı�������
        if ("" == messages) {//�ж���Ϣ�Ƿ�Ϊ��
            JOptionPane.showMessageDialog(window, "���ݲ���Ϊ�գ�")
        } else if (socket == null || !socket!!.isConnected) {//�ж��Ƿ��Ѿ����ӳɹ�
            JOptionPane.showMessageDialog(window, "δ���ӳɹ������ܷ�����Ϣ��")
        } else {
            try {
                //������Ϣ
                ClientSender(socket!!, messages, "1")
                message.text = null//�ı�����������Ϊ��
            } catch (e1: IOException) {
                JOptionPane.showMessageDialog(window, "��Ϣ����ʧ�ܣ�")
            }

        }
    }

    //��ȡ�ͻ�������
    fun getName(): String {
        return name.text
    }

    companion object {
        lateinit var window: JFrame
        lateinit var link: JButton
        lateinit var exit: JButton
        lateinit var textMessage: JTextArea
        var socket: Socket? = null
        lateinit var userJL: JList<String>

        //���������
        @JvmStatic
        fun main(args: Array<String>) {
            SwingUtilities.invokeLater {
                WindowClient()
            }
        }
    }
}
