package org.computronium.chess.movegen.moves

import org.computronium.chess.movegen.BoardState

class KingCapture(from : Int, to : Int) : StandardCapture(from, to) {

    private var canQueenSideCastle : Boolean? = null
    private var canKingSideCastle : Boolean? = null

    override fun apply(boardState: BoardState): BoardState {

        val config = boardState.whoseTurnConfig()
        canQueenSideCastle = config.canQueenSideCastle
        config.canQueenSideCastle = false
        canKingSideCastle = config.canKingSideCastle
        config.canKingSideCastle = false

        super.apply(boardState)
        return boardState
    }

    override fun rollback(boardState: BoardState) {

        super.rollback(boardState)

        val config = boardState.whoseTurnConfig()
        config.canQueenSideCastle = canQueenSideCastle!!
        config.canKingSideCastle = canKingSideCastle!!
    }
}