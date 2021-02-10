package org.smallbox.faraway.util.transition;

public class DoubleTransition extends Transition<Double> {

    public DoubleTransition(Double fromValue, Double toValue) {
        super(fromValue, toValue);
    }

    @Override
    protected Double onGetValue(float progress) {
        return (startValue * (1 - progress)) + (endValue * progress);
    }

}
