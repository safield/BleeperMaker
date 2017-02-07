package com.safield.BleeperMaker;


public class AudioUtility {
    /**
     * where x1 and x2 are the samples being interpolated between, x0 is x1's left neighbor, and x3 is x2's right neighbor. t is [0, 1], denoting the interpolation position between x1 and x2.
     */
    public static float interpolateCubic(float x0, float x1, float x2, float x3, float t)
    {
        if (x0 > 1 || x1 > 1 || x2 > 1 || x3 > 1)
            throw new AssertionError("ToneMaker -  Clipped value detected");
        float a0, a1, a2, a3;
        a0 = x3 - x2 - x0 + x1;
        a1 = x0 - x1 - a0;
        a2 = x2 - x0;
        a3 = x1;

        float out = (a0 * (t * t * t)) + (a1 * (t * t)) + (a2 * t) + (a3);
        if (out > 1)
            throw new AssertionError("ToneMaker - Clipped interpolated value detected");

        return out;
    }

    public static float InterpolateHermite4pt3oX(float x0, float x1, float x2, float x3, float t)
    {
        float c0 = x1;
        float c1 = .5F * (x2 - x0);
        float c2 = x0 - (2.5F * x1) + (2 * x2) - (.5F * x3);
        float c3 = (.5F * (x3 - x0)) + (1.5F * (x1 - x2));
        return (((((c3 * t) + c2) * t) + c1) * t) + c0;
    }
}
