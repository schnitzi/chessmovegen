package org.computronium.chess.core.moves

import org.computronium.chess.core.BoardState
import org.computronium.chess.core.moves.aspects.Transform

class Move(val moveNames: List<String>, private val transforms: List<Transform>, val metadata: MoveGenerator.MoveMetadata) {

    var nameIndex: Int = 0
    var resultsInCheck: Boolean = false

    fun apply(boardState: BoardState) {

        for (transform in transforms) {
            transform.apply(boardState)
        }
    }

    fun rollback(boardState: BoardState) {

        for (transform in transforms.reversed()) {
            transform.rollback(boardState)
        }
    }

    fun getBaseMoveName() : String {
        return moveNames[nameIndex]
    }

    override fun toString(): String {
        return getBaseMoveName() + if (resultsInCheck) "+" else ""
    }
}
