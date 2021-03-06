package me.kanmodel.oct18.concurrency.gui

import me.kanmodel.oct18.concurrency.util.Log
import java.awt.Color
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
        layout = mLayout

        background = Color.gray

        val tabbedPanel = TabbedPanel()
        add(tabbedPanel)
        Log.log("选项面板加载完毕")
        add(Log.logPanel)
        Log.log("Log面板加载完毕")

        Log.log("各组件面板加载完毕")
    }
}