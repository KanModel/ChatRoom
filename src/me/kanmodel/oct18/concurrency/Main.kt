package me.kanmodel.oct18.concurrency

import me.kanmodel.oct18.concurrency.gui.MainFrame
import javax.swing.SwingUtilities

/**
 * License URI: https://www.gnu.org/licenses/gpl-2.0.html
 * Author: KanModel
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
                        // 此处处于 事件调度线程
                        createGUI();
                    }
                }
        );*/
    }
}