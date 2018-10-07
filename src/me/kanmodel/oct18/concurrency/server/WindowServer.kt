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

    //获取端口号
    //判断端口号是否为空
    //返回整形的端口号
    private val port: Int
        get() {
            val port = portServer.text
            return if ("" == port) {
                JOptionPane.showMessageDialog(window, "端口号为口")
                0
            } else {
                Integer.parseInt(port)
            }
        }

    //初始化窗体
    init {
        init()
    }

    //初始化内容
    fun init() {//采用绝对布局
        window = JFrame("服务端")
        window.layout = null
        window.setBounds(200, 200, 500, 400)
        window.isResizable = false//不可改变大小

        val label1 = JLabel("端口号:")
        label1.setBounds(10, 8, 50, 30)
        window.add(label1)

        portServer = JTextField()
        portServer.setBounds(60, 8, 100, 30)
        portServer.text = "30000"
        window.add(portServer)

        val names = JLabel("用户名:")
        names.setBounds(180, 8, 55, 30)
        window.add(names)

        name = JTextField()
        name.setBounds(230, 8, 60, 30)
        name.text = "服务端"
        window.add(name)

        start = JButton("启动")
        start.setBounds(300, 8, 80, 30)
        window.add(start)

        exit = JButton("关闭")
        exit.setBounds(390, 8, 80, 30)
        window.add(exit)


        val label2 = JLabel("用户列表")
        label2.setBounds(40, 40, 80, 30)
        window.add(label2)


        user = JList()
        val scrollPane = JScrollPane(user)//添加滚动条
        scrollPane.setBounds(10, 70, 120, 220)
        window.add(scrollPane)

        textMessage = JTextArea()
        textMessage.setBounds(135, 70, 340, 220)
        textMessage.border = TitledBorder("聊天记录")//设置标题
        textMessage.isEditable = false//不可编辑
        //文本内容换行的两个需要配合着用
        textMessage.lineWrap = true//设置文本内容自动换行，在超出文本区域时，可能会切断单词
        textMessage.wrapStyleWord = true//设置以自动换行，以单词为整体，保证单词不会被切断
        val scrollPane1 = JScrollPane(textMessage)//设置滚动条
        scrollPane1.setBounds(135, 70, 340, 220)
        window.add(scrollPane1)

        message = JTextField()
        message.setBounds(10, 300, 360, 50)

        window.add(message)

        send = JButton("发送")
        send.setBounds(380, 305, 70, 40)
        window.add(send)

        myEvent()    //添加监听事件
        window.isVisible = true
    }

    private fun myEvent() {
        window.addWindowListener(object : WindowAdapter() {
            //关闭窗体
            override fun windowClosing(e: WindowEvent?) {
                //如果有客户端存在，发信息给客户端，并退出
                if (StartServer.userList != null && StartServer.userList!!.size != 0) {
                    try {
                        SendServer(StartServer.userList!!, "", "4")//4代表服务端退出
                    } catch (e1: IOException) {
                        e1.printStackTrace()
                    }

                }
                System.exit(0)//退出窗体
            }
        })

        exit.addActionListener {
            if (StartServer.serverSocket == null || StartServer.serverSocket!!.isClosed()) {//如果已退出，弹窗提醒
                JOptionPane.showMessageDialog(window, "服务器已关闭")
            } else {
                //发信息告诉客户端，要退出
                if (StartServer.userList != null && StartServer.userList!!.size != 0) {
                    try {
                        SendServer(StartServer.userList!!, "", 4.toString() + "")
                    } catch (e1: IOException) {
                        e1.printStackTrace()
                    }

                }
                try {
                    start.text = "启动"
                    exit.text = "已关闭"
                    StartServer.serverSocket!!.close()//关闭服务端
                    StartServer.serverSocket = null
                    StartServer.userList = null
                    StartServer.flag = false//改变服务端循环标记
                } catch (e1: IOException) {
                    e1.printStackTrace()
                }

            }
        }

        //开启服务端
        start.addActionListener {
            //如果服务端已经开启，弹窗提醒服务端已开启
            if (StartServer.serverSocket != null && !StartServer.serverSocket!!.isClosed()) {
                JOptionPane.showMessageDialog(window, "服务器已经启动")
            } else {
                ports = port//获取端口号
                if (ports != 0) {
                    try {
                        StartServer.flag = true//改变服务端接收循环标记
                        Thread(StartServer(ports)).start() //开启服务端接收线程
                        start.text = "已启动"
                        exit.text = "关闭"
                    } catch (e1: IOException) {
                        JOptionPane.showMessageDialog(window, "启动失败")
                    }
                }
            }
        }

        //点击按钮发送消息
        send.addActionListener { sendMsg() }

        //按回车发送消息
        message.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent?) {
                if (e!!.keyCode == KeyEvent.VK_ENTER) {
                    sendMsg()
                }
            }
        })
    }

    //发送消息方法
    fun sendMsg() {
        val messages = message.text
        //判断内容是否为空
        if (messages == "") {
            JOptionPane.showMessageDialog(window, "内容不能为空！")
        } else if (StartServer.userList == null || StartServer.userList!!.size == 0) {//判断是否已经连接成功
            JOptionPane.showMessageDialog(window, "未连接成功，不能发送消息！")
        } else {
            try {
                //将信息发送给所有客户端
                SendServer(StartServer.userList!!, getName() + "：" + messages, 1.toString() + "")
                //将信息添加到客户端聊天记录中
                WindowServer.textMessage.append(getName() + "：" + messages + "\r\n")
                message.text = null//消息框设置为空
            } catch (e1: IOException) {
                JOptionPane.showMessageDialog(window, "发送失败！")
            }

        }
    }

    //获取服务端名称
    private fun getName(): String {
        return name.text
    }

    companion object {
        lateinit var window: JFrame
        lateinit var textMessage: JTextArea//聊天记录
        lateinit var user: JList<String>//用户列表
        var ports: Int = 0

        //主函数入口
        @JvmStatic
        fun main(args: Array<String>) {
            WindowServer()
        }
    }
}


