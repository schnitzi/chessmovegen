package org.computronium.chess.moves

import org.computronium.chess.BoardState

class KingMove(from : Int, to : Int) : StandardMove(from, to) {

    var canQueenSideCastle : Boolean? = null
    var canKingSideCastle : Boolean? = null

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