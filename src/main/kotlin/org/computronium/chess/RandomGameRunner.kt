package org.computronium.chess.core

import java.util.*
import kotlin.system.exitProcess

/**
 * Plays a random game of chess, because why not?
 */
fun main() {

    val random = Random()

    val runner = GameRunner.newGame()
    var searchNode = runner.generateSearchNode()

    for (m in 0..300) {
        println(runner.boardState)


        if (searchNode.isCheckmate()) {

            println("Mate!")
            exitProcess(0)

        } else if (searchNode.isStalemate()) {

            println("Stalemate!")
            exitProcess(0)

        }

        val move = searchNode.moves[random.nextInt(searchNode.moves.size)]
        println(move)
        move.apply(runner.boardState)
        searchNode = runner.generateSearchNode()
    }
}
