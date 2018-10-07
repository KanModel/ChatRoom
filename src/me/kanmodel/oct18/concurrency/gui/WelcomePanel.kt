package me.kanmodel.oct18.concurrency.gui

import javax.swing.*

class WelcomePanel : JPanel() {
    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)

        add(JLabel("Welcome"))

        ipAddress = JLabel("服务器地址: ${OptionPanel.ipText.text}:${OptionPanel.portText.text}")
        add(ipAddress)
    }

    companion object {
        val welcomePanel = WelcomePanel()
        internal lateinit var ipAddress: JLabel

        fun refresh(){
            ipAddress.text = "服务器地址: ${OptionPanel.ipText.text}:${OptionPanel.portText.text}"
        }
    }
}
