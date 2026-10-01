package com.carrombot;
public class AimCalculator {
    private static final float[][] POCKETS = {
        {0.05f, 0.10f},{0.50f, 0.10f},{0.95f, 0.10f},
        {0.05f, 0.72f},{0.50f, 0.72f},{0.95f, 0.72f},
    };
    public static AimResult calculate(int strikerPx, int strikerPy,
            int targetPx, int targetPy, int screenW, int screenH) {
        float sx = (float)strikerPx/screenW;
        float sy = (float)strikerPy/screenH;
        float tx = (float)targetPx/screenW;
        float ty = (float)targetPy/screenH;
        float bestAngle = 0, bestForce = 0.7f, bestScore = Float.MAX_VALUE;
        for (float[] pocket : POCKETS) {
            float ballToPocket = (float)Math.atan2(pocket[1]-ty, pocket[0]-tx);
            float dx = tx-sx, dy = ty-sy;
            float strikerAngle = (float)Math.atan2(dy, dx);
            float dist = (float)Math.sqrt(dx*dx+dy*dy);
            float force = Math.min(1.0f, dist*2.5f);
            float diff = Math.abs(ballToPocket-strikerAngle);
            if (diff < bestScore) {
                bestScore = diff;
                bestAngle = strikerAngle;
                bestForce = force;
            }
        }
        AimResult r = new AimResult();
        r.strikerX = sx; r.strikerY = sy;
        r.angle = bestAngle; r.force = bestForce;
        r.targetX = tx; r.targetY = ty;
        return r;
    }
}
