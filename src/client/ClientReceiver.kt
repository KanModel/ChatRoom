package client

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: kgdwhsk
 * Date: 2018-10-05
 * Time: 20:08
 */
import me.kanmodel.oct18.concurrency.util.Base64Util.base642pic
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.Socket

import javax.swing.JOptionPane
import sun.text.normalizer.UTF16.append
import javax.swing.SwingUtilities


//客户端接收线程
class ClientReceiver(private val s: Socket) : Runnable {
    override fun run() {
        try {
            val brIn = BufferedReader(InputStreamReader(s.getInputStream()))//信息接收流
            while (true) {
                val info = brIn.read().toChar()//读取信息流首字符，判断信息类型
                val line = brIn.readLine()//读取信息流内容

                if (info == '1') {//代表发送的是消息
                    if (line != "") {
                        SwingUtilities.invokeLater {
                            WindowClient.textMessage.append(line + "\r\n")    //将消息添加到文本域中
                            WindowClient.textMessage.caretPosition = WindowClient.textMessage.text.length//设置消息显示最新一行，也就是滚动条出现在末尾，显示最新一条输入的信息
                        }
                    }
                }

                if (info == '5') {//收到图片
                    if (line != "") {
                        WindowClient.tmpCount = WindowClient.tmpCount?.plus(1)
                        println("${WindowClient.tmpDir}\\t${WindowClient.tmpCount}.png")
                        base642pic(line, "${WindowClient.tmpDir}\\t${WindowClient.tmpCount}.png")
                        SwingUtilities.invokeLater {
                            WindowClient.textMessage.append("图片${WindowClient.tmpCount}\r\n")    //将消息添加到文本域中
                            WindowClient.textMessage.caretPosition = WindowClient.textMessage.text.length//设置消息显示最新一行，也就是滚动条出现在末尾，显示最新一条输入的信息
                        }
                    }
                }

                if (info == '2' || info == '3') {//有新用户加入或退出，2为加入，3为退出
                    val sub = line.substring(1, line.length - 1)//去掉字符串头尾中括号
                    SwingUtilities.invokeLater {
                        val data = sub.split(", ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()//分割姓名列表
                        WindowClient.userJL.clearSelection()
                        WindowClient.userJL.setListData(data)
                    }
                }

                if (info == '4') {//4代表服务端退出
                    WindowClient.link.text = "连接"
                    WindowClient.exit.text = "已退出"
                    WindowClient.socket!!.close()
                    WindowClient.socket = null
                    val data = Array<String>(0) { "" }
                    WindowClient.userJL.setListData(data)
                    throw InterruptedException()
                }
            }
        } catch (e: IOException) {
            JOptionPane.showMessageDialog(WindowClient.window, "客户端已退出连接")
        } catch (ei: InterruptedException) {
            JOptionPane.showMessageDialog(WindowClient.window, "服务器关闭")
        }
    }
}


