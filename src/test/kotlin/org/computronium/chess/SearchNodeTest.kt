package org.computronium.chess

import com.tngtech.java.junit.dataprovider.DataProvider
import com.tngtech.java.junit.dataprovider.DataProviderRunner
import com.tngtech.java.junit.dataprovider.DataProviders.`$$`
import com.tngtech.java.junit.dataprovider.DataProviders.`$`
import com.tngtech.java.junit.dataprovider.UseDataProvider
import org.computronium.chess.movegen.SearchNode
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(DataProviderRunner::class)
@Deprecated("Just kept around for reference")
internal class SearchNodeTest {

    @Test
    @UseDataProvider(value = "findMovesData")
    fun findMoves(startFEN: String, expectedMoveFEN: String) {
        val startBoard = SearchNode.fromFEN(startFEN)
        println("$startBoard")
        println("start       = ${startBoard.boardState.toFEN()}")

        val actuals = mutableListOf<String>()
        var found = false
        var match : String? = null
        for (move in startBoard.moves) {
            //println("move = ${move.toString(startBoard.boardState)}\n")
            move.apply(startBoard.boardState)
            val actual = startBoard.boardState.toFEN()
            //println("$expectedMoveFEN =\n$actual ?")
            actuals.add(actual)
            if (expectedMoveFEN == actual) {
                found = true
                break
            } else if (expectedMoveFEN.split(" ")[0] == actual.split(" ")[0]) {
                match = actual
            }
            move.rollback(startBoard.boardState)
        }
        if (!found) {
            println("expected    = $expectedMoveFEN")
            println("close match = $match")
        }
        Assert.assertTrue("Expected\n$expectedMoveFEN, found\n$actuals", found)
    }

    companion object {

        @DataProvider
        @JvmStatic
        fun findMovesData(): Array<Array<Any>> = `$$`(
//             @formatter:off


//            `$` (
//                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
//                "rnbqkbnr/pppppppp/8/8/8/P7/1PPPPPPP/RNBQKBNR b KQkq - 0 1"),
//                    "rnbqkbnr",
//                    "pppppppp",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "PPPPPPPP",
//                    "RNBQKBNR"),
//
//                    "rnbqkbnr",
//                    "pppppppp",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "P       ",
//                    " PPPPPPP",
//                    "RNBQKBNR")),

            `$` (
                "1k6/5P2/8/8/8/8/8/4K3 w - - 20 1",
                "1k3Q2/8/8/8/8/8/8/4K3 b - - 0 1")
//            `$` (BoardState.WHITE,
//                arrayOf(
//                    " k      ",
//                    "     P  ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "    K   "),
//                arrayOf(
//                    " k   Q  ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "    K   ")),

//            `$` (BoardState.WHITE,
//                arrayOf(
//                    " k  r   ",
//                    "     P  ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "    K   "),
//                arrayOf(
//                    " k  B   ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "    K   ")),

//            `$` (BoardState.BLACK,
//                arrayOf(
//                    " k      ",
//                    "        ",
//                    "        ",
//                    "     K  ",
//                    "        ",
//                    "        ",
//                    "   p    ",
//                    "        "),
//                arrayOf(
//                    " k      ",
//                    "        ",
//                    "        ",
//                    "     K  ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "   n    ")),

//            `$` (BoardState.WHITE,
//                arrayOf(
//                    " k      ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "R   K   "),
//                arrayOf(
//                    " k      ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "  KR    ")),

//            `$` (BoardState.WHITE,
//                arrayOf(
//                    " k      ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "    K  R"),
//                arrayOf(
//                    " k      ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "     RK ")),

//            `$` (BoardState.BLACK,
//                arrayOf(
//                    "r   k   ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "    K  R"),
//                arrayOf(
//                    "  kr    ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "        ",
//                    "    K  R")),

//            `$` (BoardState.BLACK,
//                arrayOf(
//                    "rnb.kb..",
//                    "ppqppppr",
//                    ".....N..",
//                    "..p....p",
//                    "........",
//                    "P....P..",
//                    "RPPPP.PP",
//                    "..BQKBNR"),
//                arrayOf(
//                    "rnbk.b..",
//                    "ppqppppr",
//                    ".....N..",
//                    "..p....p",
//                    "........",
//                    "P....P..",
//                    "RPPPP.PP",
//                    "..BQKBNR")),

//            `$` (BoardState.WHITE,
//                arrayOf(
//                    " . k . .",
//                    ".Q. . . ",
//                    " . N . .",
//                    ". b . .p",
//                    "B. . . P",
//                    ". . .Pn ",
//                    " . . . .",
//                    ". .K. . "),
//                arrayOf(
//                    " . k . .",
//                    ".Q. .N. ",
//                    " .   . .",
//                    ". b . .p",
//                    "B. . . P",
//                    ". . .Pn ",
//                    " . . . .",
//                    ". .K. . "))
        )
    }
}