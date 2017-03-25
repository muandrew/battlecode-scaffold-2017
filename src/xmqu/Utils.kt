package xmqu

import battlecode.common.MapLocation
import battlecode.common.RobotInfo
import battlecode.common.RobotType
import battlecode.common.Team
import java.util.*

class Utils {
    companion object {
        val random = Random()
        fun isTrue(fraction: Float): Boolean {
            return fraction > random.nextFloat()
        }
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

fun List<RobotInfo>.ofTeam(team: Team): List<RobotInfo> {
    return this.filter { it.team == team }
}

fun List<RobotInfo>.notOfTeam(team: Team): List<RobotInfo> {
    return this.filter { it.team != team }
}

fun List<RobotInfo>.ofType(type: RobotType): List<RobotInfo> {
    return this.filter { it.type == type }
}

fun List<RobotInfo>.withinDistanceSq(location: MapLocation, distanceSq: Float): List<RobotInfo> {
    return this.filter { it.location.distanceSquaredTo(location) < distanceSq }
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
    val minX = center.x - radius
    val minY = center.y - radius
    val maxX = center.x + radius
    val maxY = center.y + radius

    // Completely outside.
    if ((x1 <= minX && x2 <= minX)
            || (y1 <= minY && y2 <= minY)
            || (x1 >= maxX && x2 >= maxX)
            || (y1 >= maxY && y2 >= maxY))
        return false

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
