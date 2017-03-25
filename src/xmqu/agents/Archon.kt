package xmqu.agents

import battlecode.common.RobotController
import battlecode.common.RobotType
import xmqu.Dir
import xmqu.Utils
import xmqu.goals.*
import xmqu.movements.RandomWalk

class Archon(controller: RobotController) : ProductionUnit(controller) {
    val randomWalk = RandomWalk(3)

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

    fun sometimesBuildGardener(): Boolean {
        val type = randomBot()
        val dir = Dir.Hex.random().dir
        return buildUnit(type, dir)
    }

    override fun getInitialGoal(): Goal {
        return InitialGoal(this)
    }

    class InitialGoal(val archon: Archon) : CompositeGoal(archon) {
        override fun onActivate() {
            addSubGoal(SteadyStateGoal(archon))
            addSubGoal(BuildGardener(archon))
            addSubGoal(BuildGardener(archon))
            addSubGoal(Wander(archon, RobotType.GARDENER.buildCooldownTurns + 2))
            addSubGoal(BuildGardener(archon))

        }

        override fun onProcess() {
            status = processSubGoals()
        }

        override fun onTerminate() {}
    }

    class SteadyStateGoal(val archon: Archon) : InitialGoalStub(archon) {

        override fun onProcess() {
            when {
                archon.sometimesBuildGardener() -> {
                }
                else -> archon.randomWalk.move(archon)
            }
        }
    }

    class BuildGardener(archon: Archon) : BuildUnit(archon, RobotType.GARDENER, archon.randomWalk)

    class Wander(val archon: Archon, var turns: Int) : AtomicGoal(archon) {

        override fun onActivate() {}

        override fun onProcess() {
            if (turns > 0) {
                archon.randomWalk.move(archon)
                turns--
            } else {
                status = Status.COMPLETE
            }
        }

        override fun onTerminate() {}
    }
}
