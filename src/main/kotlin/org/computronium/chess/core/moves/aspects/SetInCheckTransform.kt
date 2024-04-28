package org.computronium.chess.core.moves.aspects

import org.computronium.chess.core.BoardState

/**
 * Transform for setting whether opponent is in check after a move.
 */
class SetInCheckTransform : Transform {

    private var isInCheck = false

    override fun apply(boardState: BoardState) {

        isInCheck = boardState.whoseTurnData().isInCheck
        boardState.whoseTurnData().isInCheck = boardState.isKingInCheck(boardState.whoseTurn)
    }

    override fun rollback(boardState: BoardState) {

        boardState.whoseTurnData().isInCheck = isInCheck
    }
}
