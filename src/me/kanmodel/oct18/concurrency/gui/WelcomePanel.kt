package me.kanmodel.oct18.concurrency.gui

import javax.swing.*

class WelcomePanel : JPanel() {
    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)

        add(JLabel("Welcome"))

        ipAddress = JLabel("��������ַ: ${OptionPanel.ipText.text}:${OptionPanel.portText.text}")
        add(ipAddress)

        val introLabel = JLabel()
        introLabel.text = "<html><h1>ʹ����֪</h1><h3>�����¼��壺</h3><p>����������������ڷ��ͽ�����Ϣ</p><h3>ѡ����壺</h3><p>�������÷�����ip���˿ڡ����ֵ�ѡ��</p></html>"
        add(introLabel)
    }

    companion object {
        val welcomePanel = WelcomePanel()
        internal lateinit var ipAddress: JLabel

        fun refresh(){
            ipAddress.text = "��������ַ: ${OptionPanel.ipText.text}:${OptionPanel.portText.text}"
        }
    }
}