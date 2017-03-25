package xmqu.goals

import battlecode.common.RobotType
import xmqu.Dir
import xmqu.agents.ProductionUnit
import xmqu.movements.Movement

open class BuildUnit(val productionUnit: ProductionUnit, val unitType: RobotType, val movement: Movement) : AtomicGoal(productionUnit) {

    override fun onActivate() {}

    override fun onProcess() {
        val dir = Dir.Hex.random().dir
        if (productionUnit.buildUnit(unitType, dir)) {
            status = Status.COMPLETE
        } else {
            movement.move(productionUnit)
        }
    }

    override fun onTerminate() {}
}