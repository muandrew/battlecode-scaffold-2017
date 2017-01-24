package xmqu.agents

import battlecode.common.Direction
import battlecode.common.RobotController
import battlecode.common.RobotType

interface ProductionUnit {

    val controller: RobotController

    fun buildUnit(type: RobotType, dir: Direction): Boolean {
        if (controller.canBuildRobot(type, dir)) {
            controller.buildRobot(type, dir)
            return true
        } else {
            return false
        }
    }
}
