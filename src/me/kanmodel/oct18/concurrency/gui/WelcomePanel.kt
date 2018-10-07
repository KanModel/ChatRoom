package me.kanmodel.oct18.concurrency.gui

import javax.swing.*

class WelcomePanel : JPanel() {
    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)

        add(JLabel("Welcome"))

        ipAddress = JLabel("服务器地址: ${OptionPanel.ipText.text}:${OptionPanel.portText.text}")
        add(ipAddress)

        val introLabel = JLabel()
        introLabel.text = "<html><h1>使用须知</h1><h3>聊天记录面板：</h3><p>服务器开启后可用于发送接收消息</p><h3>选项面板：</h3><p>用于设置服务器ip、端口、名字等选项</p></html>"
        add(introLabel)
    }

    companion object {
        val welcomePanel = WelcomePanel()
        internal lateinit var ipAddress: JLabel

        fun refresh(){
            ipAddress.text = "服务器地址: ${OptionPanel.ipText.text}:${OptionPanel.portText.text}"
        }
    }
}
