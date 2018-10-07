package me.kanmodel.oct18.concurrency.gui

import java.awt.Dimension
import javax.swing.JTabbedPane

class TabbedPanel : JTabbedPane() {
    init {
        maximumSize = Dimension(600, 2000)
        preferredSize = Dimension(600, 600)
        minimumSize = Dimension(600, 500)

        addTab("��ӭ", WelcomePanel.welcomePanel)
        addTab("�����¼", ChatLogPanel())
        addTab("ѡ��", OptionPanel.optionPanel)

        addChangeListener {
            when (selectedIndex) {
                0 -> WelcomePanel.refresh()
            }
        }
    }
}
