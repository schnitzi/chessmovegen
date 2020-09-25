package org.computronium.chess.testcaseeditor

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class TestCaseGroup(description: String?, testCases: List<TestCase> = listOf(), var modified: Boolean = false) {

    var description: String? = description
        set(value) {
            modified = true
            field = value
        }

    var testCases: List<TestCase> = testCases
        set(value) {
            modified = true
            field = value
        }

    operator fun get(index: Int) : TestCase {
        return testCases[index]
    }

    fun getSize() = testCases.size

    fun add(testCase: TestCase) {
        testCases = testCases + testCase
    }

    fun expectedFENsAt(index: Int) : List<TestCase.TestCasePosition> {
        return testCases[index].expected
    }

    fun remove(index: Int) {
        val mutableTestCases = testCases.toMutableList()
        mutableTestCases.removeAt(index)
        testCases = mutableTestCases.toList()
    }

    companion object {
        fun fromFile(file: File) : TestCaseGroup {
            val jsonString: String = file.readText(Charsets.UTF_8)
            val testCaseType = object : TypeToken<TestCaseGroup>() {}.type
            return Gson().fromJson(jsonString, testCaseType)
        }
    }
}
