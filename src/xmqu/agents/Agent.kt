package xmqu.agents

import battlecode.common.*
import xmqu.Dir
import xmqu.Environment
import xmqu.Vector2D
import xmqu.debug_crash
import xmqu.goals.Goal

abstract class Agent(val controller: RobotController) {

    var env = Environment()
    val team = controller.team!!
    val opponent = team.opponent()!!

    fun run() {
        val goal = getInitialGoal()
        while (true) {
            env.update(controller)
            goal.process()
            try {
                Clock.`yield`()
            } catch (e: Exception) {
                e.printStackTrace()
                debug_crash(e.message ?: "no message.")
            }
        }
    }

    /**
     * Attempts to move towards a direction, has fallback plans.
     */
    open fun moveTowards(dest: MapLocation): Boolean {
        val location = controller.location
        val heading = Vector2D(location, dest).normalize()
        for (bullet in env.bullets) {
            heading.addWith(Vector2D.inverseRSq(bullet.location, location, 30f))
        }
        for (robot in env.robots) {
            val mag = if (robot.team.isPlayer) 10f else {
                when {
                    robot.type == RobotType.LUMBERJACK -> 55f
                    else -> 20f
                }
            }
            heading.addWith(Vector2D.inverseRSq(robot.location, location, mag))
        }
        for (tree in env.trees) {
            heading.addWith(Vector2D.inverseRSq(tree.location, location, 4f))
        }
        val dir = heading.toDirection()
        return moveTo(dir) || moveRandomly() || moveRandomly()
    }

    /**
     * A basic move operation.
     */
    fun moveTo(dir: Direction): Boolean {
        if (controller.canMove(dir)) {
            controller.move(dir)
            return true
        } else {
            return false
        }
    }

    /**
     * Choose one random direction to try.
     */
    fun moveRandomly(): Boolean {
        return moveTo(Dir.random())
    }

    fun areEnemiesNearby(distanceSq: Float): Boolean {
        return env.nearbyRobots(opponent, distanceSq).isNotEmpty()
    }

    abstract fun getInitialGoal(): Goal
}
