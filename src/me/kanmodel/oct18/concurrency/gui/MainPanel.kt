package me.kanmodel.oct18.concurrency.gui

import me.kanmodel.oct18.concurrency.Log
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.Point
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import javax.swing.*

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: kgdwhsk
 * Date: 2018-10-05
 * Time: 9:17
 */
class MainPanel : JPanel() {
    init {
        val mLayout = BoxLayout(this, BoxLayout.X_AXIS)
//        layout = BorderLayout()
        layout = mLayout

        background = Color.gray

        val tabbedPanel = TabbedPanel()
//        tabbedPanel.border = BorderFactory.createRaisedSoftBevelBorder()

//        val rigidArea = Box.createRigidArea(Dimension(200, 600))


//        add(label1)
//        add(BorderLayout.CENTER, tabbedPanel)
//        add(BorderLayout.EAST, Log.logPanel)
        add(tabbedPanel)
        add(Log.logPanel)

        Log.log("gg")
    }
}