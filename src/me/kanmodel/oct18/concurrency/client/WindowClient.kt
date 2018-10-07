package me.kanmodel.oct18.concurrency.client

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
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.JOptionPane
import javax.swing.JScrollPane
import javax.swing.JTextArea
import javax.swing.JTextField
import javax.swing.border.TitledBorder

class WindowClient {
    internal lateinit var port: JTextField
    internal lateinit var name: JTextField
    internal lateinit var ip: JTextField
    internal lateinit var message: JTextField
    internal lateinit var send: JButton

    //初始化窗体
    init {
        init()
    }

    //窗体初始化内容
    fun init() {//采用绝对布局
        window = JFrame("客户端")
        window.layout = null
        window.setBounds(200, 200, 500, 400)
        window.isResizable = false

        val label = JLabel("主机IP:")
        label.setBounds(10, 8, 50, 30)
        window.add(label)

        ip = JTextField()
        ip.setBounds(55, 8, 60, 30)
        ip.text = "127.0.0.1"
        window.add(ip)


        val label1 = JLabel("端口号:")
        label1.setBounds(125, 8, 50, 30)
        window.add(label1)

        port = JTextField()
        port.setBounds(170, 8, 40, 30)
        port.text = "30000"
        window.add(port)

        val names = JLabel("用户名:")
        names.setBounds(220, 8, 55, 30)
        window.add(names)

        name = JTextField()
        name.setBounds(265, 8, 60, 30)
        name.text = "客户端1"
        window.add(name)

        link = JButton("连接")
        link.setBounds(335, 8, 75, 30)
        window.add(link)

        exit = JButton("退出")
        exit.setBounds(415, 8, 75, 30)
        window.add(exit)

        val label2 = JLabel("用户列表")
        label2.setBounds(40, 40, 80, 30)
        window.add(label2)

        user = JList()
        val scrollPane = JScrollPane(user)//设置滚动条
        scrollPane.setBounds(10, 70, 120, 220)
        window.add(scrollPane)

        textMessage = JTextArea()
        textMessage.setBounds(135, 70, 340, 220)
        textMessage.isEditable = false//文本不可编辑
        textMessage.border = TitledBorder("聊天记录")//设置标题
        //文本内容换行的两个需要配合着用
        textMessage.lineWrap = true//设置文本内容自动换行，在超出文本区域时，可能会切断单词
        textMessage.wrapStyleWord = true//设置以自动换行，以单词为整体，保证单词不会被切断
        val scrollPane1 = JScrollPane(textMessage)//设置滚动条
        scrollPane1.setBounds(135, 70, 340, 220)
        window.add(scrollPane1)

        message = JTextField()
        message.setBounds(10, 300, 360, 50)
        message.text = null
        window.add(message)

        send = JButton("发送")
        send.setBounds(380, 305, 70, 40)
        window.add(send)

        myEvent()//添加监听事件
        window.isVisible = true//设置窗体可见
    }


    fun myEvent() {//事件监听
        window.addWindowListener(object : WindowAdapter() {
            //退出窗体
            override fun windowClosing(e: WindowEvent?) {
                //如果仍在连接，发信息给服务端，并退出
                if (socket != null && socket!!.isConnected) {
                    try {
                        SendClient(socket!!, getName(), "3")
                    } catch (e1: IOException) {
                        e1.printStackTrace()
                    }

                }
                System.exit(0)
            }
        })

        //关闭连接
        exit.addActionListener {
            //如果仍在连接，将信息发给服务端
            if (socket == null) {
                JOptionPane.showMessageDialog(window, "已关闭连接")
            } else if (socket != null && socket!!.isConnected) {
                try {
                    SendClient(socket!!, getName(), "3")//发送信息给服务端
                    link.text = "连接"
                    exit.text = "已退出"
                    socket!!.close()//关闭socket
                    socket = null
                } catch (e1: IOException) {
                    e1.printStackTrace()
                }

            }
        }

        //建立连接
        link.addActionListener {
            //判断是否已经连接成功
            if (socket != null && socket!!.isConnected) {
                JOptionPane.showMessageDialog(window, "已经连接成功！")
            } else {
                val ipString = ip.text//获取ip地址
                val portClient = port.text//获取端口号

                if ("" == ipString || "" == portClient) {//判断获取内容是否为空
                    JOptionPane.showMessageDialog(window, "ip或端口号为空！")
                } else {
                    try {
                        val ports = Integer.parseInt(portClient)//将端口号转为整形
                        socket = Socket(ipString, ports)//建立连接
                        link.text = "已连接"//更改button显示信息
                        exit.text = "退出"
                        SendClient(socket!!, getName(), "2")//发送该客户端名称至服务器
                        Thread(ReceiveClient(socket!!)).start()//启动接收线程
                    } catch (e2: Exception) {
                        JOptionPane.showMessageDialog(window, "连接未成功！可能是ip或端口号格式不对，或服务器未开启。")
                    }

                }
            }
        }

        //点击按钮发送信息
        send.addActionListener { sendMsg() }

        //按回车发送信息
        message.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent?) {
                if (e!!.keyCode == KeyEvent.VK_ENTER) {
                    sendMsg()
                }
            }
        })
    }

    //发送信息的方法
    fun sendMsg() {
        val messages = message.text//获取文本框内容
        if ("" == messages) {//判断信息是否为空
            JOptionPane.showMessageDialog(window, "内容不能为空！")
        } else if (socket == null || !socket!!.isConnected) {//判断是否已经连接成功
            JOptionPane.showMessageDialog(window, "未连接成功，不能发送消息！")
        } else {
            try {
                //发送信息
                SendClient(socket!!, getName() + "：" + messages, "1")
                message.text = null//文本框内容设置为空
            } catch (e1: IOException) {
                JOptionPane.showMessageDialog(window, "信息发送失败！")
            }

        }
    }

    //获取客户端名称
    fun getName(): String {
        return name.text
    }

    companion object {
        lateinit var window: JFrame
        lateinit var link: JButton
        lateinit var exit: JButton
        lateinit var textMessage: JTextArea
        var socket: Socket? = null
        lateinit var user: JList<String>

        //主函数入口
        @JvmStatic
        fun main(args: Array<String>) {
            WindowClient()
        }
    }
}
