package org.computronium.chess.testcaseeditor

import com.google.gson.Gson
import org.computronium.chess.movegen.SearchNode
import java.awt.Dimension
import java.awt.GridLayout
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.File
import java.io.FileWriter
import javax.swing.JFileChooser
import javax.swing.JFrame
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem
import javax.swing.JOptionPane
import javax.swing.SwingUtilities
import javax.swing.UIManager
import javax.swing.WindowConstants
import kotlin.system.exitProcess

/**
 * Utility to create and edit test files for chess move generators.  Boards referred to in short
 * for using FEN records (https://en.wikipedia.org/wiki/Forsyth%E2%80%93Edwards_Notation).  Test
 * files contain a set of starting positions (as FENs), and for each, the set of resulting FENs
 * that a correct move generator should generate.
 *
 * TODO buttons to move through boards
 * TODO allow comments on data sets, start FENs, and result FENs
 * TODO dropdowns sorted by whether there are comments
 * TODO show filename in title, with "*" if modified
 * TODO labels above boards
 * TODO clean up bottom text
 * TODO allow board construction
 * TODO allow deletion, with confirm
 * TODO fix unicode writing of = on save
 * TODO handle special cases in move names
 */
internal class FENTestFileEditor(private var testCaseGroup: TestCaseGroup = TestCaseGroup(null)) : JFrame() {

    private val leftPanel: ChessBoardPanel
    private val rightPanel: ChessBoardPanel

    init {

        jMenuBar = createJMenuBar()

        leftPanel = ChessBoardPanel()
        rightPanel = ChessBoardPanel()

        leftPanel.fenComboBox.addActionListener {
            val index = leftPanel.fenComboBox.selectedIndex
            if (index >= 0) {
                rightPanel.setFenList(testCaseGroup.expectedFENsAt(index))
                rightPanel.fenComboBox.selectedIndex = 0
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
    }

    private fun createJMenuBar(): JMenuBar {

        val fileMenu = JMenu("File")

        val newFileItem = JMenuItem("File")
        val newStartFEN = JMenuItem("Starting FEN")
        newStartFEN.addActionListener {
            val newFEN = JOptionPane.showInputDialog("New starting FEN:")
            val newRoot = SearchNode.fromFEN(newFEN)
            val newTestCase = TestCase(null,
                    TestCase.TestCasePosition(null, null, newFEN),
                    newRoot.moves.map {
                        val move = it.toString(newRoot.boardState)
                        it.apply(newRoot.boardState)
                        val fen = newRoot.boardState.toFEN()
                        it.rollback(newRoot.boardState)
                        TestCase.TestCasePosition(null, move, fen)
                    })

            testCaseGroup.testCases.add(newTestCase)
            testCaseGroupChanged()
            leftPanel.selectFEN(testCaseGroup.getSize()-1)
        }
        val newMenu = JMenu("New")
        newMenu.add(newFileItem)
        newMenu.add(newStartFEN)
        fileMenu.add(newMenu)
        
        val openItem = JMenuItem("Open")
        openItem.addActionListener {
            val chooser = JFileChooser()
            val result = chooser.showOpenDialog(this)
            if (result == JFileChooser.APPROVE_OPTION) {
                loadFile(chooser.selectedFile)
            }
        }
        fileMenu.add(openItem)

        val saveItem = JMenuItem("Save")
        saveItem.addActionListener {
            val chooser = JFileChooser()
            val result = chooser.showSaveDialog(this)
            if (result == JFileChooser.APPROVE_OPTION) {
                val writer = FileWriter(chooser.selectedFile)
                Gson().toJson(testCaseGroup, writer)
                writer.close()
            }
        }
        fileMenu.add(saveItem)

        val quitItem = JMenuItem("Quit")
        quitItem.addActionListener { exitProcess(0) }
        fileMenu.add(quitItem)

        val menuBar = JMenuBar()
        menuBar.add(fileMenu)
        return menuBar
    }

    private fun loadFile(file: File) {
        testCaseGroup = TestCaseGroup.fromFile(file)
        testCaseGroupChanged()
        leftPanel.selectFEN(0)
    }

    private fun testCaseGroupChanged() {
        leftPanel.setFenList(testCaseGroup.testCases.map { it.start })
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val r = Runnable {
                val frame = FENTestFileEditor()
                frame.isVisible = true
                if (args.isNotEmpty()) {
                    frame.loadFile(File(args[0]))
                }
            }
            SwingUtilities.invokeLater(r)
        }
    }
}
