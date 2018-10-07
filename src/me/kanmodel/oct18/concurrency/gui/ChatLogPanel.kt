package me.kanmodel.oct18.concurrency.gui

import me.kanmodel.oct18.concurrency.Main
import me.kanmodel.oct18.concurrency.server.SendServer
import me.kanmodel.oct18.concurrency.server.StartServer
import java.awt.BorderLayout
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.io.IOException
import javax.swing.*
import javax.swing.border.TitledBorder

class ChatLogPanel : JPanel() {
    private val message = JTextArea()
    private val sendBtn = JButton("发送")

    init {
        layout = BorderLayout()

        textMessage = JTextArea()
//        textMessage.setBounds(135, 70, 340, 220)
        textMessage.border = TitledBorder("聊天记录")//设置标题
        textMessage.isEditable = false//不可编辑
        //文本内容换行的两个需要配合着用
        textMessage.lineWrap = true//设置文本内容自动换行，在超出文本区域时，可能会切断单词
        textMessage.wrapStyleWord = true//设置以自动换行，以单词为整体，保证单词不会被切断
        add(BorderLayout.CENTER, JScrollPane(textMessage))

        user = JList()
        add(BorderLayout.WEST, JScrollPane(user))

        val panel = JPanel(BorderLayout())
        //按回车发送消息
        message.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent?) {
                if (e!!.keyCode == KeyEvent.VK_ENTER) {
                    sendMsg()
                }
            }
        })
        panel.add(BorderLayout.CENTER, message)
        //点击按钮发送消息
        sendBtn.addActionListener { sendMsg() }
        panel.add(BorderLayout.EAST, sendBtn)
        add(BorderLayout.SOUTH, panel)
    }

    fun sendMsg() {
        val messages = message.text
        //判断内容是否为空
        if (messages == "") {
            JOptionPane.showMessageDialog(Main.mainFrame, "内容不能为空！")
        } else if (StartServer.userList == null || StartServer.userList!!.size == 0) {//判断是否已经连接成功
            JOptionPane.showMessageDialog(Main.mainFrame, "未连接成功，不能发送消息！")
        } else {
            try {
                //将信息发送给所有客户端
                SendServer(StartServer.userList!!, getServerName() + "：" + messages, 1.toString() + "")
                //将信息添加到客户端聊天记录中
                textMessage.append(getServerName() + "：" + messages + "\r\n")
                message.text = ""//消息框设置为空
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
        lateinit var textMessage: JTextArea//聊天记录
        lateinit var user: JList<String>//用户列表
    }
}
