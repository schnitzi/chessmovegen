package org.computronium.chess.core.moves.aspects

import org.computronium.chess.core.BoardState

class KingMoveTransform() : Transform {

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
