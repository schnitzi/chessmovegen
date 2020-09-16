package org.computronium.chess

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.awt.*
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.File
import javax.swing.*
import kotlin.system.exitProcess

/**
 * Utility for browsing chess starting positions and the resulting set of move positions.
 */
internal class ChessBoardFrame(testCases: Array<TestCase>) : JFrame() {

    init {

        val leftPanel = ChessBoardPanel(testCases.map { it.start }.toTypedArray())
        val rightPanel = ChessBoardPanel(arrayOf())

        leftPanel.fenComboBox.addActionListener {
            rightPanel.setFenList(testCases[leftPanel.fenComboBox.selectedIndex].expected)
        }

        layout = GridLayout(0, 2)
        add(leftPanel)
        add(rightPanel)

//        font = Font("Sans-Serif", Font.PLAIN, 52)
        size = Dimension(850, 600)

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(windowEvent: WindowEvent?) {
                exitProcess(0)
            }
        })

        setLocationRelativeTo(null)
        this.isFocusable = true
        this.requestFocus()

        leftPanel.fenComboBox.selectedIndex = 0
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val r = Runnable {
                val jsonString: String = File(args[0]).readText(Charsets.UTF_8)
                val testCaseType = object : TypeToken<List<TestCase>>() {}.type
                val testCases : List<TestCase> = Gson().fromJson(jsonString, testCaseType)

                val frame = ChessBoardFrame(testCases.toTypedArray())
                frame.isVisible = true
            }
            SwingUtilities.invokeLater(r)
        }
    }
}
