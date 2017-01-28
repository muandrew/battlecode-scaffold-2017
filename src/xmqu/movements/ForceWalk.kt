package xmqu.movements

import battlecode.common.MapLocation
import battlecode.common.RobotInfo
import battlecode.common.RobotType
import xmqu.Vector2D
import xmqu.agents.Agent

class ForceWalk(val forceMap: ForceMap) : Movement {

    fun forceMap(setup: ForceMap.() -> Unit): ForceMap {
        val forceMap = ForceMap()
        forceMap.setup()
        return forceMap
    }

    class ForceMap {
        var enemyRobot: Map<RobotType, Forces> = mapOf()
        var neutralRobot: Map<RobotType, Forces> = mapOf()
        var ownRobot: Map<RobotType, Forces> = mapOf()
        var enemyTree: Forces = Forces(0f, 0f)
        var neutralTree: Forces = Forces(0f, 0f)
        var ownTree: Forces = Forces(0f, 0f)
        var bullets: Forces = Forces(0f, 0f)

        class MapBuilder {
            val map: MutableMap<RobotType, Forces> = mutableMapOf()
            operator fun Pair<RobotType, Forces>.unaryPlus() {
                map.put(this.first, this.second)
            }
        }

        fun enemyRobot(setup: MapBuilder.() -> Unit): MapBuilder {
            val mapBuilder = MapBuilder()
            mapBuilder.setup()
            enemyRobot = mapBuilder.map
            return mapBuilder
        }

        fun neutralRobot(setup: MapBuilder.() -> Unit): MapBuilder {
            val mapBuilder = MapBuilder()
            mapBuilder.setup()
            neutralRobot = mapBuilder.map
            return mapBuilder
        }

        fun ownRobot(setup: MapBuilder.() -> Unit): MapBuilder {
            val mapBuilder = MapBuilder()
            mapBuilder.setup()
            ownRobot = mapBuilder.map
            return mapBuilder
        }

        fun enemyTree(forces: Forces) {
            enemyTree = forces
        }

        fun neutralTree(forces: Forces) {
            neutralTree = forces
        }

        fun ownTree(forces: Forces) {
            ownTree = forces
        }

        fun bullets(forces: Forces) {
            bullets = forces
        }
    }

    data class Forces(val attraction: Float, val repulsion: Float)

    override fun move(agent: Agent): Boolean {
//        var fm = forceMap {
//            enemyRobot {
//                +(RobotType.ARCHON to Forces(1f, 2f))
//                +(RobotType.ARCHON to Forces(1f, 3f))
//            }
//        }

        val dir = Vector2D(0f, 0f)
        val env = agent.env
        val location = env.location
        for (robot in env.robots) {
            when (robot.team) {
                agent.team -> addTo(dir, forceMap.ownRobot, location, robot)
                agent.opponent -> addTo(dir, forceMap.enemyRobot, location, robot)
                else -> addTo(dir, forceMap.neutralRobot, location, robot)
            }
        }
        for (tree in env.trees) {
            when (tree.team) {
                agent.team -> addTo(dir, location, tree.location, forceMap.ownTree)
                agent.opponent -> addTo(dir, location, tree.location, forceMap.enemyTree)
                else -> addTo(dir, location, tree.location, forceMap.neutralTree)
            }
        }
        for (bullet in env.bullets) {
            addTo(dir, location, bullet.location, forceMap.bullets)
        }
        return agent.moveTo(dir.toDirection()) || agent.moveRandomly() || agent.moveRandomly()
    }

    fun addTo(vector: Vector2D, current: MapLocation, target: MapLocation, forces: Forces) {
        vector.addWith(Vector2D.inverseRSq(current, target, forces.attraction))
        vector.addWith(Vector2D.inverseRSq(current, target, forces.repulsion))
    }

    fun addTo(vector: Vector2D, map: Map<RobotType, Forces>, current: MapLocation, target: RobotInfo) {
        val forces = map[target.type]
        if (forces != null) {
            addTo(vector, current, target.location, forces)
        }
    }
}
