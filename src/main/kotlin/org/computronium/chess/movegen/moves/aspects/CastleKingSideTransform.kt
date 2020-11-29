package org.computronium.chess.movegen.moves.aspects

import org.computronium.chess.movegen.BoardState

class CastleKingSideTransform : Transform {

    override fun apply(boardState: BoardState) {

        val sideData = boardState.whoseTurnData()
        val homeRankStart = sideData.homeRankStart
        boardState.move(homeRankStart+4, homeRankStart+6)    // move the king
        boardState.move(homeRankStart+7, homeRankStart+5)    // move the rook
    }

    override fun rollback(boardState: BoardState) {

        val sideData = boardState.whoseTurnData()
        val homeRankStart = sideData.homeRankStart
        boardState.move(homeRankStart+6, homeRankStart+4)    // move the king back
        boardState.move(homeRankStart+5, homeRankStart+7)    // move the rook back
    }

    companion object {

        fun isPossible(boardState: BoardState) : Boolean {

            val sideData = boardState.whoseTurnData()
            val homeRankStart = sideData.homeRankStart
            return !sideData.isInCheck &&
                    sideData.canKingSideCastle &&
                    boardState.empty(homeRankStart+5) &&
                    boardState.empty(homeRankStart+6) &&
                    !boardState.isAttacked(homeRankStart+5, 1 - boardState.whoseTurn)
        }
    }
}