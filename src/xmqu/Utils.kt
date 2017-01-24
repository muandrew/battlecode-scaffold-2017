package xmqu

import battlecode.common.Direction

fun randomDir(): Direction {
    return Direction((Math.random() * Math.PI * 2).toFloat())
}
