import org.openrndr.*
import org.openrndr.color.*
import org.openrndr.draw.*
import org.openrndr.events.*
import org.openrndr.math.*
import org.openrndr.shape.*
import org.openrndr.extra.noclear.NoClear
import kotlin.math.*

suspend fun main() = applicationAsync {
    configure {
        title = "Your Title"
    }
    program {
        var lastInterected = 0.0
        var isInteracted = true

        val urlParamMap = getUrlParamMap(js("window.location.search"))

        val scalePreset: Double = urlParamMap["scale"]?.toDoubleOrNull() ?: 1.0

        // your own variables

        mouse.moved.listen {
            isInteracted = true
        }

        extend(NoClear())

        extend {
            if (isInteracted) {
                lastInterected = seconds
                isInteracted = false
            }

            if ((seconds - lastInterected) > 0.5) return@extend

            val mousePosition = mouse.position


            // your own drawings


            val a = rgb("#ff0000")
            drawer.clear(a)
            drawer.fill = ColorRGBa.WHITE
            drawer.circle(mousePosition, (100.0 + cos(seconds) * 40.0)*scalePreset)
        }
    }
}
