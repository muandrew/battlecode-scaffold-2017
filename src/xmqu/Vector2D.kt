package xmqu

import battlecode.common.Direction
import battlecode.common.MapLocation

class Vector2D(var x: Float, var y: Float) {

    constructor(start: MapLocation, end: MapLocation)
            : this(end.x - start.x, end.y - start.y)

    fun normalize(): Vector2D {
        val mag = Math.sqrt(magSquared().toDouble()).toFloat()
        x /= mag
        y /= mag
        return this
    }

    fun multiplyWith(mag: Float): Vector2D {
        x *= mag
        y *= mag
        return this
    }

    fun addWith(v: Vector2D): Vector2D {
        x += v.x
        y += v.y
        return this
    }

    fun magSquared(): Float {
        return (x * x + y * y)
    }

    fun clone(): Vector2D {
        return Vector2D(x, y)
    }

    fun toDirection(): Direction {
        return Direction(x, y)
    }
}

fun inverseRSq(start: MapLocation, end: MapLocation, weight: Float): Vector2D {
    val result = Vector2D(start, end)
    val rSq = result.magSquared()
    System.out.println(weight / rSq)
    return result.normalize().multiplyWith(weight / rSq)
}
