package org.computronium.chess

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.junit.Assert
import org.junit.Test
import java.io.File


class TestMoveGenerator {

    @Test
    fun runTest() {

        val jsonString: String = File("./src/test/resources/testdata.json").readText(Charsets.UTF_8)
        val testCaseType = object : TypeToken<List<TestCase>>() {}.type
        val jsonTextList : List<TestCase> = Gson().fromJson(jsonString, testCaseType)

        for (testCase in jsonTextList) {
            println("Testing '${testCase.description ?: "<no description>"}' (${testCase.start})")
            val startPos = SearchNode.fromFEN(testCase.start)
            val actual = startPos.moves.map {
                it.apply(startPos.boardState)
                val fen = startPos.boardState.toFEN()
                it.rollback(startPos.boardState)
                fen
            }.toSet()
//            println("Expected but didn't find: ${testCase.expected.filter { !actual.contains(it) }}")
//            println("Found but didn't expect: ${actual.filter { !testCase.expected.contains(it) }}")
            Assert.assertEquals("Did not match expected board states", testCase.expected, actual)
        }
    }

    class TestCase(val description: String?, val start: String, val expected: Set<String>)
}