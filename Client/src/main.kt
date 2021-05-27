import java.awt.TextArea
import java.awt.TextField
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*
import javax.swing.GroupLayout
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JPanel
import kotlin.concurrent.thread
import kotlin.system.exitProcess

fun main() {
    val client = ClientWindow("Клиент")
    client.isVisible = true
}
class ClientWindow(title: String) : JFrame() {
    init {
        val client = Client("localhost", 5804)
        client.start()

        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        setTitle(title)
        val textField = TextField()
        val textArea = TextArea()

        val btn = JButton("Send")
        btn.addActionListener {
            textArea.append("Я: ${textField.text}\n")
            client.send(textField.text)
        }
        val mainPanel = JPanel()
        val gl = GroupLayout(mainPanel)
        gl.setVerticalGroup(
            gl.createSequentialGroup()
                .addGap(4)
                .addComponent(textArea, 200, 700, GroupLayout.DEFAULT_SIZE)
                .addGap(4)
                .addGroup(
                    gl.createParallelGroup()
                        .addComponent(textField)
                        .addGap(5)
                        .addComponent(btn)
                )
                .addGap(4)
        )
        gl.setHorizontalGroup(
            gl.createParallelGroup()
                .addComponent(textArea)
                .addGap(5)
                .addGroup(
                    gl.createSequentialGroup()
                        .addComponent(textField)
                        .addGap(5)
                        .addComponent(btn)
                )

        )

        mainPanel.add(textArea)
        mainPanel.add(textField)
        mainPanel.add(btn)
        mainPanel.layout = gl
        add(mainPanel)
        pack()

        client.addSessionFinishedListener {
            textArea.append("Работа с сервером завершена. Нажмите Enter для выхода...\n")
            exitProcess(0)
        }
        client.addMessageListener { textArea.append(it+"\n") }
    }
}