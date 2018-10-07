package me.kanmodel.oct18.concurrency

import me.kanmodel.oct18.concurrency.gui.MainFrame
import javax.swing.SwingUtilities

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: kgdwhsk
 * Date: 2018-09-29
 * Time: 10:59
 */
object Main {
    val mainFrame = MainFrame()

    @JvmStatic
    fun main(args: Array<String>){
        SwingUtilities.invokeLater {
            mainFrame.isVisible = true
        }
        /*SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        // �˴����� �¼������߳�
                        createGUI();
                    }
                }
        );*/
    }
}