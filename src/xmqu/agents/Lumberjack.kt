package xmqu.agents

import battlecode.common.GameConstants
import battlecode.common.MapLocation
import battlecode.common.RobotController
import xmqu.Vector2D
import xmqu.goals.CompositeGoal
import xmqu.goals.Goal
import xmqu.ofTeam
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
            val mag = if (robot.team.isPlayer) 10f else -4f
            heading.addWith(Vector2D.inverseRSq(robot.location, location, mag))
        }
        for (tree in env.trees) {
            heading.addWith(Vector2D.inverseRSq(tree.location, location, -4f))
        }
        val dir = heading.toDirection()
        return moveTo(dir) || moveRandomly() || moveRandomly()
    }

    override fun getInitialGoal(): Goal {
        return InitialGoal(this)
    }

    class InitialGoal(val lumberjack: Lumberjack) : CompositeGoal(lumberjack) {

        val waypoints: MutableList<MapLocation> = lumberjack.controller.getInitialArchonLocations(lumberjack.team.opponent())
                .toMutableList()
                .shuffle()

        override fun onActivate() {}

        override fun onProcess() {
            when {
                moveToEnemy() -> {
                }
                moveToWaypoint() -> {
                }
                else -> lumberjack.moveRandomly()
            }
            attack()
        }

        override fun onTerminate() {}

        fun moveToEnemy(): Boolean {
            val enemies = lumberjack.env.robots.ofTeam(lumberjack.opponent)
            if (enemies.isNotEmpty()) {
                val target = enemies[0].location
                lumberjack.moveTowards(target)
                return true
            } else {
                return false
            }
        }

        fun moveToWaypoint(): Boolean {
            clearWaypoints()
            if (waypoints.isNotEmpty()) {
                lumberjack.moveTowards(waypoints[0])
                return true
            } else {
                return false
            }
        }

        fun clearWaypoints() {
            if (waypoints.isNotEmpty()) {
                if (waypoints[0].distanceSquaredTo(lumberjack.controller.location) < 15) {
                    waypoints.removeAt(0)
                }
            }
        }

        fun attack(): Boolean {
            val robots = lumberjack.controller.senseNearbyRobots(GameConstants.LUMBERJACK_STRIKE_RADIUS).asList()
            val enemyRobots = robots.ofTeam(lumberjack.opponent)
            if (enemyRobots.isNotEmpty() && lumberjack.controller.canStrike()) {
                lumberjack.controller.strike()
                return true
            } else {
                val trees = lumberjack.env.trees.filter { it.team != lumberjack.team }
                if (trees.isNotEmpty()) {
                    if (lumberjack.controller.canChop(trees[0].ID)) {
                        lumberjack.controller.chop(trees[0].ID)
                        return true
                    }
                }
            }
            return false
        }
    }
}
