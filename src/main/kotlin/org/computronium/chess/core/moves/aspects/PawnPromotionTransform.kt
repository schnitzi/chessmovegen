package org.computronium.chess.core.moves.aspects

import org.computronium.chess.core.BoardState
import org.computronium.chess.core.Piece
import org.computronium.chess.core.PieceType

class PawnPromotionTransform(private val to: Int, private val promoteTo: PieceType) : Transform {

    private var pawn: Piece? = null

    override fun apply(boardState: BoardState) {

        pawn = boardState[to]

        boardState[to] = Piece.forTypeAndColor(promoteTo, pawn!!.color)
    }

    override fun rollback(boardState: BoardState) {

        boardState[to] = pawn
    }
}