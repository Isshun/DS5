package org.smallbox.faraway.core.module.path.spline;

import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 24/03/2017.
 */
public class BezierPath {
    private int SEGMENTS_PER_CURVE = 64;
    private float MINIMUM_SQR_DISTANCE = 0.01f;

    // This corresponds to about 172 degrees, 8 degrees from a traight line
    private float DIVISION_THRESHOLD = -0.99f;

    private List<Vector3> controlPoints;

    private int curveCount; //how many bezier curves in this path?

    /**
     * Constructs a new empty Bezier curve. Use one of these methods
     * to add points: SetControlPoints, Interpolate, SamplePoints.
     */
    public BezierPath() {
        controlPoints = new ArrayList<>();
    }

    /**
     * Sets the control points of this Bezier path.
     * Points 0-3 forms the first Bezier curve, points
     * 3-6 forms the second curve, etc.
     */
    public void SetControlPoints(List<Vector3> newControlPoints) {
        controlPoints.clear();
        controlPoints.addAll(newControlPoints);
        curveCount = (controlPoints.size() - 1) / 3;
    }

    /**
     * Returns the control points for this Bezier curve.
     */
    public List<Vector3> GetControlPoints() {
        return controlPoints;
    }
//
//    /**
//     * Calculates a Bezier interpolated path for the given points.
//     */
//    public void Interpolate(List<Vector3> segmentPoints, float scale) {
//        controlPoints.clear();
//
//        if (segmentPoints.size() < 2) {
//            return;
//        }
//
//        for (int i = 0; i < segmentPoints.size(); i++) {
//            if (i == 0) // is first
//            {
//                Vector3 p1 = segmentPoints.get(i);
//                Vector3 p2 = segmentPoints.get(i + 1);
//
//                Vector3 tangent = new Vector3(p2);
//                tangent.sub(p1);
//                Vector3 q1 = new Vector3(p1.x + scale * tangent.x, p1.y + scale * tangent.y, 0);
//
//                controlPoints.add(p1);
//                controlPoints.add(q1);
//            } else if (i == segmentPoints.size() - 1) //last
//            {
//                Vector3 p0 = segmentPoints.get(i - 1);
//                Vector3 p1 = segmentPoints.get(i);
//                Vector3 tangent = new Vector3(p1).sub(p0);
//                Vector3 q0 = new Vector3(p1).sub(new Vector3(tangent.x * scale, tangent.y * scale, 0));
//
//                controlPoints.add(q0);
//                controlPoints.add(p1);
//            } else {
//                Vector3 p0 = segmentPoints.get(i - 1);
//                Vector3 p1 = segmentPoints.get(i);
//                Vector3 p2 = segmentPoints.get(i + 1);
//                Vector3 tangent = new Vector3(p2).sub(p0).nor();
//                Vector3 q0 = p1 - scale * tangent * new Vector3(p1).sub(p0).magnitude;
//                Vector3 q1 = p1 + scale * tangent * new Vector3(p2).sub(p1).magnitude;
//
//                controlPoints.add(q0);
//                controlPoints.add(p1);
//                controlPoints.add(q1);
//            }
//        }
//
//        curveCount = (controlPoints.Count - 1) / 3;
//    }
//
//    /**
//     * Sample the given points as a Bezier path.
//     */
//    public void SamplePoints(List<Vector3> sourcePoints, float minSqrDistance, float maxSqrDistance, float scale) {
//        if (sourcePoints.size() < 2) {
//            return;
//        }
//
//        Stack<Vector3> samplePoints = new Stack<>();
//
//        samplePoints.add(sourcePoints[0]);
//
//        Vector3 potentialSamplePoint = sourcePoints[1];
//
//        int i = 2;
//
//        for (i = 2; i < sourcePoints.Count; i++) {
//            if (
//                    ((potentialSamplePoint - sourcePoints[i]).sqrMagnitude > minSqrDistance) &&
//                            ((samplePoints.Peek() - sourcePoints[i]).sqrMagnitude > maxSqrDistance)) {
//                samplePoints.Push(potentialSamplePoint);
//            }
//
//            potentialSamplePoint = sourcePoints[i];
//        }
//
//        //now handle last bit of curve
//        Vector3 p1 = samplePoints.Pop(); //last sample point
//        Vector3 p0 = samplePoints.Peek(); //second last sample point
//        Vector3 tangent = (p0 - potentialSamplePoint).normalized;
//        float d2 = (potentialSamplePoint - p1).magnitude;
//        float d1 = (p1 - p0).magnitude;
//        p1 = p1 + tangent * ((d1 - d2) / 2);
//
//        samplePoints.Push(p1);
//        samplePoints.Push(potentialSamplePoint);
//
//
//        Interpolate(new List<Vector3>(samplePoints), scale);
//    }

    /**
     * Caluclates a point on the path.
     *
     * @param curveIndex The index of the curve that the point is on. For example,
     *                   the second curve (index 1) is the curve with controlpoints 3, 4, 5, and 6.
     * @param t          The paramater indicating where on the curve the point is. 0 corresponds
     *                   to the "left" point, 1 corresponds to the "right" end point.
     */
    public Vector3 CalculateBezierPoint(int curveIndex, float t) {
        int nodeIndex = curveIndex * 3;

        Vector3 p0 = controlPoints.get(nodeIndex);
        Vector3 p1 = controlPoints.get(nodeIndex + 1);
        Vector3 p2 = controlPoints.get(nodeIndex + 2);
        Vector3 p3 = controlPoints.get(nodeIndex + 3);

        return CalculateBezierPoint(t, p0, p1, p2, p3);
    }

    /**
     * Gets the drawing points. This implementation simply calculates a certain number
     * of points per curve.
     */
    public List<Vector3> GetDrawingPoints0() {
        List<Vector3> drawingPoints = new ArrayList<>();

        for (int curveIndex = 0; curveIndex < curveCount; curveIndex++) {
            if (curveIndex == 0) //Only do this for the first end point. 
            //When i != 0, this coincides with the 
            //end point of the previous segment,
            {
                drawingPoints.add(CalculateBezierPoint(curveIndex, 0));
            }

            for (int j = 1; j <= SEGMENTS_PER_CURVE; j++) {
                float t = j / (float) SEGMENTS_PER_CURVE;
                drawingPoints.add(CalculateBezierPoint(curveIndex, t));
            }
        }

        return drawingPoints;
    }

    /**
     * Gets the drawing points. This implementation simply calculates a certain number
     * of points per curve.
     * <p>
     * This is a lsightly different inplementation from the one above.
     */
    public List<Vector3> GetDrawingPoints1() {
        List<Vector3> drawingPoints = new ArrayList<>();

        for (int i = 0; i < controlPoints.size() - 3; i += 3) {
            Vector3 p0 = controlPoints.get(i);
            Vector3 p1 = controlPoints.get(i + 1);
            Vector3 p2 = controlPoints.get(i + 2);
            Vector3 p3 = controlPoints.get(i + 3);

            if (i == 0) //only do this for the first end point. When i != 0, this coincides with the end point of the previous segment,
            {
                drawingPoints.add(CalculateBezierPoint(0, p0, p1, p2, p3));
            }

            for (int j = 1; j <= SEGMENTS_PER_CURVE; j++) {
                float t = j / (float) SEGMENTS_PER_CURVE;
                drawingPoints.add(CalculateBezierPoint(t, p0, p1, p2, p3));
            }
        }

        return drawingPoints;
    }
//
//    /**
//     * This gets the drawing points of a bezier curve, using recursive division,
//     * which results in less points for the same accuracy as the above implementation.
//     */
//    public List<Vector3> GetDrawingPoints2() {
//        List<Vector3> drawingPoints = new ArrayList<>();
//
//        for (int curveIndex = 0; curveIndex < curveCount; curveIndex++) {
//            List<Vector3> bezierCurveDrawingPoints = FindDrawingPoints(curveIndex);
//
//            if (curveIndex != 0) {
//                //remove the fist point, as it coincides with the last point of the previous Bezier curve.
//                bezierCurveDrawingPoints.remove(0);
//            }
//
//            drawingPoints.addAll(bezierCurveDrawingPoints);
//        }
//
//        return drawingPoints;
//    }
//
//    List<Vector3> FindDrawingPoints(int curveIndex) {
//        List<Vector3> pointList = new ArrayList<>();
//
//        Vector3 left = CalculateBezierPoint(curveIndex, 0);
//        Vector3 right = CalculateBezierPoint(curveIndex, 1);
//
//        pointList.add(left);
//        pointList.add(right);
//
//        FindDrawingPoints(curveIndex, 0, 1, pointList, 1);
//
//        return pointList;
//    }


//    /**
//     * @returns the number of points added.
//     */
//    int FindDrawingPoints(int curveIndex, float t0, float t1, List<Vector3> pointList, int insertionIndex) {
//        Vector3 left = CalculateBezierPoint(curveIndex, t0);
//        Vector3 right = CalculateBezierPoint(curveIndex, t1);
//
//        if (new Vector3(left).sub(right).sqrMagnitude < MINIMUM_SQR_DISTANCE) {
//            return 0;
//        }
//
//        float tMid = (t0 + t1) / 2;
//        Vector3 mid = CalculateBezierPoint(curveIndex, tMid);
//
//        Vector3 leftDirection = (left - mid).normalized;
//        Vector3 rightDirection = (right - mid).normalized;
//
//        if (Vector3.Dot(leftDirection, rightDirection) > DIVISION_THRESHOLD || Mathf.Abs(tMid - 0.5f) < 0.0001f) {
//            int pointsaddedCount = 0;
//
//            pointsaddedCount += FindDrawingPoints(curveIndex, t0, tMid, pointList, insertionIndex);
//            pointList.add(insertionIndex + pointsaddedCount, mid);
//            pointsaddedCount++;
//            pointsaddedCount += FindDrawingPoints(curveIndex, tMid, t1, pointList, insertionIndex + pointsaddedCount);
//
//            return pointsaddedCount;
//        }
//
//        return 0;
//    }


    /**
     * Caluclates a point on the Bezier curve represented with the four controlpoints given.
     */
    private Vector3 CalculateBezierPoint(float t, Vector3 p0, Vector3 p1, Vector3 p2, Vector3 p3) {
        float u = 1 - t;
        float tt = t * t;
        float uu = u * u;
        float uuu = uu * u;
        float ttt = tt * t;

        Vector3 p = new Vector3(); //first term
        p.set(uuu * p0.x, uuu * p0.y, 0);
        p.add(3 * uu * t * p1.x, 3 * uu * t * p1.y, 0); //second term
        p.add(3 * u * tt * p2.x, 3 * u * tt * p2.y, 0); //third term
        p.add(ttt * p3.x, ttt * p3.y, 0); //fourth term

        return p;

    }
}