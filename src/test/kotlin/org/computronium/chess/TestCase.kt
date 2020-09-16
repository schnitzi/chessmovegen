package org.computronium.chess

class TestCase(val description: String?, val start: String, val expected: Set<String>) {
    override fun toString(): String {
        return start
    }
}