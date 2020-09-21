package org.computronium.chess.testcaseeditor

import org.computronium.chess.movegen.BoardState

class TestCase(val description: String?, val start: TestCasePosition, val expected: List<TestCasePosition>) {


    class TestCasePosition(val description: String?, val move: String?, val fen: String) {

        private var state: BoardState? = null

        override fun toString(): String {
            return (if (move != null) "$move -> " else "") + fen
        }

        fun moveLabel(): String {
            return "After ${getBoardState().moveNumber}. ${if (getBoardState().whoseTurn == BoardState.WHITE) "... " else ""}${move ?: "??"}\n"
        }

        fun getBoardState(): BoardState {
            if (state == null) state = BoardState.fromFEN(fen)
            return state!!
        }
    }

    override fun toString(): String {
        return start.fen
    }
}
