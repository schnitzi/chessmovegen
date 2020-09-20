package org.computronium.chess.testcaseeditor

class TestCase(val description: String?, val start: TestCasePosition, val expected: List<TestCasePosition>) {
    override fun toString(): String {
        return start.fen
    }

    class TestCasePosition(val description: String?, val move: String?, val fen: String) {
        override fun toString(): String {
            return (if (move != null) "$move -> " else "") + fen
        }

        fun moveLabel(): String {
            return "After $move"
        }
    }

}