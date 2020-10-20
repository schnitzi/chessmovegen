package org.computronium.chess.movegen.moves.aspects

import org.computronium.chess.movegen.BoardState

/**
 * That aspect that's just a piece moving from one place to another.
 */
class MoveAspect(val from: Int, val to: Int) : Aspect {

    private var enPassantCapturePos : Int? = null

    private var whoseTurnIsInCheck : Boolean = false

    override fun apply(boardState: BoardState) {

        boardState.move(from, to)

        enPassantCapturePos = boardState.enPassantCapturePos

        boardState.enPassantCapturePos = null

        whoseTurnIsInCheck = boardState.whoseTurnConfig().isInCheck

        if (boardState.whoseTurn == BoardState.WHITE) {
            boardState.moveNumber++
        }

        boardState.whoseTurn = 1 - boardState.whoseTurn
    }

    override fun rollback(boardState: BoardState) {

        boardState.whoseTurn = 1 - boardState.whoseTurn

        if (boardState.whoseTurn == BoardState.WHITE) {
            boardState.moveNumber--
        }

        boardState.whoseTurnConfig().isInCheck = whoseTurnIsInCheck

        boardState.enPassantCapturePos = enPassantCapturePos

        boardState.move(to, from)
    }
}
