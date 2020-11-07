package org.computronium.chess.movegen.moves.aspects

import org.computronium.chess.movegen.BoardState

class CastleQueenSideAspect : Aspect {

    override fun apply(boardState: BoardState) {

        val sideData = boardState.whoseTurnData()
        val homeRankStart = sideData.homeRankStart
        boardState.move(homeRankStart+4, homeRankStart+2)    // move the king
        sideData.kingPos = homeRankStart+2
        boardState.move(homeRankStart, homeRankStart+3)    // move the rook
    }

    override fun rollback(boardState: BoardState) {

        val sideData = boardState.whoseTurnData()
        val homeRankStart = sideData.homeRankStart
        boardState.move(homeRankStart+2, homeRankStart+4)    // move the king back
        sideData.kingPos = homeRankStart+4
        boardState.move(homeRankStart+3, homeRankStart)    // move the rook back
    }

    companion object {

        fun isPossible(boardState: BoardState) : Boolean {

            val sideData = boardState.whoseTurnData()
            val homeRankStart = sideData.homeRankStart
            return !sideData.isInCheck &&
                    sideData.canQueenSideCastle &&
                    boardState.empty(homeRankStart+5) &&
                    boardState.empty(homeRankStart+6) &&
                    !boardState.isAttacked(homeRankStart+5, 1 - boardState.whoseTurn)
        }
    }
}