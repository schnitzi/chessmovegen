package org.computronium.chess

import org.computronium.chess.movegen.SearchNode
import org.computronium.chess.testcaseeditor.TestCaseGroup
import org.junit.Assert
import org.junit.Test
import java.io.File


class TestMoveGenerator {

    @Test
    fun runTest() {

        val testFileDir = File("./src/main/resources/testcases")
        val testFiles = testFileDir.listFiles()
        if (testFiles == null) {
            Assert.fail("No test files found.")
            return
        }
        for (testFile in testFiles.filter { file -> file.name.endsWith(".json") }) {
            doTest(testFile)
        }

    }

    private fun doTest(testFile: File) {
        val testCaseGroup = TestCaseGroup.fromFile(testFile)

        for (testCase in testCaseGroup.testCases) {

            println("Testing $testFile (${testCase.description ?: "no description"}) - '${testCase.start}'")

            val expectedMoves = testCase.expected.associateBy({ it.move.toString() }, { it.fen })

            val startPos = SearchNode.fromFEN(testCase.start.fen)
            val actualMoves = startPos.moves.associateBy({ it.toString() }, {
                it.apply(startPos.boardState)
                val fen = startPos.boardState.toFEN()
                it.rollback(startPos.boardState)
                fen
            })

            if (expectedMoves != actualMoves) {

                if (expectedMoves.keys != actualMoves.keys) {
                    println("Moves: Expected but didn't find: ${expectedMoves.keys.filter { !actualMoves.keys.contains(it) }}")
                    println("Moves: Found but didn't expect: ${actualMoves.keys.filter { !expectedMoves.keys.contains(it) } }")
                }

                println("FENs: Expected but didn't find: ${expectedMoves.values.filter { !actualMoves.values.contains(it) }}")
                println("FENs: Found but didn't expect: ${actualMoves.values.filter { !expectedMoves.values.contains(it) }}")
            }

            Assert.assertEquals("Did not match expected board states", expectedMoves, actualMoves)
        }
    }

}