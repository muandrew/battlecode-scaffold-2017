package xmqu.agents

import battlecode.common.*
import xmqu.Dir
import xmqu.Utils
import xmqu.goals.*
import xmqu.movements.RandomWalk

class Gardener(controller: RobotController) : ProductionUnit(controller) {
    val randomWalk = RandomWalk(3)

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
            addSubGoal(HexFortress(gardener))
            addSubGoal(FindSettlement(gardener))
            addSubGoal(BuildLumberjack(gardener))
        }

        override fun onProcess() {
            status = processSubGoals()
        }

        override fun onTerminate() {}
    }

    class BuildLumberjack(gardener: Gardener) : BuildUnit(gardener, RobotType.LUMBERJACK, gardener.randomWalk)

    class FindSettlement(val gardener: Gardener) : AtomicGoal(gardener) {
        val ROAM_TIME = 10
        val MAX_STRIDE = 2
        var roamTime = 0
        var dir = Direction.NORTH
        var stridesLeft = MAX_STRIDE

        override fun onActivate() {
            roamTime = ROAM_TIME
        }

        override fun onProcess() {
            if (stridesLeft <= 0) {
                setNewDirection()
            }
            when {
                roamTime > 0 -> {
                    randomWalk()
                    roamTime--
                }
                gardener.canHexUp() -> status = Status.COMPLETE
                else -> gardener.moveRandomly()
            }
        }

        override fun onTerminate() {}

        fun randomWalk() {
            if (gardener.moveTo(dir)) {
                stridesLeft--
            } else {
                setNewDirection()
            }
        }

        fun setNewDirection() {
            dir = Dir.random()
            stridesLeft = MAX_STRIDE
        }
    }

    class HexFortress(val gardener: Gardener) : CompositeGoal(gardener) {

        override fun onActivate() {
            addSubGoal(MaintainFortress(gardener))
            val spaces = Dir.Hex.values()
                    .filter { gardener.controller.canPlantTree(it.dir) }

            if (!spaces.isEmpty()) {
                val gate = spaces[Utils.random.nextInt(spaces.size)]
                spaces
                        .filter { gate != it }
                        .forEach { addSubGoal(BuildWall(gardener, it)) }
            }
        }

        override fun onProcess() {
            status = processSubGoals()
            if (status == Status.FAILED) {
                when (subGoals.head()) {
                    is HexFortress.BuildWall -> status = Status.ACTIVE
                    is MaintainFortress -> {
                        status = Status.INACTIVE
                        activateIfInactive()
                    }
                }
            }
        }

        override fun onTerminate() {}

        class BuildWall(val gardener: Gardener, val hex: Dir.Hex) : AtomicGoal(gardener) {
            var maxTurn: Int = 0

            override fun onActivate() {
                val controller = gardener.controller
                maxTurn = controller.roundNum + controller.buildCooldownTurns + 3
            }

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

        class MaintainFortress(val gardener: Gardener) : AtomicGoal(gardener) {

            override fun onActivate() {}

            override fun onProcess() {
                val trees = gardener.controller.senseNearbyTrees(2f, gardener.team)
                if (trees.isNotEmpty()) {
                    trees.sortBy { it.health }
                    val lowest = trees[0]
                    when {
                        (gardener.areEnemiesNearby(1000f)
                                && Utils.isTrue(0.2f / (1 + gardener.env.nearbyRobots(gardener.team, 49f).size))
                                && gardener.buildUnit(RobotType.LUMBERJACK, Dir.Hex.random().dir)) -> {
                        }
                        (GameConstants.BULLET_TREE_MAX_HEALTH - lowest.health >= GameConstants.WATER_HEALTH_REGEN_RATE
                                && gardener.water(lowest)) -> {
                        }
                        (gardener.controller.treeCount * 3 / gardener.controller.robotCount
                                > Utils.random.nextFloat()
                                && gardener.buildUnit(RobotType.LUMBERJACK, Dir.Hex.random().dir)) -> {
                        }
                        (4.5f - trees.size / 10f > Utils.random.nextFloat()) -> {
                            status = Status.FAILED
                        }
                    }
                } else {
                    status = Status.FAILED
                }
            }


            override fun onTerminate() {}
        }
    }
}
