package org.computronium.chess.moves

import org.computronium.chess.BoardState
import org.computronium.chess.Piece
import org.computronium.chess.PieceType

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
        return BoardState.squareName(to) + "=" + promoteTo.letter
    }
}