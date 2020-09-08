package org.computronium.chess.moves

import org.computronium.chess.BoardState
import org.computronium.chess.Piece

class PawnEnPassantCapture(val from: Int) : Move() {

    private var capturedPiece: Piece? = null

    override fun apply(boardState: BoardState): BoardState {

        val pawnToCapturePos = boardState.enPassantCapturePos!! - boardState.whoseTurnConfig().pawnMoveDirection
        capturedPiece = boardState[pawnToCapturePos]
        boardState[pawnToCapturePos] = null
        boardState[boardState.enPassantCapturePos!!] = boardState[from]
        boardState[from] = null

        super.apply(boardState)
        return boardState
    }

    override fun rollback(boardState: BoardState) {

        super.rollback(boardState)

        val pawnToCapturePos : Int = boardState.enPassantCapturePos!! - boardState.whoseTurnConfig().pawnMoveDirection
        boardState[from] = boardState[pawnToCapturePos]
        boardState[pawnToCapturePos] = capturedPiece
        boardState[boardState.enPassantCapturePos!!] = null
    }

    override fun toString(boardState: BoardState): String {
        return "" + BoardState.fileChar(from) + "x" + BoardState.squareName(boardState.enPassantCapturePos!!) + " e.p."
    }
}