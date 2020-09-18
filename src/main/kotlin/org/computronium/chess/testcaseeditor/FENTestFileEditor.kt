package org.computronium.chess.testcaseeditor

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.computronium.chess.movegen.SearchNode
import java.awt.*
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.File
import javax.swing.*
import kotlin.system.exitProcess

/**
 * Utility to create and edit test files for chess move generators.  Boards referred to in short
 * for using FEN records (https://en.wikipedia.org/wiki/Forsyth%E2%80%93Edwards_Notation).  Test
 * files contain a set of starting positions (as FENs), and for each, the set of resulting FENs
 * that a correct move generator should generate.
 *
 * TODO show move along with resulting board
 * TODO buttons to move through boards
 * TODO select new fen after adding
 * TODO allow comments on data sets, start FENs, and result FENs
 * TODO dropdowns sorted by whether there are comments
 * TODO allow file open
 * TODO show filename in title, with "*" if modified
 * TODO allow start without filename
 * TODO labels above boards
 * TODO clean up bottom text
 * TODO allow board construction
 */
internal class FENTestFileEditor(private var testCases : List<TestCase> = listOf()) : JFrame() {

    private val leftPanel: ChessBoardPanel
    private val rightPanel: ChessBoardPanel

    init {

        jMenuBar = createJMenuBar()

        leftPanel = ChessBoardPanel(arrayOf())
        rightPanel = ChessBoardPanel(arrayOf())

        leftPanel.fenComboBox.addActionListener {
            if (leftPanel.fenComboBox.selectedIndex >= 0) {
                rightPanel.setFenList(testCases[leftPanel.fenComboBox.selectedIndex].expected.map { it.fen }.toList())
            }
        }

        layout = GridLayout(0, 2)
        add(leftPanel)
        add(rightPanel)

        size = Dimension(950, 600)

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

        setTestCases(testCases)
    }

    private fun createJMenuBar(): JMenuBar {

        val fileMenu = JMenu("File")

        val newFileItem = JMenuItem("File")
        val newStartFEN = JMenuItem("Starting FEN")
        newStartFEN.addActionListener {
            val newFEN = JOptionPane.showInputDialog("New starting FEN:")
            val newRoot = SearchNode.fromFEN(newFEN)
            val newTestCase = TestCase(null,
                    TestCase.TestCasePosition(null, newFEN),
                    newRoot.moves.map {
                        it.apply(newRoot.boardState)
                        val fen = newRoot.boardState.toFEN()
                        it.rollback(newRoot.boardState)
                        TestCase.TestCasePosition(null, fen)
                    })

            setTestCases(testCases + newTestCase, testCases.size)
        }
        val newMenu = JMenu("New")
        newMenu.add(newFileItem)
        newMenu.add(newStartFEN)
        fileMenu.add(newMenu)

        val saveItem = JMenuItem("Save")
        saveItem.addActionListener { println("Saving") }
        fileMenu.add(saveItem)

        val quitItem = JMenuItem("Quit")
        quitItem.addActionListener { exitProcess(0) }
        fileMenu.add(quitItem)

        val menuBar = JMenuBar()
        menuBar.add(fileMenu)
        return menuBar
    }

    private fun loadFile(filename: String) {
        val jsonString: String = File(filename).readText(Charsets.UTF_8)
        val testCaseType = object : TypeToken<List<TestCase>>() {}.type
        val testCases : List<TestCase> = Gson().fromJson(jsonString, testCaseType)
        setTestCases(testCases)
    }

    private fun setTestCases(testCases: List<TestCase>) {
        setTestCases(testCases, if (testCases.isEmpty()) -1 else 0)
    }

    private fun setTestCases(testCases: List<TestCase>, selectedIndex: Int) {
        this.testCases = testCases
        leftPanel.setFenList(testCases.map { it.start.fen }, selectedIndex)
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val r = Runnable {
                val frame = FENTestFileEditor()
                frame.isVisible = true
                if (args.isNotEmpty()) {
                    frame.loadFile(args[0])
                }
            }
            SwingUtilities.invokeLater(r)
        }
    }
}
