package me.kanmodel.oct18.concurrency.gui

import me.kanmodel.oct18.concurrency.util.Log
import me.kanmodel.oct18.concurrency.Main
import me.kanmodel.oct18.concurrency.net.LockerManager.CHAT_LOCKER
import me.kanmodel.oct18.concurrency.net.ReceiveServer
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
    private val sendBtn = JButton("发送")

    init {
        layout = BorderLayout(5, 5)

        textMessage = JTextArea()
        textMessage.border = TitledBorder("聊天记录")//设置标题
        textMessage.isEditable = false//不可编辑
        //文本内容换行的两个需要配合着用
        textMessage.lineWrap = true//设置文本内容自动换行，在超出文本区域时，可能会切断单词
        textMessage.wrapStyleWord = true//设置以自动换行，以单词为整体，保证单词不会被切断
        val textScrollPane = JScrollPane(textMessage)
//        add(BorderLayout.CENTER, JScrollPane(textMessage))

        userList = JList()
        userList.border = TitledBorder("用户列表")
        val userScrollPane = JScrollPane(userList)
//        add(BorderLayout.WEST, userScrollPane)

        val chatSpiltPane = JSplitPane()
        chatSpiltPane.leftComponent = userScrollPane
        chatSpiltPane.rightComponent = textScrollPane
        chatSpiltPane.isOneTouchExpandable = true
        chatSpiltPane.dividerLocation = 150
        add(chatSpiltPane)

        val messagePanel = JPanel(BorderLayout())
        //按回车发送消息
        message.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent?) {
                if (e!!.keyCode == KeyEvent.VK_ENTER) {
                    sendMsg()
                }
            }
        })
        messagePanel.add(BorderLayout.CENTER, message)
        sendBtn.addActionListener { sendMsg() }//点击按钮发送消息
        messagePanel.add(BorderLayout.EAST, sendBtn)
        add(BorderLayout.SOUTH, messagePanel)

/*        val splitPane = JSplitPane(JSplitPane.VERTICAL_SPLIT)
        splitPane.topComponent = chatSpiltPane
        splitPane.bottomComponent = messagePanel
        chatSpiltPane.dividerLocation = 100
        add(splitPane)*/
    }

    //发送信息
    fun sendMsg() {
        val messages = message.text
        //判断内容是否为空
        if (messages == "") {
            JOptionPane.showMessageDialog(Main.mainFrame, "内容不能为空！")
        } else if (StartServer.userSocketList.size == 0) {//判断是否已经连接成功
            JOptionPane.showMessageDialog(Main.mainFrame, "未连接成功，不能发送消息！")
        } else {
            try {
                //将信息发送给所有客户端
//                SendServer(getServerName() + "：" + messages, 1.toString() + "")
                val line = "${SimpleDateFormat("HH:mm:ss").format(Date())} [${getServerName()}]:$messages"
                SendServer(line, 1.toString() + "")

                Log.log("服务端线程 尝试获取$CHAT_LOCKER")
                ReceiveServer.chatLogSem.acquire()
                try {
                    //将信息添加到客户端聊天记录中
                    textMessage.append("$line\r\n")
                    chatLogger.add(line)
                } catch (e: Exception) {
                    e.printStackTrace()
                }finally {
                    ReceiveServer.chatLogSem.release()
                    Log.log("服务端线程 释放$CHAT_LOCKER")
                }
                message.text = null//消息框设置为空
            } catch (e1: IOException) {
                JOptionPane.showMessageDialog(Main.mainFrame, "发送失败！")
            }

        }
    }

    //获取服务端名称
    private fun getServerName(): String {
        return OptionPanel.serverName.text
    }

    companion object {
        val chatLogger = ArrayList<String>()//聊天记录
        lateinit var textMessage: JTextArea//聊天记录文本区域
        lateinit var userList: JList<String>//用户列表
    }
}
