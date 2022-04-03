package org.computronium.chess.core.moves

import org.computronium.chess.core.BoardState
import org.computronium.chess.core.PieceType
import java.util.stream.Collectors

class AlgebraicMoveNameGenerator : MoveNameGenerator {
    /**
     * Generates all the possible move names for the given move.  This is used to disambiguate -- if more
     * than one move has the same 'easy' move name, you can try the next for each, then the next, etc, until
     * one is found that is unique.
     */
    override fun generateMoveNames(from: Int, to: Int, capture: Boolean, promoteTo: PieceType?, boardState: BoardState): List<String> {
        val plain = StringBuffer()
        val withFile = StringBuffer()
        val withRank = StringBuffer()
        val withBoth = StringBuffer()
        val buffers = listOf(plain, withFile, withRank, withBoth)

        val piece = boardState[from]

        if (piece?.type != PieceType.PAWN) {
            appendAll(buffers, "${piece?.type?.letter}")
        } else if (capture) {
            plain.append("${BoardState.fileChar(from)}")
        }

        appendAll(listOf(withFile, withBoth), "${BoardState.fileChar(from)}")
        appendAll(listOf(withRank, withBoth), "${BoardState.rankChar(from)}")

        if (capture) {
            appendAll(buffers, "x")
        }

        appendAll(buffers, "${BoardState.fileChar(to)}")
        appendAll(buffers, "${BoardState.rankChar(to)}")
        if (promoteTo != null) {
            appendAll(buffers, "=${promoteTo.letter}")
        }

        return buffers.stream().map { sb -> sb.toString() }.collect(Collectors.toList())
    }


    private fun appendAll(buffers: List<StringBuffer>, s: String) {
        for (buffer in buffers) {
            buffer.append(s)
        }
    }
}