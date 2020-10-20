package org.computronium.chess.movegen.moves

import org.computronium.chess.movegen.BoardState
import org.computronium.chess.movegen.PieceType
import org.computronium.chess.movegen.moves.aspects.Aspect
import org.computronium.chess.movegen.moves.aspects.CaptureAspect
import org.computronium.chess.movegen.moves.aspects.CastleKingSideAspect
import org.computronium.chess.movegen.moves.aspects.CastleQueenSideAspect
import org.computronium.chess.movegen.moves.aspects.KingMoveAspect
import org.computronium.chess.movegen.moves.aspects.MoveAspect
import org.computronium.chess.movegen.moves.aspects.PawnInitialMoveAspect
import org.computronium.chess.movegen.moves.aspects.PawnPromotionAspect
import org.computronium.chess.movegen.moves.aspects.RookMoveAspect

class Move(val moveNames: List<String>, val aspects: List<Aspect>) {

    var resultsInCheck: Boolean = false

    fun apply(boardState: BoardState) {

        for (aspect in aspects) {
            aspect.apply(boardState)
        }
    }

    fun rollback(boardState: BoardState) {

        for (aspect in aspects.reversed()) {
            aspect.rollback(boardState)
        }
    }

    override fun toString(): String {
        return moveNames[0]
    }


}