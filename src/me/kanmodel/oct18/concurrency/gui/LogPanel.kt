package me.kanmodel.oct18.concurrency.gui

import java.awt.*
import java.text.SimpleDateFormat
import java.util.*
import javax.swing.*

class LogPanel : JPanel() {
    private var count = 0
    private val label = JLabel("Log:")
    private val text = JTextArea()
    //    private val textPanel = JPanel(GridLayout())
    private val scrollTextPanel = JScrollPane(text)

    init {
        background = Color.white
//        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        layout = BorderLayout()

        border = BorderFactory.createRaisedSoftBevelBorder()

//        label.horizontalAlignment = SwingConstants.RIGHT
//        label.preferredSize = Dimension(180, 20)

        text.lineWrap = true
        text.isEditable = false
        text.append("Æô¶¯ÓÚ " + SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Date()))
//        textPanel.add(text)
        scrollTextPanel.horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        scrollTextPanel.verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS
        scrollTextPanel.minimumSize = Dimension(200, 580)

//        label.minimumSize = Dimension(300, 20)
//        label.border = BorderFactory.createRaisedSoftBevelBorder()

        add(BorderLayout.NORTH, label)
        add(BorderLayout.CENTER, scrollTextPanel)
    }

    fun log(str: String) {
        val time = SimpleDateFormat("HH:mm:ss").format(Date())
        val txt = "\n$time - ${++count}: $str"
        text.append(txt)
        text.selectAll()
        print(txt)
    }
}
