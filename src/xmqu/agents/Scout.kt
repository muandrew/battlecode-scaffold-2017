package xmqu.agents

import battlecode.common.Direction
import battlecode.common.MapLocation
import battlecode.common.RobotController
import battlecode.common.RobotType
import xmqu.goals.CompositeGoal
import xmqu.goals.Goal
import xmqu.intercepts
import xmqu.shuffle

class Scout(controller: RobotController) : Agent(controller) {

    override fun getInitialGoal(): Goal {
        return InitialGoal(this)
    }

    class InitialGoal(val scout: Scout) : CompositeGoal(scout) {

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
            val controller = scout.controller

            if (controller.teamBullets > RobotType.GARDENER.bulletCost && controller.canFireSingleShot()) {
                val location = controller.location
                scout.env.trees
                        .filter { intercepts(location, target, it.location, it.radius) }
                        .forEach { return false }
                scout.env.robots
                        .filter { it.team != scout.team.opponent() }
                        .filter { intercepts(location, target, it.location, it.radius) }
                        .forEach { return false }
                val dir = Direction(location, target)
                controller.fireSingleShot(dir)
                return true
            } else {
                return false
            }
        }
    }
}
