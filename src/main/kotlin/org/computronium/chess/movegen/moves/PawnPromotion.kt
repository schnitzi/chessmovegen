package org.computronium.chess.movegen.moves

import org.computronium.chess.movegen.BoardState
import org.computronium.chess.movegen.Piece
import org.computronium.chess.movegen.PieceType

class PawnPromotion(from: Int, to: Int, private val promoteTo: PieceType) : PawnMove(from, to) {

    private var pawn: Piece? = null

    override fun apply(boardState: BoardState): BoardState {

        pawn = boardState[from]

        super.apply(boardState)

        boardState[to] = Piece.forTypeAndColor(promoteTo, pawn!!.color)
        return boardState
    }

    override fun rollback(boardState: BoardState) {

        super.rollback(boardState)

        boardState[from] = pawn
    }

    override fun toString(boardState: BoardState): String {
        val sb = StringBuilder()
        sb.append(BoardState.squareName(to)).append("=").append(promoteTo.letter)
        if (resultsInCheck) {
            sb.append("+")
        }
        return sb.toString()
    }
}