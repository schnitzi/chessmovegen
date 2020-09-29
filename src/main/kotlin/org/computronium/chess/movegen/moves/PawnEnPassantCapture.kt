package org.computronium.chess.movegen.moves

import org.computronium.chess.movegen.BoardState
import org.computronium.chess.movegen.Piece

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
        val sb = StringBuilder()
                .append(BoardState.fileChar(from))
                .append( "x")
                .append(BoardState.squareName(boardState.enPassantCapturePos!!))
                .append(" e.p.")
        if (resultsInCheck) {
            sb.append("+")
        }
        return sb.toString()
    }
}