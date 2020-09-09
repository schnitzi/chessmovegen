package org.computronium.chess

import java.awt.*
import java.awt.geom.*
import java.awt.image.BufferedImage
import java.util.*
import javax.swing.*
import javax.swing.border.BevelBorder

/**
 * Chessboard renderer, converted from a StackOverflow answer in Java.
 *
 * @author Andrew Thompson
 * @see {@link https://stackoverflow.com/a/18686753/215403}
 */
internal class ChessBoard : JPanel(GridLayout(0, 8, 0, 0)) {

    companion object {
        private const val KING = 0
        private const val QUEEN = 1
        private const val CASTLE = 2
        private const val BISHOP = 3
        private const val KNIGHT = 4
        private const val PAWN = 5

        private const val WHITE = 0
        private const val BLACK = 1

        /**
         * Unicode values for chess pieces.
         */
        private val pieces = arrayOf("\u2654", "\u2655", "\u2656", "\u2657", "\u2658", "\u2659")
        private val order = intArrayOf(CASTLE, KNIGHT, BISHOP, QUEEN, KING, BISHOP, KNIGHT, CASTLE)

        @JvmStatic
        fun main(args: Array<String>) {

            val pawnRow = intArrayOf(PAWN, PAWN, PAWN, PAWN, PAWN, PAWN, PAWN, PAWN)

            val r = Runnable {

                val gradientFill = true
                val gui = ChessBoard()
                gui.border = BevelBorder(BevelBorder.LOWERED, Color.GRAY.brighter(), Color.GRAY, Color.GRAY.darker(), Color.GRAY)
                // set up a chess board
                gui.addPiecesToContainer(gui, WHITE, BLACK, order, gradientFill)
                gui.addPiecesToContainer(gui, BLACK, BLACK, pawnRow, gradientFill)
                gui.addBlankLabelRow(gui, WHITE)
                gui.addBlankLabelRow(gui, BLACK)
                gui.addBlankLabelRow(gui, WHITE)
                gui.addBlankLabelRow(gui, BLACK)
                gui.addPiecesToContainer(gui, WHITE, WHITE, pawnRow, gradientFill)
                gui.addPiecesToContainer(gui, BLACK, WHITE, order, gradientFill)
                JOptionPane.showMessageDialog(null, gui, "Chessboard", JOptionPane.INFORMATION_MESSAGE)
                val tileSet = JPanel(GridLayout(0, 6, 0, 0))
                tileSet.isOpaque = false
                val tileSetOrder = intArrayOf(KING, QUEEN, CASTLE, KNIGHT, BISHOP, PAWN)
                gui.addPiecesToContainer(tileSet, Color(0, 0, 0, 0), BLACK, tileSetOrder, gradientFill)
                gui.addPiecesToContainer(tileSet, Color(0, 0, 0, 0), WHITE, tileSetOrder, gradientFill)
            }
            SwingUtilities.invokeLater(r)
        }
    }

    /*
     * Colors..
     */
    private val outlineColor = Color.DARK_GRAY
    private val pieceColors = arrayOf(Color(203, 203, 197), Color(192, 142, 60))
    private val squareColors = arrayOf(Color.WHITE, Color.BLACK)

    init {
        font = Font("Sans-Serif", Font.PLAIN, 64)
    }

    private fun separateShapeIntoRegions(shape: Shape): ArrayList<Shape> {
        val regions = ArrayList<Shape>()
        val pi = shape.getPathIterator(null)
        var gp = GeneralPath()
        while (!pi.isDone) {
            val coords = DoubleArray(6)
            val pathSegmentType = pi.currentSegment(coords)
            val windingRule = pi.windingRule
            gp.windingRule = windingRule
            when (pathSegmentType) {
                PathIterator.SEG_MOVETO -> {
                    gp = GeneralPath()
                    gp.windingRule = windingRule
                    gp.moveTo(coords[0], coords[1])
                }
                PathIterator.SEG_LINETO -> {
                    gp.lineTo(coords[0], coords[1])
                }
                PathIterator.SEG_QUADTO -> {
                    gp.quadTo(coords[0], coords[1], coords[2], coords[3])
                }
                PathIterator.SEG_CUBICTO -> {
                    gp.curveTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5])
                }
                PathIterator.SEG_CLOSE -> {
                    gp.closePath()
                    regions.add(Area(gp))
                }
                else -> {
                    System.err.println("Unexpected value! $pathSegmentType")
                }
            }
            pi.next()
        }
        return regions
    }

    private fun getImageForChessPiece(piece: Int, side: Int, gradient: Boolean): BufferedImage {
        val sz = font.size
        val bi = BufferedImage(sz, sz, BufferedImage.TYPE_INT_ARGB)
        val g = bi.createGraphics()
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE)
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY)

        val frc = g.fontRenderContext
        val gv = font.createGlyphVector(frc, pieces[piece])
        val shape1 = gv.outline
        val r = shape1.bounds
        val spaceX = sz - r.width
        val spaceY = sz - r.height
        val trans = AffineTransform.getTranslateInstance(-r.x + (spaceX / 2).toDouble(), -r.y + (spaceY / 2).toDouble())
        val shapeCentered = trans.createTransformedShape(shape1)
        val imageShape: Shape = Rectangle2D.Double(0.0, 0.0, sz.toDouble(), sz.toDouble())
        val imageShapeArea = Area(imageShape)
        val shapeArea = Area(shapeCentered)
        imageShapeArea.subtract(shapeArea)
        val regions = separateShapeIntoRegions(imageShapeArea)
        g.stroke = BasicStroke(1F)
        g.color = pieceColors[side]
        val baseColor = pieceColors[side]
        if (gradient) {
            val c1 = baseColor.brighter()
            val gp = GradientPaint((sz / 2 - r.width / 4).toFloat(), (sz / 2 - r.height / 4).toFloat(), c1,
                (sz / 2 + r.width / 4).toFloat(), (sz / 2 + r.height / 4).toFloat(), baseColor, false)
            g.paint = gp
        } else {
            g.color = baseColor
        }
        for (region in regions) {
            val r1 = region.bounds
            if (r1.getX() >= 0.001 || r1.getY() >= 0.001) {
                g.fill(region)
            }
        }
        g.color = outlineColor
        g.fill(shapeArea)
        g.dispose()
        return bi
    }

    private fun addColoredUnicodeCharToContainer(c: Container, piece: Int, side: Int, bg: Color?, gradient: Boolean) {
        val l = JLabel(ImageIcon(getImageForChessPiece(piece, side, gradient)), JLabel.CENTER)
        l.background = bg
        l.isOpaque = true
        c.add(l)
    }

    private fun addPiecesToContainer(c: Container, initialSquareColor: Int, side: Int, pieces: IntArray, gradient: Boolean) {
        var squareColor = initialSquareColor
        for (piece in pieces) {
            addColoredUnicodeCharToContainer(c, piece, side, if (squareColor++ % 2 == BLACK) Color.BLACK else Color.WHITE, gradient)
        }
    }

    private fun addPiecesToContainer(c: Container, bg: Color?, side: Int, pieces: IntArray, gradient: Boolean) {
        for (piece in pieces) {
            addColoredUnicodeCharToContainer(c, piece, side, bg, gradient)
        }
    }

    private fun addBlankLabelRow(c: Container, initialSquareColor: Int) {
        var squareColor = initialSquareColor
        for (ii in 0..7) {
            val l = JLabel()
            val bg = squareColors[squareColor]
            squareColor = 1 - squareColor
            l.background = bg
            l.isOpaque = true
            c.add(l)
        }
    }
}