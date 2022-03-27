package org.computronium.chess.testcaseeditor

import com.google.gson.GsonBuilder
import org.computronium.chess.movegen.BoardState
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
 * Pre-release:
 * TODO decide: are checks, mates, and stalemates out of scope?
 * TODO decide: save multiple move formats?
 * TODO add move as test case
 * TODO confirm en passant move names
 * TODO verify move generator against perft
 * TODO reorder test cases
 * Post-release:
 * TODO show filename in title, with "*" if modified
 * TODO cancel on save at close doesn't abort close
 * TODO show count of possible moves
 * TODO ctrl-s to save
 * TODO allow board construction
 */
internal class FENTestFileEditor(private var testCaseGroup: TestCaseGroup = TestCaseGroup(null)) : JFrame() {

    private val descriptionPanel: DescriptionPanel
    private val leftPanel: SidePanel
    private val rightPanel: SidePanel
    private var file: File? = null
    private var directory: File = File(System.getProperty("user.dir"),"src/main/resources/testcases")

    init {

        jMenuBar = createJMenuBar()

        descriptionPanel = DescriptionPanel()
        descriptionPanel.description.document.addDocumentListener(object : DocumentListener {
            override fun changedUpdate(e: DocumentEvent?) {
                testCaseGroup.description = descriptionPanel.description.text
            }

            override fun insertUpdate(e: DocumentEvent?) {
                testCaseGroup.description = descriptionPanel.description.text
            }

            override fun removeUpdate(e: DocumentEvent?) {
                testCaseGroup.description = descriptionPanel.description.text
            }
        })

        leftPanel = SidePanel("Test case")
        rightPanel = SidePanel("Moves")
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
                descriptionPanel.description.text = ""
            }
        }

        val openItem = JMenuItem("Open")
        openItem.addActionListener {

            if (okayToProceedToNewTestGroup()) {
                val chooser = JFileChooser()
                chooser.currentDirectory = directory
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

        val saveAsItem = JMenuItem("Save as")
        saveAsItem.addActionListener {
            doSaveAs()
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
        fileMenu.add(saveAsItem)
        fileMenu.add(quitItem)

        return fileMenu
    }

    private fun createActionMenu(): JMenu {

        val addTestCase = JMenuItem("Add test case")
        addTestCase.addActionListener {
            val newFEN = JOptionPane.showInputDialog("FEN of starting position:")
            if (newFEN != null) {
                addFen(newFEN)
            }
        }

        val addMoveAsTestCase = JMenuItem("Add move result as test case")
        addMoveAsTestCase.addActionListener {
            val newFEN = rightPanel.selectedFEN()?.fen
            if (newFEN != null) {
                addFen(newFEN)
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

        val addTranspositions = JMenuItem("Add missing white/black transpositions")
        addTranspositions.addActionListener {
            val transpositionsAdded = addTranspositions()
            JOptionPane.showMessageDialog(this, "Added $transpositionsAdded new test cases.")
        }

        val actionMenu = JMenu("Action")
        actionMenu.add(addTestCase)
        actionMenu.add(addMoveAsTestCase)
        actionMenu.add(removeTestCase)
        actionMenu.add(addTranspositions)
        return actionMenu
    }

    private fun addFen(newFEN: String) {
        var newFEN1 = newFEN
        newFEN1 = newFEN1.trim()
        if (testCaseGroup.contains(newFEN1)) {
            val regenerate = JOptionPane.showConfirmDialog(this, "FEN already in group.  Regenerate it?")
            if (regenerate != JOptionPane.YES_OPTION) {
                return
            }
        }
        val newRoot = SearchNode.fromFEN(newFEN1)
        val newTestCase = TestCase(null,
            TestCase.TestCasePosition(null, null, newFEN1),
            newRoot.moves.map {
                val move = it.toString()
                it.apply(newRoot.boardState)
                val fen = newRoot.boardState.toFEN()
                it.rollback(newRoot.boardState)
                TestCase.TestCasePosition(null, move, fen)
            })

        testCaseGroup.add(newTestCase)
        testCaseGroupChanged()
        leftPanel.selectFEN(testCaseGroup.getSize() - 1)
    }

    private fun addTranspositions() {
        val newTestCaseGroup = TestCaseGroup(testCaseGroup.description)
        for (testCase in testCaseGroup.testCases) {

            val transposedBoardState = BoardState.fromFEN(testCase.start.fen).transpose()

            val newFEN = transposedBoardState.toFEN()
            if (!testCaseGroup.contains(newFEN)) {
                val newRoot = SearchNode.fromFEN(newFEN)
                val newTestCase = TestCase("Transpose of ${testCase.start.fen}",
                    TestCase.TestCasePosition(null, null, newFEN),
                    newRoot.moves.map {
                        val move = it.toString()
                        it.apply(newRoot.boardState)
                        val fen = newRoot.boardState.toFEN()
                        it.rollback(newRoot.boardState)
                        TestCase.TestCasePosition(null, move, fen)
                    })
                newTestCaseGroup.add(newTestCase)
            }
        }

        testCaseGroup = newTestCaseGroup
        testCaseGroupChanged()
        if (testCaseGroup.getSize() > 0) {
            leftPanel.selectFEN(0)
        }
    }

    private fun doSave() : Boolean {
        if (file == null) {
            if (!chooseSaveFile()) return false
        }
        writeFile()
        return true
    }

    private fun chooseSaveFile(): Boolean {
        val chooser = JFileChooser()
        chooser.currentDirectory = directory
        val result = chooser.showSaveDialog(this)
        if (result == JFileChooser.CANCEL_OPTION) {
            return false
        }
        file = chooser.selectedFile
        if (!file!!.name.endsWith(".json")) {
            file = File(file!!.path + ".json")
        }
        return true
    }

    private fun doSaveAs() : Boolean {
        if (!chooseSaveFile()) return false
        writeFile()
        return true
    }

    private fun writeFile() {
        val writer = FileWriter(file!!)
        val gson = GsonBuilder()
                .setPrettyPrinting()
                .excludeFieldsWithoutExposeAnnotation()
                .disableHtmlEscaping()
                .create()
        gson.toJson(testCaseGroup, writer)
        writer.close()
        testCaseGroup.modified = false
        title = file.toString()
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
            descriptionPanel.description.text = testCaseGroup.description
        }
        directory = file.parentFile
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
