package org.computronium.chess.testcaseeditor

import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JPanel
import javax.swing.JTextArea
import javax.swing.border.EmptyBorder

class DescriptionPanel : JPanel() {

    val description: JTextArea = JTextArea()

    init {

        layout = BoxLayout(this, BoxLayout.LINE_AXIS)
        border = EmptyBorder(5, 5, 5, 5)
        add(Box.createHorizontalGlue())
        add(description)
        add(Box.createHorizontalGlue())
    }
}