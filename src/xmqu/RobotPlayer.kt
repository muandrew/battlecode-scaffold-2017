@file:JvmName("RobotPlayer")

package xmqu

import battlecode.common.RobotController
import battlecode.common.RobotType
import xmqu.agents.*

/**
 * run() is the method that is called when a robot is instantiated in the Battlecode world.
 * If this method returns, the robot dies!
 **/
@Suppress("unused")
fun run(controller: RobotController) {
    val agent = when (controller.type) {
        RobotType.ARCHON -> Archon(controller)
        RobotType.GARDENER -> Gardener(controller)
        RobotType.SOLDIER -> Soldier(controller)
        RobotType.TANK -> Tank(controller)
        RobotType.SCOUT -> Scout(controller)
        RobotType.LUMBERJACK -> Lumberjack(controller)
        else -> BasicAgent(controller)
    }
    agent.run()
}
