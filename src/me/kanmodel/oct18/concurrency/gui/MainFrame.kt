package me.kanmodel.oct18.concurrency.gui

import me.kanmodel.oct18.concurrency.net.SendServer
import me.kanmodel.oct18.concurrency.net.StartServer
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
class MainFrame(myTitle: String = "������") : JFrame(myTitle) {

    init {
        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        size = Dimension(800, 600)
        minimumSize = Dimension(800, 600)
        isResizable = false
        setLocationRelativeTo(null)

        contentPane = MainPanel()

        addWindowListener(object : WindowAdapter() {
            //�رմ���
            override fun windowClosing(e: WindowEvent?) {
                //����пͻ��˴��ڣ�����Ϣ���ͻ��ˣ����˳�
                if (StartServer.userSockets.size != 0) {
                    try {
                        SendServer("", "4")//4���������˳�
                    } catch (e1: IOException) {
                        e1.printStackTrace()
                    }

                }
                System.exit(0)//�˳�����
            }
        })
    }
}