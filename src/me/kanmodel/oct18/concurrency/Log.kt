package me.kanmodel.oct18.concurrency

import me.kanmodel.oct18.concurrency.gui.LogPanel

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: kgdwhsk
 * Date: 2018-10-05
 * Time: 11:44
 */
object Log {
    val logPanel = LogPanel()

    fun log(str: String){
        logPanel.log(str)
    }
}