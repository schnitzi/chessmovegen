package org.computronium.chess.testcaseeditor

import com.google.gson.GsonBuilder
import org.computronium.chess.movegen.SearchNode
import java.awt.BorderLayout
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
import javax.swing.JPanel
import javax.swing.JTextArea
import javax.swing.SwingUtilities
import javax.swing.UIManager
import javax.swing.WindowConstants
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import kotlin.system.exitProcess


/**
 * Utility to create and edit test files for chess move generators.  Boards referred to in short
 * for using FEN records (https://en.wikipedia.org/wiki/Forsyth%E2%80%93Edwards_Notation).  Test
 * files contain a set of starting positions (as FENs), and for each, the set of resulting FENs
 * that a correct move generator should generate.
 *
 * TODO allow comments on data sets, start FENs, and result FENs
 * TODO dropdowns sorted by whether there are comments
 * TODO show filename in title, with "*" if modified
 * TODO Fix move numbers
 * TODO clean up bottom text
 * TODO allow board construction
 * TODO fix unicode writing of = on save
 * TODO format JSON on save
 * TODO warn before overwriting file on save
 * TODO handle special cases in move names
 * TODO are checks, mates, and stalemates out of scope?
 * TODO save multiple move formats?
 * TODO save as (to other file)
 * TODO regenerate file option?
 * TODO below TODOs
 */
internal class FENTestFileEditor(private var testCaseGroup: TestCaseGroup = TestCaseGroup(null)) : JFrame() {

    private val descriptionPanel: JTextArea
    private val leftPanel: SidePanel
    private val rightPanel: SidePanel
    private var file: File? = null

    init {

        jMenuBar = createJMenuBar()

        descriptionPanel = JTextArea()
        descriptionPanel.document.addDocumentListener(object : DocumentListener {
            override fun changedUpdate(e: DocumentEvent?) {
                testCaseGroup.description = descriptionPanel.text
            }

            override fun insertUpdate(e: DocumentEvent?) {
                testCaseGroup.description = descriptionPanel.text
            }

            override fun removeUpdate(e: DocumentEvent?) {
                testCaseGroup.description = descriptionPanel.text
            }
        })

        leftPanel = SidePanel()
        rightPanel = SidePanel()
        rightPanel.otherSidePanel = leftPanel

        leftPanel.fenComboBox.addActionListener {
            val index = leftPanel.fenComboBox.selectedIndex
            if (index >= 0) {
                val fens = testCaseGroup.expectedFENsAt(index)
                rightPanel.setFenList(fens)
                rightPanel.fenComboBox.selectedIndex = if (fens.isEmpty()) -1 else 0
            }
        }

        val mainPanel = JPanel(GridLayout(0, 2))
        mainPanel.add(leftPanel)
        mainPanel.add(rightPanel)

        layout = BorderLayout()
        add(BorderLayout.NORTH, descriptionPanel)
        add(BorderLayout.CENTER, mainPanel)

        size = Dimension(950, 600)

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(windowEvent: WindowEvent?) {
                if (okayToProceedToNewTestGroup()) {
                    exitProcess(0)
                }
            }
        })

        setLocationRelativeTo(null)
        this.isFocusable = true
        this.requestFocus()
    }

    private fun createJMenuBar(): JMenuBar {

        val menuBar = JMenuBar()
        menuBar.add(createFileMenu())
        menuBar.add(createActionMenu())
        return menuBar
    }

    private fun createFileMenu(): JMenu {

        val newFileItem = JMenuItem("New")
        newFileItem.addActionListener {

            if (okayToProceedToNewTestGroup()) {
                file = null
                testCaseGroup = TestCaseGroup(null)
                testCaseGroupChanged()
                leftPanel.selectFEN(-1)
            }
        }

        val openItem = JMenuItem("Open")
        openItem.addActionListener {

            if (okayToProceedToNewTestGroup()) {
                val chooser = JFileChooser()
                val result = chooser.showOpenDialog(this)
                if (result == JFileChooser.APPROVE_OPTION) {
                    loadFile(chooser.selectedFile)
                }
            }
        }

        val saveItem = JMenuItem("Save")
        saveItem.addActionListener {
            doSave()
        }

        val quitItem = JMenuItem("Quit")
        quitItem.addActionListener {
            if (okayToProceedToNewTestGroup()) {
                exitProcess(0)
            }
        }

        val fileMenu = JMenu("File")
        fileMenu.add(newFileItem)
        fileMenu.add(openItem)
        fileMenu.add(saveItem)
        fileMenu.add(quitItem)

        return fileMenu
    }

    private fun createActionMenu(): JMenu {

        val addTestCase = JMenuItem("Add test case")
        addTestCase.addActionListener {
            val newFEN = JOptionPane.showInputDialog("FEN of starting position:")
            if (newFEN != null) {
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

                testCaseGroup.add(newTestCase)
                testCaseGroupChanged()
                leftPanel.selectFEN(testCaseGroup.getSize() - 1)
            }
        }

        val removeTestCase = JMenuItem("Remove test case")
        removeTestCase.addActionListener {
            val confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove the current test case?")
            if (confirm == JOptionPane.YES_OPTION) {

                testCaseGroup.remove(leftPanel.fenComboBox.selectedIndex)
                testCaseGroupChanged()
                leftPanel.selectFEN(testCaseGroup.getSize() - 1)
            }
        }

        val actionMenu = JMenu("Action")
        actionMenu.add(addTestCase)
        actionMenu.add(removeTestCase)
        return actionMenu
    }

    private fun doSave() : Boolean {
        if (file == null) {
            val chooser = JFileChooser()
            val result = chooser.showSaveDialog(this)
            if (result == JFileChooser.CANCEL_OPTION) {
                return false
            }
            file = chooser.selectedFile
        }
        val writer = FileWriter(file!!)
        val gson = GsonBuilder()
                .setPrettyPrinting()
                .excludeFieldsWithoutExposeAnnotation()
                .create()
        gson.toJson(testCaseGroup, writer)
        writer.close()
        testCaseGroup.modified = false
        return true
    }

    private fun okayToProceedToNewTestGroup() : Boolean {
        if (testCaseGroup.modified) {
            val options = arrayOf("Save", "Discard", "Cancel")
            val choice = JOptionPane.showOptionDialog(this, "Test group not saved.  Save it?",
                    "Not saved",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0])
            when (choice) {
                0 -> {
                    return doSave()
                }
                1 -> {
                    return true
                }
                2 -> {
                    return false
                }
            }
        }
        return true
    }

    private fun loadFile(file: File) {
        this.file = file
        testCaseGroup = TestCaseGroup.fromFile(file)
        testCaseGroupChanged()
        if (testCaseGroup.getSize() > 0) {
            leftPanel.selectFEN(0)
        }
        title = file.toString()
        if (testCaseGroup.description != null) {
            descriptionPanel.text = testCaseGroup.description
        }
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
