package xmqu.agents

import battlecode.common.MapLocation
import battlecode.common.RobotController
import xmqu.Vector2D
import xmqu.goals.CompositeGoal
import xmqu.goals.Goal
import xmqu.shuffle

class Lumberjack(controller: RobotController) : Agent(controller) {

    /**
     * Attempts to move towards a direction, has fallback plans.
     */
    override fun moveTowards(dest: MapLocation): Boolean {
        val location = controller.location
        val heading = Vector2D(location, dest).normalize()
        for (bullet in env.bullets) {
            heading.addWith(Vector2D.inverseRSq(bullet.location, location, 30f))
        }
        for (robot in env.robots) {
            val mag = if (robot.team.isPlayer) 10f else 5f
            heading.addWith(Vector2D.inverseRSq(robot.location, location, mag))
        }
        for (tree in env.trees) {
            heading.addWith(Vector2D.inverseRSq(tree.location, location, 6f))
        }
        val dir = heading.toDirection()
        return moveTo(dir) || moveRandomly() || moveRandomly()
    }

    override fun getInitialGoal(): Goal {
        return InitialGoal(this)
    }

    class InitialGoal(val scout: Lumberjack) : CompositeGoal(scout) {

        val waypoints: MutableList<MapLocation> = scout.controller.getInitialArchonLocations(scout.team.opponent())
                .toMutableList()
                .shuffle()

        override fun onActivate() {}

        override fun onProcess() {
            when {
                moveToEnemy() -> {
                }
                moveToWaypoint() -> {
                }
                else -> scout.moveRandomly()
            }
        }

        override fun onTerminate() {}

        fun moveToEnemy(): Boolean {
            val enemies = scout.env.robots
                    .filter { it.team == scout.team.opponent() }
            if (enemies.isNotEmpty()) {
                val target = enemies[0].location
                scout.moveTowards(target)
                fire(target)
                return true
            } else {
                return false
            }
        }

        fun moveToWaypoint(): Boolean {
            clearWaypoints()
            if (waypoints.isNotEmpty()) {
                scout.moveTowards(waypoints[0])
                return true
            } else {
                return false
            }
        }

        fun clearWaypoints() {
            if (waypoints.isNotEmpty()) {
                if (waypoints[0].distanceSquaredTo(scout.controller.location) < 15) {
                    waypoints.removeAt(0)
                }
            }
        }

        fun fire(target: MapLocation): Boolean {
            if (scout.controller.canChop(target)) {
                scout.controller.chop(target)
                return true
            } else {
                return false
            }
        }
    }
}
