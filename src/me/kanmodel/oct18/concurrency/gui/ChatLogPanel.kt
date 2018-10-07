package me.kanmodel.oct18.concurrency.gui

import me.kanmodel.oct18.concurrency.Log
import me.kanmodel.oct18.concurrency.Main
import me.kanmodel.oct18.concurrency.server.ReceiveServer
import me.kanmodel.oct18.concurrency.server.SendServer
import me.kanmodel.oct18.concurrency.server.StartServer
import java.awt.BorderLayout
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.io.IOException
import java.lang.Exception
import javax.swing.*
import javax.swing.border.TitledBorder

class ChatLogPanel : JPanel() {
    private val message = JTextField()
    private val sendBtn = JButton("����")

    init {
        layout = BorderLayout(5, 5)

        textMessage = JTextArea()
        textMessage.border = TitledBorder("�����¼")//���ñ���
        textMessage.isEditable = false//���ɱ༭
        //�ı����ݻ��е�������Ҫ�������
        textMessage.lineWrap = true//�����ı������Զ����У��ڳ����ı�����ʱ�����ܻ��жϵ���
        textMessage.wrapStyleWord = true//�������Զ����У��Ե���Ϊ���壬��֤���ʲ��ᱻ�ж�
        val textScrollPane = JScrollPane(textMessage)
//        add(BorderLayout.CENTER, JScrollPane(textMessage))

        userList = JList()
        userList.border = TitledBorder("�û��б�")
        val userScrollPane = JScrollPane(userList)
//        add(BorderLayout.WEST, userScrollPane)

        val chatSpiltPane = JSplitPane()
        chatSpiltPane.leftComponent = userScrollPane
        chatSpiltPane.rightComponent = textScrollPane
        chatSpiltPane.isOneTouchExpandable = true
        chatSpiltPane.dividerLocation = 150
        add(chatSpiltPane)

        val messagePanel = JPanel(BorderLayout())
        //���س�������Ϣ
        message.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent?) {
                if (e!!.keyCode == KeyEvent.VK_ENTER) {
                    sendMsg()
                }
            }
        })
        messagePanel.add(BorderLayout.CENTER, message)
        sendBtn.addActionListener { sendMsg() }//�����ť������Ϣ
        messagePanel.add(BorderLayout.EAST, sendBtn)
        add(BorderLayout.SOUTH, messagePanel)

/*        val splitPane = JSplitPane(JSplitPane.VERTICAL_SPLIT)
        splitPane.topComponent = chatSpiltPane
        splitPane.bottomComponent = messagePanel
        chatSpiltPane.dividerLocation = 100
        add(splitPane)*/
    }

    //������Ϣ
    fun sendMsg() {
        val messages = message.text
        //�ж������Ƿ�Ϊ��
        if (messages == "") {
            JOptionPane.showMessageDialog(Main.mainFrame, "���ݲ���Ϊ�գ�")
        } else if (StartServer.userSocketList == null || StartServer.userSocketList!!.size == 0) {//�ж��Ƿ��Ѿ����ӳɹ�
            JOptionPane.showMessageDialog(Main.mainFrame, "δ���ӳɹ������ܷ�����Ϣ��")
        } else {
            try {
                //����Ϣ���͸����пͻ���
                SendServer(getServerName() + "��" + messages, 1.toString() + "")

                Log.log("����� ���Ի�ȡ��")
                ReceiveServer.chatLogSem.acquire()
                try {
                    //����Ϣ��ӵ��ͻ��������¼��
                    textMessage.append(getServerName() + "��" + messages + "\r\n")
                } catch (e: Exception) {
                    e.printStackTrace()
                }finally {
                    ReceiveServer.chatLogSem.release()
                    Log.log("����� �ͷ���")
                }
                message.text = null//��Ϣ������Ϊ��
            } catch (e1: IOException) {
                JOptionPane.showMessageDialog(Main.mainFrame, "����ʧ�ܣ�")
            }

        }
    }

    //��ȡ���������
    private fun getServerName(): String {
        return OptionPanel.serverName.text
    }

    companion object {
        lateinit var textMessage: JTextArea//�����¼
        lateinit var userList: JList<String>//�û��б�
    }
}
