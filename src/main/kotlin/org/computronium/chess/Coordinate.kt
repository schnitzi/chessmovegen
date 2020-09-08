package org.computronium.chess

data class Coordinate(val file : Int, val rank : Int) {

    override fun toString(): String {
        return "${fileChar()}${rankChar()}"
    }

    fun rankChar() : Char { return (49 + rank).toChar() }

    fun fileChar() : Char { return (97 + file).toChar() }
}
