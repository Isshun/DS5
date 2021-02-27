package org.smallbox.faraway.util.transition;

public class IntegerTransition extends Transition<Integer> {

    public IntegerTransition(Integer fromValue, Integer toValue) {
        super(fromValue, toValue);
    }

    @Override
    protected Integer onGetValue(float progress) {
        return (int)((startValue * (1 - progress)) + (endValue * progress));
    }

}
