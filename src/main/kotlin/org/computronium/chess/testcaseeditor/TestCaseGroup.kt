package org.computronium.chess.testcaseeditor

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class TestCaseGroup(var description: String?, internal var testCases: MutableList<TestCase> = mutableListOf()) {

    operator fun get(index: Int) : TestCase {
        return testCases[index]
    }

    fun getSize() = testCases.size

    fun expectedFENsAt(index: Int) : List<TestCase.TestCasePosition> {
        return testCases[index].expected
    }

    companion object {
        fun fromFile(file: File) : TestCaseGroup {
            val jsonString: String = file.readText(Charsets.UTF_8)
            val testCaseType = object : TypeToken<TestCaseGroup>() {}.type
            return Gson().fromJson(jsonString, testCaseType)
        }
    }
}
