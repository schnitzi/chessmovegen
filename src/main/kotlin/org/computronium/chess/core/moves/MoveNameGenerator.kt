package org.computronium.chess.core.moves

import org.computronium.chess.core.BoardState
import org.computronium.chess.core.PieceType

interface MoveNameGenerator {
    /**
     * Generates all the possible move names for the given move.  This is used to disambiguate -- if more
     * than one move has the same 'easy' move name, you can try the next for each, then the next, etc, until
     * one is found that is unique.
     */
    fun generateMoveNames(from: Int, to: Int, capture: Boolean = false, promoteTo: PieceType? = null, boardState: BoardState): List<String>
}