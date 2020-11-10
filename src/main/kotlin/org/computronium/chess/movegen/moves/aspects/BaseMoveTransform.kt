package org.computronium.chess.movegen.moves.aspects

import org.computronium.chess.movegen.BoardState

/**
 * Transform that does all the things that are common to every kind of move.
 */
class BaseMoveTransform : Transform {

    private var enPassantCapturePos : Int? = null

    private var whoseTurnIsInCheck : Boolean = false

    override fun apply(boardState: BoardState) {

        enPassantCapturePos = boardState.enPassantCapturePos
        boardState.enPassantCapturePos = null

        whoseTurnIsInCheck = boardState.whoseTurnData().isInCheck

        if (boardState.whoseTurn == BoardState.WHITE) {
            boardState.moveNumber++
        }
    }

    override fun rollback(boardState: BoardState) {

        if (boardState.whoseTurn == BoardState.WHITE) {
            boardState.moveNumber--
        }

        boardState.whoseTurnData().isInCheck = whoseTurnIsInCheck

        boardState.enPassantCapturePos = enPassantCapturePos
    }
}
