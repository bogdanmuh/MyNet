import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.lang.Exception
import java.net.Socket
import kotlin.concurrent.thread

class SocketIO(val socket: Socket) {

    private var stop = false
    private val socketClosedListener = mutableListOf<()->Unit>()
    private val messageListeners = mutableListOf<(String)-> Unit>()

    fun addSocketClosedListener(l: ()->Unit){ socketClosedListener.add(l) }

    fun removeSocketClosedListener(l: ()->Unit){ socketClosedListener.remove(l) }

    fun addMessageListener(l:(String)-> Unit){ messageListeners.add(l) }

    fun removeMessageListener(l:(String)-> Unit){ messageListeners.remove(l) }

    fun stop(){
        stop = true
        socket.close()
    }

    fun startDataReceiving() {
        stop = false
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val br = BufferedReader(InputStreamReader(socket.getInputStream()))
                while (!stop) {
                    val data = br.readLine()
                    if (data!=null)
                        messageListeners.forEach { l -> l(data) }
                    else { throw IOException("Связь прервалась") }
                }
            } catch (ex: Exception){ messageListeners.forEach { l -> ex.message?.let { l(it) } } }
            finally {
                socket.close()
                socketClosedListener.forEach{it()}
            }
        }
    }

    fun sendData(data: String): Boolean{
        try {
            val pw = PrintWriter(socket.getOutputStream())
            pw.println(data)
            pw.flush()
            return true
        } catch (ex: Exception){
            return false
        }
    }
    fun send(data: String): Boolean{
        return try {
            val pw = PrintWriter(socket.getOutputStream())
            pw.println(data)
            pw.flush()
            true
        } catch (ex: Exception){
            false
        }
    }
}