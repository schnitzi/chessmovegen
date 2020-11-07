package org.computronium.chess.movegen.moves.aspects

import org.computronium.chess.movegen.BoardState

class RookMoveAspect(private val from : Int) : Aspect {

    private var canQueenSideCastle = false
    private var canKingSideCastle = false

    override fun apply(boardState: BoardState) {

        val sideData = boardState.whoseTurnData()
        val homeRank = sideData.homeRankStart
        if (from == homeRank) {     // queenside rook
            canQueenSideCastle = sideData.canQueenSideCastle
            sideData.canQueenSideCastle = false
        } else if (from == homeRank+7) {    // kingside rook
            canKingSideCastle = sideData.canKingSideCastle
            sideData.canKingSideCastle = false
        }
    }

    override fun rollback(boardState: BoardState) {
        val sideData = boardState.whoseTurnData()
        val homeRank = sideData.homeRankStart
        if (from == homeRank) {
            sideData.canQueenSideCastle = canQueenSideCastle
        } else if (from == homeRank+7) {
            sideData.canKingSideCastle = canKingSideCastle
        }
    }
}