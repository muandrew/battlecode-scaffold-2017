package xmqu;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;

@SuppressWarnings("unused")
public strictfp class RobotPlayer {

    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
     **/
    public static void run(RobotController rc) throws GameActionException {
        Main.Companion.run(rc);
    }
}
