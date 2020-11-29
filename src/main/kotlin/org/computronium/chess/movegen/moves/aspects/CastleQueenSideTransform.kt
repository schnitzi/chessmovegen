package org.computronium.chess.movegen.moves.aspects

import org.computronium.chess.movegen.BoardState

class CastleQueenSideTransform : Transform {

    override fun apply(boardState: BoardState) {

        val sideData = boardState.whoseTurnData()
        val homeRankStart = sideData.homeRankStart
        boardState.move(homeRankStart+4, homeRankStart+2)    // move the king
        boardState.move(homeRankStart, homeRankStart+3)    // move the rook
    }

    override fun rollback(boardState: BoardState) {

        val sideData = boardState.whoseTurnData()
        val homeRankStart = sideData.homeRankStart
        boardState.move(homeRankStart+2, homeRankStart+4)    // move the king back
        boardState.move(homeRankStart+3, homeRankStart)    // move the rook back
    }

    companion object {

        fun isPossible(boardState: BoardState) : Boolean {

            val sideData = boardState.whoseTurnData()
            val homeRankStart = sideData.homeRankStart
            return !sideData.isInCheck &&
                    sideData.canQueenSideCastle &&
                    boardState.empty(homeRankStart + 1) &&
                    boardState.empty(homeRankStart + 2) &&
                    boardState.empty(homeRankStart + 3) &&
                    !boardState.isAttacked(homeRankStart + 3, 1 - boardState.whoseTurn)
        }
    }
}