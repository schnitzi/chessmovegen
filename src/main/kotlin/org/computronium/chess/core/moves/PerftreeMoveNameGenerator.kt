package org.computronium.chess.core.moves

import org.computronium.chess.core.BoardState
import org.computronium.chess.core.PieceType
import java.util.*

class PerftreeMoveNameGenerator : MoveNameGenerator {

    override fun generateMoveNames(from: Int, to: Int, capture: Boolean, promoteTo: PieceType?, boardState: BoardState): List<String> {

        val sb = StringBuilder()
        sb.append(BoardState.squareName(from))
        sb.append(BoardState.squareName(to))
        if (promoteTo != null) {
            sb.append(promoteTo.name)
        }
        return Collections.singletonList(sb.toString())
    }
}