package org.computronium.chess

class TestCase(val description: String?, val start: TestCasePosition, val expected: List<TestCasePosition>) {
    override fun toString(): String {
        return start.fen
    }

    class TestCasePosition(val description: String?, val fen: String)
}