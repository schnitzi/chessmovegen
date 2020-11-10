package org.computronium.chess.testcaseeditor

import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JPanel
import javax.swing.JTextArea
import javax.swing.border.EmptyBorder
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class DescriptionPanel : JPanel() {

    val description: JTextArea

    init {
        description = JTextArea()

        layout = BoxLayout(this, BoxLayout.LINE_AXIS)
        border = EmptyBorder(5, 5, 5, 5)
        add(Box.createHorizontalGlue())
        add(description)
        add(Box.createHorizontalGlue())
    }
}