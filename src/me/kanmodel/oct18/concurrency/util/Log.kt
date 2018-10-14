package me.kanmodel.oct18.concurrency.util

import me.kanmodel.oct18.concurrency.gui.LogPanel
import java.lang.Exception
import java.util.concurrent.locks.ReentrantLock

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: kgdwhsk
 * Date: 2018-10-05
 * Time: 11:44
 */
object Log {
    private val lock: ReentrantLock = ReentrantLock(true)
    val logPanel = LogPanel()

    fun log(str: String){
        lock.lock()
        try {
            logPanel.log(str)
        } catch (e: Exception) {

        }finally {
            lock.unlock()
        }
    }
}