package me.kanmodel.oct18.concurrency.gui

import me.kanmodel.oct18.concurrency.net.DataManager.userSockets
import me.kanmodel.oct18.concurrency.net.ServerSender
import java.awt.Dimension
import java.awt.event.*
import java.io.IOException
import javax.swing.JFrame
import javax.swing.WindowConstants

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: kgdwhsk
 * Date: 2018-09-29
 * Time: 11:01
 */
class MainFrame(myTitle: String = "聊天室") : JFrame(myTitle) {

    init {
        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        size = Dimension(800, 600)
        minimumSize = Dimension(800, 600)
        isResizable = false
        setLocationRelativeTo(null)

        contentPane = MainPanel()

        addWindowListener(object : WindowAdapter() {
            //关闭窗体
            override fun windowClosing(e: WindowEvent?) {
                //如果有客户端存在，发信息给客户端，并退出
                if (userSockets.size != 0) {
                    try {
                        ServerSender("", "4")//4代表服务端退出
                    } catch (e1: IOException) {
                        e1.printStackTrace()
                    }

                }
                System.exit(0)//退出窗体
            }
        })
    }
}