package xmqu

import battlecode.common.Direction
import battlecode.common.RobotController

fun debug_crash(message: String) {
    throw Exception(message)
}

fun debug_move(controller: RobotController, dir: Direction) {
    controller.setIndicatorLine(
            controller.location,
            controller.location.add(dir),
            0, 255, 0)
}
