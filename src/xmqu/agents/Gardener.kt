package xmqu.agents

import battlecode.common.GameConstants
import battlecode.common.RobotController
import battlecode.common.RobotType
import battlecode.common.TreeInfo
import xmqu.Dir
import xmqu.goals.AtomicGoal
import xmqu.goals.CompositeGoal
import xmqu.goals.Goal
import xmqu.goals.Status

class Gardener(controller: RobotController) : Agent(controller), ProductionUnit {

    fun canHexUp(): Boolean {
        Dir.Hex.values().forEach {
            if (!controller.canPlantTree(it.dir)) {
                return false
            }
        }
        return true
    }

    fun water(tree: TreeInfo): Boolean {
        if (controller.canWater(tree.ID)) {
            controller.water(tree.ID)
            return true
        } else {
            return false
        }
    }

    override fun getInitialGoal(): Goal {
        return InitialGoal(this)
    }

    class InitialGoal(val gardener: Gardener) : CompositeGoal(gardener) {

        override fun onActivate() {
            addSubGoal(MaintainFortress(gardener))
            addSubGoal(HexFortress(gardener))
            addSubGoal(FindSettlement(gardener))
        }

        override fun onProcess() {
            //TODO fix
            status = processSubGoals()
        }

        override fun onTerminate() {}
    }

    class FindSettlement(val gardener: Gardener) : AtomicGoal(gardener) {

        override fun onActivate() {}

        override fun onProcess() {
            if (gardener.canHexUp()) {
                status = Status.COMPLETE
            } else {
                gardener.moveRandomly()
            }
        }

        override fun onTerminate() {}
    }

    class HexFortress(val gardener: Gardener) : CompositeGoal(gardener) {

        override fun onActivate() {
            val gate = Dir.Hex.random()
            Dir.Hex.values()
                    .filter { gate != it }
                    .forEach { addSubGoal(BuildWall(gardener, it)) }
        }

        override fun onProcess() {
            //TODO fix
            status = processSubGoals()
        }

        override fun onTerminate() {}

        class BuildWall(val gardener: Gardener, val hex: Dir.Hex) : AtomicGoal(gardener) {
            val maxTurn: Int

            init {
                val controller = gardener.controller
                maxTurn = controller.roundNum + controller.buildCooldownTurns + 3
            }

            override fun onActivate() {}

            override fun onProcess() {
                val controller = gardener.controller
                when {
                    (controller.buildCooldownTurns > 0) -> {
                    }
                    (controller.canPlantTree(hex.dir)) -> {
                        controller.plantTree(hex.dir)
                        status = Status.COMPLETE
                    }
                    (controller.roundNum > maxTurn) -> status = Status.FAILED
                }
            }

            override fun onTerminate() {}
        }
    }

    class MaintainFortress(val gardener: Gardener) : AtomicGoal(gardener) {

        override fun onActivate() {}

        override fun onProcess() {
            val trees = gardener.controller.senseNearbyTrees(2f, gardener.team)
            if (trees.isNotEmpty()) {
                trees.sortBy { it.health }
                val lowest = trees[0]
                if (GameConstants.BULLET_TREE_MAX_HEALTH - lowest.health >= GameConstants.WATER_HEALTH_REGEN_RATE
                        && gardener.water(lowest)) {
                } else {
                    //TODO figure out
                    gardener.buildUnit(RobotType.SCOUT, Dir.Hex.random().dir)
                }
            } else {
                status = Status.FAILED
            }
        }

        override fun onTerminate() {}
    }
}
