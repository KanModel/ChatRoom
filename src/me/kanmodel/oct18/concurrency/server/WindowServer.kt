package me.kanmodel.oct18.concurrency.server

import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.IOException

import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.JOptionPane
import javax.swing.JScrollPane
import javax.swing.JTextArea
import javax.swing.JTextField
import javax.swing.border.TitledBorder

class WindowServer {
    internal lateinit var start: JButton
    internal lateinit var send: JButton
    internal lateinit var exit: JButton
    internal lateinit var portServer: JTextField
    internal lateinit var message: JTextField
    internal lateinit var name: JTextField

    //��ȡ�˿ں�
    //�ж϶˿ں��Ƿ�Ϊ��
    //�������εĶ˿ں�
    private val port: Int
        get() {
            val port = portServer.text
            return if ("" == port) {
                JOptionPane.showMessageDialog(window, "�˿ں�Ϊ��")
                0
            } else {
                Integer.parseInt(port)
            }
        }

    //��ʼ������
    init {
        init()
    }

    //��ʼ������
    fun init() {//���þ��Բ���
        window = JFrame("�����")
        window.layout = null
        window.setBounds(200, 200, 500, 400)
        window.isResizable = false//���ɸı��С

        val label1 = JLabel("�˿ں�:")
        label1.setBounds(10, 8, 50, 30)
        window.add(label1)

        portServer = JTextField()
        portServer.setBounds(60, 8, 100, 30)
        portServer.text = "30000"
        window.add(portServer)

        val names = JLabel("�û���:")
        names.setBounds(180, 8, 55, 30)
        window.add(names)

        name = JTextField()
        name.setBounds(230, 8, 60, 30)
        name.text = "�����"
        window.add(name)

        start = JButton("����")
        start.setBounds(300, 8, 80, 30)
        window.add(start)

        exit = JButton("�ر�")
        exit.setBounds(390, 8, 80, 30)
        window.add(exit)


        val label2 = JLabel("�û��б�")
        label2.setBounds(40, 40, 80, 30)
        window.add(label2)


        user = JList()
        val scrollPane = JScrollPane(user)//��ӹ�����
        scrollPane.setBounds(10, 70, 120, 220)
        window.add(scrollPane)

        textMessage = JTextArea()
        textMessage.setBounds(135, 70, 340, 220)
        textMessage.border = TitledBorder("�����¼")//���ñ���
        textMessage.isEditable = false//���ɱ༭
        //�ı����ݻ��е�������Ҫ�������
        textMessage.lineWrap = true//�����ı������Զ����У��ڳ����ı�����ʱ�����ܻ��жϵ���
        textMessage.wrapStyleWord = true//�������Զ����У��Ե���Ϊ���壬��֤���ʲ��ᱻ�ж�
        val scrollPane1 = JScrollPane(textMessage)//���ù�����
        scrollPane1.setBounds(135, 70, 340, 220)
        window.add(scrollPane1)

        message = JTextField()
        message.setBounds(10, 300, 360, 50)

        window.add(message)

        send = JButton("����")
        send.setBounds(380, 305, 70, 40)
        window.add(send)

        myEvent()    //��Ӽ����¼�
        window.isVisible = true
    }

    private fun myEvent() {
        window.addWindowListener(object : WindowAdapter() {
            //�رմ���
            override fun windowClosing(e: WindowEvent?) {
                //����пͻ��˴��ڣ�����Ϣ���ͻ��ˣ����˳�
                if (StartServer.userList != null && StartServer.userList!!.size != 0) {
                    try {
                        SendServer(StartServer.userList!!, "", "4")//4���������˳�
                    } catch (e1: IOException) {
                        e1.printStackTrace()
                    }

                }
                System.exit(0)//�˳�����
            }
        })

        exit.addActionListener {
            if (StartServer.serverSocket == null || StartServer.serverSocket!!.isClosed()) {//������˳�����������
                JOptionPane.showMessageDialog(window, "�������ѹر�")
            } else {
                //����Ϣ���߿ͻ��ˣ�Ҫ�˳�
                if (StartServer.userList != null && StartServer.userList!!.size != 0) {
                    try {
                        SendServer(StartServer.userList!!, "", 4.toString() + "")
                    } catch (e1: IOException) {
                        e1.printStackTrace()
                    }

                }
                try {
                    start.text = "����"
                    exit.text = "�ѹر�"
                    StartServer.serverSocket!!.close()//�رշ����
                    StartServer.serverSocket = null
                    StartServer.userList = null
                    StartServer.flag = false//�ı�����ѭ�����
                } catch (e1: IOException) {
                    e1.printStackTrace()
                }

            }
        }

        //���������
        start.addActionListener {
            //���������Ѿ��������������ѷ�����ѿ���
            if (StartServer.serverSocket != null && !StartServer.serverSocket!!.isClosed()) {
                JOptionPane.showMessageDialog(window, "�������Ѿ�����")
            } else {
                ports = port//��ȡ�˿ں�
                if (ports != 0) {
                    try {
                        StartServer.flag = true//�ı����˽���ѭ�����
                        Thread(StartServer(ports)).start() //��������˽����߳�
                        start.text = "������"
                        exit.text = "�ر�"
                    } catch (e1: IOException) {
                        JOptionPane.showMessageDialog(window, "����ʧ��")
                    }
                }
            }
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

    //������Ϣ����
    fun sendMsg() {
        val messages = message.text
        //�ж������Ƿ�Ϊ��
        if (messages == "") {
            JOptionPane.showMessageDialog(window, "���ݲ���Ϊ�գ�")
        } else if (StartServer.userList == null || StartServer.userList!!.size == 0) {//�ж��Ƿ��Ѿ����ӳɹ�
            JOptionPane.showMessageDialog(window, "δ���ӳɹ������ܷ�����Ϣ��")
        } else {
            try {
                //����Ϣ���͸����пͻ���
                SendServer(StartServer.userList!!, getName() + "��" + messages, 1.toString() + "")
                //����Ϣ��ӵ��ͻ��������¼��
                WindowServer.textMessage.append(getName() + "��" + messages + "\r\n")
                message.text = null//��Ϣ������Ϊ��
            } catch (e1: IOException) {
                JOptionPane.showMessageDialog(window, "����ʧ�ܣ�")
            }

        }
    }

    //��ȡ���������
    private fun getName(): String {
        return name.text
    }

    companion object {
        lateinit var window: JFrame
        lateinit var textMessage: JTextArea//�����¼
        lateinit var user: JList<String>//�û��б�
        var ports: Int = 0

        //���������
        @JvmStatic
        fun main(args: Array<String>) {
            WindowServer()
        }
    }
}


