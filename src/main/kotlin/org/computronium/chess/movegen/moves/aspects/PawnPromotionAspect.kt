package org.computronium.chess.movegen.moves.aspects

import org.computronium.chess.movegen.BoardState
import org.computronium.chess.movegen.Piece
import org.computronium.chess.movegen.PieceType

class PawnPromotionAspect(private val from: Int, private val to: Int, private val promoteTo: PieceType) : Aspect {

    override fun apply(boardState: BoardState) {

        val pawn = boardState[to]

        boardState[to] = Piece.forTypeAndColor(promoteTo, pawn!!.color)
    }

    override fun rollback(boardState: BoardState) {
    }
}