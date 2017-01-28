package xmqu

import battlecode.common.*

class Environment {
    var location: MapLocation = MapLocation(0f, 0f)
    var bullets: List<BulletInfo> = listOf()
    var robots: List<RobotInfo> = listOf()
    var trees: List<TreeInfo> = listOf()

    fun update(controller: RobotController) {
        location = controller.location
        bullets = controller.senseNearbyBullets().toList()
        robots = controller.senseNearbyRobots().toList()
        trees = controller.senseNearbyTrees().toList()
    }

    fun nearbyRobots(team: Team, distanceSq: Float): List<RobotInfo> {
        return robots
                .ofTeam(team)
                .withinDistanceSq(location, distanceSq)
    }
}
