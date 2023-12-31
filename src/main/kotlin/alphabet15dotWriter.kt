import org.openrndr.*
import org.openrndr.color.*
import org.openrndr.draw.*
import org.openrndr.events.*
import org.openrndr.math.*
import org.openrndr.shape.*
import kotlin.math.*


fun alphabet15dotWriter(drawer: Drawer, defaultStyle: A15DWriteStyle = A15DWriteStyle(), init: Alphabet15dotWriter.() -> Unit) {
    Alphabet15dotWriter(drawer, defaultStyle).init()
}

class A15DWriteStyle() {
    var scale = Vector2(15.0,20.0)
    var weight: Double = 3.0
    var dotScale: Double = 1.5
    var charGap: Double = 1.0
    var lineGap: Double = 3.0
    var color: ColorRGBa = ColorRGBa.BLACK
    var alphabet15dotMap = alphabet15dotASCIIMap
    var linearTransition = Matrix22.IDENTITY
    var isCursorXTransiting = false
    var isCursorYTransiting = false
}

class Alphabet15dotWriter(val drawer: Drawer, val defaultStyle: A15DWriteStyle = A15DWriteStyle()) {

    fun newWriting(style: A15DWriteStyle = defaultStyle, action: A15DLine.() -> Unit) {
        A15DLine(style).action()
    }

    inner class A15DLine(val style: A15DWriteStyle) {
        var cursorX = 0.0
        var cursorY = 0.0
        var cursor: Vector2
            get() = Vector2(cursorX,cursorY)
            set(value) {
                cursorX = value.x
                cursorY = value.y
            }
        
        val textHeight: Double
            get() = style.scale.y * 4

        fun textWidth(text: String): Double =
            if (text.length == 0) 0.0
            else style.scale.x * ((text.length * 2) + ((text.length - 1) * style.charGap))

        fun move(x: Double, y: Double) {
            cursorX = x
            cursorY = y
        }

        fun writeLine(text: String, dotMap: (Vector2)->Vector2 = { it }) {
            val originCursor = cursor + 0.0
            val circleCenters = mutableListOf<Vector2>()
            val contourList = mutableListOf<ShapeContour>()
            text.toList().forEach { c ->
                (style.alphabet15dotMap[c] ?: alphabet15dotASCIIMap['$']!!).forEach { dL ->
                    if (dL.size == 1) {
                        val (x,y) = separateOneDigit(dL[0])

                        val rawPoint = Vector2(x.toDouble(), y.toDouble()) * style.scale
                        val point = dotMap(style.linearTransition * rawPoint + cursor)

                        circleCenters += point
                    }
                    else {
                        val dVL = dL.map {
                            val (x,y) = separateOneDigit(it)
                            dotMap(style.linearTransition * (Vector2(x.toDouble(), y.toDouble()) * style.scale) + cursor)
                        }
                        val con = ShapeContour.fromPoints(dVL, closed = false)
                        contourList += con
                    }
                }
                if (!style.isCursorXTransiting) { cursorX += style.scale.x * (2 + style.charGap) }
                else { cursor = cursor + (style.linearTransition * Vector2(style.scale.x * (2 + style.charGap), 0.0)) }
            }

            drawer.apply {
                fill = style.color
                stroke = null
                circles(circleCenters, style.weight * style.dotScale)
            }
            drawer.apply {
                fill = null
                stroke = style.color
                strokeWeight = style.weight
                contours(contourList)
            }
            
            cursor = originCursor + 0.0
            if (!style.isCursorYTransiting) { cursorY += style.scale.y * (4 + style.lineGap) }
            cursor = cursor + (style.linearTransition * Vector2(0.0, style.scale.y * (4 + style.lineGap)))
        }
    }

}






