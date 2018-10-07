package me.kanmodel.oct18.concurrency.gui

import me.kanmodel.oct18.concurrency.Log
import me.kanmodel.oct18.concurrency.Main
import me.kanmodel.oct18.concurrency.server.SendServer
import me.kanmodel.oct18.concurrency.server.StartServer
import java.io.IOException
import javax.swing.*

class OptionPanel : JPanel() {
    private val btn = JButton("Test")
    private val resizeToggleBtn = JToggleButton("������С: ��")
    private val serverToggleBtn = JToggleButton("������: ��")

    private val port: Int
        get() {
            val port = portText.text
            return if ("" == port) {
                JOptionPane.showMessageDialog(Main.mainFrame, "�˿ں�Ϊ��")
                0
            } else {
                Integer.parseInt(port)
            }
        }

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)

        btn.addActionListener {
            Log.log("�����ťbtn�����ťbtn�����ťbtn�����ťbtn�����ťbtn�����ťbtn�����ťbtn�����ťbtn")
        }

/*        resizeToggleBtn.addChangeListener{
            val btn = it.source as JToggleButton
            Log.log("${btn.text} is selected? ${btn.isSelected}")
            if (btn.isSelected) {
                btn.text = "������С: ��"
                Main.mainFrame.isResizable = true
            } else {
                btn.text = "������С: ��"
                Main.mainFrame.isResizable = false
            }
        }*/

        resizeToggleBtn.addActionListener {
            val btn = it.source as JToggleButton
            Log.log("${btn.text} is selected? ${btn.isSelected}")
            if (btn.isSelected) {
                btn.text = "������С: ��"
                Main.mainFrame.isResizable = true
            } else {
                btn.text = "������С: ��"
                Main.mainFrame.isResizable = false
            }
        }

        serverToggleBtn.addActionListener {
            val btn = it.source as JToggleButton
//            Log.log("${btn.text} is selected? ${btn.isSelected}")
            if (btn.isSelected) {
                btn.text = "������: ��"

                serverPort = port
                if (serverPort != 0) {
                    try {
                        StartServer.flag = true
                        Thread(StartServer(serverPort)).start()
                        Log.log("������������${ipText.text}:$port")
                    } catch (e: IOException) {
                        JOptionPane.showMessageDialog(Main.mainFrame, "����������ʧ��")
                        Log.log("����������ʧ��")
                    }
                }
/*                ports = port//��ȡ�˿ں�
                if (ports != 0) {
                    try {
                        StartServer.flag = true//�ı����˽���ѭ�����
                        Thread(StartServer(ports)).start() //��������˽����߳�
                        start.text = "������"
                        exit.text = "�ر�"
                    } catch (e1: IOException) {
                        JOptionPane.showMessageDialog(window, "����ʧ��")
                    }
                }*/
            } else {
                btn.text = "������: ��"

                if (StartServer.userList != null && StartServer.userList!!.size != 0) {
                    try {
                        SendServer(StartServer.userList!!, "", 4.toString() + "")
                    } catch (e1: IOException) {
                        e1.printStackTrace()
                    }
                }
                try {
                    StartServer.serverSocket!!.close()//�رշ����
                    StartServer.serverSocket = null
                    StartServer.userList = null
                    StartServer.flag = false//�ı�����ѭ�����
                    Log.log("�������ر�")
                } catch (e1: IOException) {
                    e1.printStackTrace()
                    Log.log("�������ر��쳣")
                }
            }
        }

        val hBox1 = Box.createHorizontalBox()
        hBox1.add(btn)

        val hBox2 = Box.createHorizontalBox()
        hBox2.add(resizeToggleBtn)

        val hBox3 = Box.createHorizontalBox()
        hBox3.add(JLabel("IP:"))
        ipText = JTextField("127.0.0.1")
        ipText.horizontalAlignment = JTextField.CENTER
        hBox3.add(ipText)

        val hBox4 = Box.createHorizontalBox()
        hBox4.add(JLabel("Port:"))
        portText = JTextField("20018")
        portText.horizontalAlignment = JTextField.CENTER
        hBox4.add(portText)

        val hBox5 = Box.createHorizontalBox()
        hBox5.add(JLabel("����:"))
        serverName = JTextField("�����")
        serverName.horizontalAlignment = JTextField.CENTER
        hBox5.add(serverName)

        val hBox6 = Box.createHorizontalBox()
        hBox6.add(serverToggleBtn)

        val hBoxl = Box.createHorizontalBox()
        hBoxl.add(JLabel("KanModel@2018"))

        add(hBox1)
        add(hBox2)
        add(hBox3)
        add(hBox4)
        add(hBox5)
        add(hBox6)
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
