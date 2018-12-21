package me.kanmodel.oct18.concurrency.gui

import me.kanmodel.oct18.concurrency.util.Log
import me.kanmodel.oct18.concurrency.Main
import me.kanmodel.oct18.concurrency.net.DataManager.CHAT_LOCKER
import me.kanmodel.oct18.concurrency.net.DataManager.chatHistories
import me.kanmodel.oct18.concurrency.net.DataManager.chatMutex
import me.kanmodel.oct18.concurrency.net.DataManager.chatQueue
import me.kanmodel.oct18.concurrency.net.DataManager.notEmpty
import me.kanmodel.oct18.concurrency.net.SendServer
import me.kanmodel.oct18.concurrency.net.StartServer
import java.awt.BorderLayout
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import javax.swing.*
import javax.swing.border.TitledBorder

class ChatLogPanel : JPanel() {
    private val message = JTextField()
    private val sendBtn = JButton("����")

    init {
        layout = BorderLayout(5, 5)

        chatLogJTA = JTextArea()
        chatLogJTA.border = TitledBorder("�����¼")//���ñ���
        chatLogJTA.isEditable = false//���ɱ༭
        //�ı����ݻ��е�������Ҫ�������
        chatLogJTA.lineWrap = true//�����ı������Զ����У��ڳ����ı�����ʱ�����ܻ��жϵ���
        chatLogJTA.wrapStyleWord = true//�������Զ����У��Ե���Ϊ���壬��֤���ʲ��ᱻ�ж�
        val textScrollPane = JScrollPane(chatLogJTA)
//        add(BorderLayout.CENTER, JScrollPane(chatLogJTA))

        userJL = JList()
        userJL.border = TitledBorder("�û��б�")
        val userScrollPane = JScrollPane(userJL)
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
        } else if (StartServer.userSockets.size == 0) {//�ж��Ƿ��Ѿ����ӳɹ�
            JOptionPane.showMessageDialog(Main.mainFrame, "δ���ӳɹ������ܷ�����Ϣ��")
        } else {
            try {
                val line = "${SimpleDateFormat("HH:mm:ss").format(Date())} [${getServerName()}]:$messages"
                SendServer(line, 1.toString() + "")//����Ϣ���͸����пͻ���

                Log.log("������߳� ���Ի�ȡ$CHAT_LOCKER")
                chatMutex.acquire()
                try {
//                    chatLogJTA.append("$line\r\n")//����Ϣ��ӵ��ͻ��������¼��
                    chatQueue.offer(line)
                    notEmpty.release()
                    chatHistories.add(line)
                } catch (e: Exception) {
                    e.printStackTrace()
                }finally {
                    chatMutex.release()
                    Log.log("������߳� �ͷ�$CHAT_LOCKER")
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
        lateinit var chatLogJTA: JTextArea//�����¼�ı�����
        lateinit var userJL: JList<String>//�û��б�
    }
}
