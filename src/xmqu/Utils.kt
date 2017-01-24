package xmqu

import battlecode.common.MapLocation
import java.util.*

class Utils {
    companion object {
        val random = Random()
    }
}

fun <E> MutableList<E>.swap(a: Int, b: Int): MutableList<E> {
    val temp = this[a]
    this[a] = this[b]
    this[b] = temp
    return this
}

fun <E> MutableList<E>.shuffle(): MutableList<E> {
    var remaining = this.size
    while (remaining > 1) {
        this.swap(Utils.random.nextInt(remaining), remaining - 1)
        remaining--
    }
    return this
}

/**
 * NateS
 * http://stackoverflow.com/questions/1585525/how-to-find-the-intersection-point-between-a-line-and-a-rectangle
 * Tired, don't want to reinvent wheel.
 */
fun intercepts(a: MapLocation, b: MapLocation, center: MapLocation, radius: Float): Boolean {
    val x1 = a.x
    val y1 = a.y
    val x2 = b.x
    val y2 = b.y
    // since we are using circles
    val radiusM = radius - 2
    val minX = center.x - radiusM
    val minY = center.y - radiusM
    val maxX = center.x + radiusM
    val maxY = center.y + radiusM

    // Completely outside.
    if ((x1 <= minX && x2 <= minX)
            || (y1 <= minY && y2 <= minY)
            || (x1 >= maxX && x2 >= maxX)
            || (y1 >= maxY && y2 >= maxY))
        return false;

    val m = (y2 - y1) / (x2 - x1)

    var y = m * (minX - x1) + y1
    if (y > minY && y < maxY) return true

    y = m * (maxX - x1) + y1
    if (y > minY && y < maxY) return true

    var x = (minY - y1) / m + x1
    if (x > minX && x < maxX) return true

    x = (maxY - y1) / m + x1
    if (x > minX && x < maxX) return true

    return false
}
