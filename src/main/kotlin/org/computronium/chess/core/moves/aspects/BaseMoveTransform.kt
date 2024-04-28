package org.computronium.chess.core.moves.aspects

import org.computronium.chess.core.BoardState

/**
 * Transform that does all the things that are common to every kind of move.
 */
class BaseMoveTransform : Transform {

    private var enPassantCapturePos : Int? = null

    override fun apply(boardState: BoardState) {

        enPassantCapturePos = boardState.enPassantCapturePos
        boardState.enPassantCapturePos = null

        if (boardState.whoseTurn == BoardState.BLACK) {
            boardState.moveNumber++
        }

        boardState.halfMovesSinceCaptureOrPawnAdvance += 1
    }

    override fun rollback(boardState: BoardState) {

        boardState.halfMovesSinceCaptureOrPawnAdvance -= 1

        if (boardState.whoseTurn == BoardState.BLACK) {
            boardState.moveNumber--
        }

        boardState.enPassantCapturePos = enPassantCapturePos
    }
}
