package org.computronium.chess

import java.util.*
import kotlin.system.exitProcess

fun main(args : Array<String>) {

    val random = Random()

    var boardState = SearchNode.newGame()

    for (m in 0..300) {
        println(boardState)

        if (boardState.isCheckmate()) {

            println("Mate!")
            exitProcess(0)

        } else if (boardState.isStalemate()) {

            println("Stalemate!")
            exitProcess(0)

        }

        val move = boardState.moves[random.nextInt(boardState.moves.size)]
        println(move)
        move.apply(boardState.boardState)
        boardState = SearchNode(boardState.boardState)
    }
}
