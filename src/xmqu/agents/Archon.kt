package xmqu.agents

import battlecode.common.RobotController
import battlecode.common.RobotType
import xmqu.Dir
import xmqu.Utils
import xmqu.goals.Goal
import xmqu.goals.InitialGoalStub

class Archon(controller: RobotController) : Agent(controller), ProductionUnit {

    fun randomBot(): RobotType {
        val roundNum = controller.roundNum
        val x = Utils.random.nextFloat()

        when {
            (roundNum < RobotType.GARDENER.buildCooldownTurns && x > .1f) -> return RobotType.GARDENER
            (x > (controller.roundNum - RobotType.GARDENER.buildCooldownTurns) / (controller.treeCount + 1 * 500)) ->
                return RobotType.GARDENER
            else -> return RobotType.ARCHON
        }
    }

    fun buildGardener(): Boolean {
        val type = randomBot()
        val dir = Dir.Hex.random().dir
        return buildUnit(type, dir)
    }

    override fun getInitialGoal(): Goal {
        return InitialGoal(this)
    }

    class InitialGoal(val archon: Archon) : InitialGoalStub(archon) {

        override fun onProcess() {
            when {
                archon.buildGardener() -> {
                }
                else -> archon.moveRandomly()
            }
        }
    }
}
