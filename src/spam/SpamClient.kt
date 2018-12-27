package spam

import java.awt.BorderLayout
import java.awt.Dimension
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JTextArea
import javax.swing.WindowConstants

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: kgdwhsk
 * Date: 2018-10-07
 * Time: 20:26
 */
class SpamClient {
    //客户端发送信息类
    internal class SendClient @Throws(IOException::class)
    constructor(s: Socket?, message: Any, info: String) {
        init {
            val messages = info + message
            val pwOut = PrintWriter(s!!.getOutputStream(), true)
            pwOut.println(messages)
        }
    }

    //客户端接收线程
    internal class ReceiveClient(private val s: Socket?) : Runnable {
        override fun run() {
            try {
                //信息接收流
                val brIn = BufferedReader(InputStreamReader(s!!.getInputStream()))
                while (true) {
                    val info = brIn.read().toChar()//读取信息流首字符，判断信息类型
                    val line = brIn.readLine()//读取信息流内容

                    if (info == '1') {//代表发送的是消息
                        //设置消息显示最新一行，也就是滚动条出现在末尾，显示最新一条输入的信息
                    }

                    if (info == '2' || info == '3') {//有新用户加入或退出，2为加入，3为退出
//                        val sub = line.substring(1, line.length - 1)
//                        val data = sub.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    }

                    if (info == '4') {//4代表服务端退出
                        s.close()
                        throw InterruptedException()
                    }
                }
            }  catch (ei: InterruptedException) {
                println("服务器关闭")
                btn2.isEnabled = false
                btn3.isEnabled = false
                flag = false
            }
        }
    }


    companion object {
        var amount = 10
        val sockets = ArrayList<Socket?>()

        val frame = JFrame()
        val btn = JButton("连接/断开")
        val btn2 = JButton("SPAM")
        val btn3 = JButton("SPAM^2")

        var flag = false

        @JvmStatic
        fun main(arg: Array<String>) {

            btn2.isEnabled = false
            btn3.isEnabled = false
            val amountJTA = JTextArea()
            amountJTA.text = amount.toString()
            frame.setLocationRelativeTo(null)
            frame.size = Dimension(600, 600)
            frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
            frame.add(btn)
            frame.add(BorderLayout.EAST, btn2)
            frame.add(BorderLayout.SOUTH, btn3)
            frame.add(BorderLayout.NORTH, amountJTA)
//            frame.pack()


            btn.addActionListener {
                amount = amountJTA.text.toInt()
                if (flag) {
                    flag = !flag
                    spamStop()
                    btn2.isEnabled = false
                    btn3.isEnabled = false
                } else {
                    flag = !flag
                    sockets.clear()
                    spamStart()
                    btn2.isEnabled = true
                    btn3.isEnabled = true
                }
            }

            btn2.addActionListener {
                sendSpam()
            }

            btn3.addActionListener {
                sendNSpam()
            }


            frame.isVisible = true
        }

        private fun spamStart() {
            for (i in 0 until amount) {
//                sockets[i] = Socket("127.0.0.1", 20018)
                sockets.add(Socket("127.0.0.1", 20018))
                SendClient(sockets[i], "客户端$i", "2")//发送该客户端名称至服务器
                Thread(ReceiveClient(sockets[i])).start()//启动接收线程
            }
        }

        private fun spamStop() {
            for (i in 0 until amount) {
                SendClient(sockets[i], "客户端$i", "3")//发送该客户端名称至服务器
                sockets[i]!!.close()
            }
            sockets.clear()
        }

        private fun sendSpam(){
//            SendClient(socket, getName() + "：" + messages, "1")

            for (i in 0 until amount) {
                SendClient(sockets[i], "gg!$i", "1")
            }
        }

        private fun sendNSpam(){
//            SendClient(socket, getName() + "：" + messages, "1")

            for (j in 0 until amount) {
                for (i in 0 until amount) {
                    SendClient(sockets[i], "gg!$i $j", "1")
                }
            }
        }
    }
}