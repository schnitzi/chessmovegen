package org.computronium.chess.movegen.moves.aspects

import org.computronium.chess.movegen.BoardState
import org.computronium.chess.movegen.Piece
import org.computronium.chess.movegen.PieceType

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