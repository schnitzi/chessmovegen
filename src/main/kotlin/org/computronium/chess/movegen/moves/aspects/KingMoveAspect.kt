package org.computronium.chess.movegen.moves.aspects

import org.computronium.chess.movegen.BoardState

class KingMoveAspect() : Aspect {

    private var canQueenSideCastle : Boolean? = null
    private var canKingSideCastle : Boolean? = null

    override fun apply(boardState: BoardState) {

        val sideData = boardState.whoseTurnData()

        canQueenSideCastle = sideData.canQueenSideCastle
        sideData.canQueenSideCastle = false
        canKingSideCastle = sideData.canKingSideCastle
        sideData.canKingSideCastle = false
    }

    override fun rollback(boardState: BoardState) {

        val sideData = boardState.whoseTurnData()

        sideData.canKingSideCastle = canKingSideCastle!!
        sideData.canQueenSideCastle = canQueenSideCastle!!
    }
}
