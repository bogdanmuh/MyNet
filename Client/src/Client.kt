import java.awt.TextArea
import java.awt.TextField
import java.net.Socket
import javax.swing.GroupLayout
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JPanel
import kotlin.system.exitProcess

class Client(val host: String, val port: Int) {
    private val socket: Socket
    private val communicator: SocketIO

    private val messageListeners = mutableListOf<(String)-> Unit>()

    init{
        socket = Socket(host, port)
        communicator = SocketIO(socket)
    }
    fun addMessageListener(l:(String)-> Unit){ messageListeners.add(l)}
    fun removeMessageListener(l:(String)-> Unit){ messageListeners.remove(l) }

    fun stop(){ communicator.stop() }

    fun start(){ communicator.addMessageListener {
        messageListeners.forEach { l -> l(it) }
    }
        communicator.startDataReceiving()
    }

    fun send(data: String) { communicator.sendData(data) }

    fun addSessionFinishedListener(l: ()->Unit){ communicator.addSocketClosedListener(l) }

    fun removeSessionFinishedListener(l: ()->Unit){ communicator.removeSocketClosedListener(l) }
}