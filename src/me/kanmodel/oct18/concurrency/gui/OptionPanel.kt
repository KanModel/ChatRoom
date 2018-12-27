package me.kanmodel.oct18.concurrency.gui

import me.kanmodel.oct18.concurrency.util.Log
import me.kanmodel.oct18.concurrency.Main
import me.kanmodel.oct18.concurrency.net.DataManager.userSockets
import me.kanmodel.oct18.concurrency.net.ServerSender
import me.kanmodel.oct18.concurrency.net.StartServer
import java.awt.Dimension
import java.io.IOException
import javax.swing.*

class OptionPanel : JPanel() {
    private val btn = JButton("Test")
    private val resizeToggleBtn = JToggleButton("调整大小: 关")
    private val serverToggleBtn = JToggleButton("服务器: 关")

    private val port: Int
        get() {
            val port = portText.text
            return if ("" == port) {
                JOptionPane.showMessageDialog(Main.mainFrame, "端口号为口")
                0
            } else {
                Integer.parseInt(port)
            }
        }

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)

        btn.addActionListener {
            Log.log("点击按钮btn点击按钮btn点击按钮btn点击按钮btn点击按钮btn点击按钮btn点击按钮btn点击按钮btn")
        }

        resizeToggleBtn.addActionListener {
            val btn = it.source as JToggleButton
            Log.log("窗口调整属性设置为 ${btn.isSelected}")
            if (btn.isSelected) {
                btn.text = "调整大小: 开"
                Main.mainFrame.isResizable = true
            } else {
                btn.text = "调整大小: 关"
                Main.mainFrame.isResizable = false
            }
        }

        serverToggleBtn.addActionListener {
            val btn = it.source as JToggleButton
            if (btn.isSelected) {
                btn.text = "服务器: 开"

                ipText.isEditable = false
                portText.isEditable = false
                serverName.isEditable = false
                serverPort = port
                if (serverPort != 0) {
                    try {
                        StartServer.flag = true
                        Thread(StartServer(serverPort)).start()
                        Log.log("服务器启动于${ipText.text}:$port")
                    } catch (e: IOException) {
                        JOptionPane.showMessageDialog(Main.mainFrame, "服务器启动失败")
                        Log.log("服务器启动失败")
                    }
                }
            } else {
                btn.text = "服务器: 关"

                ipText.isEditable = true
                portText.isEditable = true
                serverName.isEditable = true
                if (userSockets.size != 0) {
                    try {
                        ServerSender("", 4.toString() + "")
                    } catch (e1: IOException) {
                        e1.printStackTrace()
                    }
                }
                try {
                    StartServer.serverSocket!!.close()//关闭服务端
                    StartServer.serverSocket = null
                    userSockets.clear()
                    StartServer.userNames.clear()
                    StartServer.flag = false//改变服务端循环标记
                    Log.log("服务器关闭")
                } catch (e1: IOException) {
                    e1.printStackTrace()
                    Log.log("服务器关闭异常")
                }
            }
        }

        val hBox1 = Box.createHorizontalBox()
        hBox1.add(btn)

        val hBox2 = Box.createHorizontalBox()
        hBox2.add(resizeToggleBtn)

        val hBox3 = Box.createHorizontalBox()
        hBox3.add(JLabel(" IP :   "))
        ipText = JTextField("127.0.0.1")
        ipText.horizontalAlignment = JTextField.RIGHT
        ipText.maximumSize = Dimension(400, 30)
        ipText.minimumSize = Dimension(400, 30)
        hBox3.add(ipText)

        val hBox4 = Box.createHorizontalBox()
        hBox4.add(JLabel("Port:"))
//        hBox4.add(Box.createHorizontalStrut(200))
        portText = JTextField("20018")
        portText.horizontalAlignment = JTextField.RIGHT
        portText.maximumSize = Dimension(400, 30)
        portText.minimumSize = Dimension(400, 30)
        hBox4.add(portText)

        val hBox5 = Box.createHorizontalBox()
        hBox5.add(JLabel("名字:"))
//        hBox5.add(Box.createHorizontalStrut(200))
        serverName = JTextField("服务端")
        serverName.horizontalAlignment = JTextField.RIGHT
        serverName.maximumSize = Dimension(400, 30)
        serverName.minimumSize = Dimension(400, 30)
        hBox5.add(serverName)

        val hBox6 = Box.createHorizontalBox()
        hBox6.add(serverToggleBtn)

        val hBoxl = Box.createHorizontalBox()
        hBoxl.add(JLabel("KanModel@2018"))

//        add(Box.createVerticalStrut(5))
//        add(hBox1)
        add(Box.createVerticalStrut(5))
        add(hBox2)
        add(Box.createVerticalStrut(5))
        add(hBox3)
        add(Box.createVerticalStrut(5))
        add(hBox4)
        add(Box.createVerticalStrut(5))
        add(hBox5)
        add(Box.createVerticalStrut(5))
        add(hBox6)
        add(Box.createVerticalGlue())
        add(hBoxl)
    }

    companion object {
        var serverPort: Int = 0

        lateinit var portText: JTextField
        lateinit var ipText: JTextField
        lateinit var serverName: JTextField

        val optionPanel = OptionPanel()
    }
}
