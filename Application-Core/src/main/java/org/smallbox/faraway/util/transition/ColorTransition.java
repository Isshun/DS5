package org.smallbox.faraway.util.transition;

public class ColorTransition extends Transition<Integer> {

    public ColorTransition(Integer fromColor, Integer toColor) {
        super(fromColor, toColor);
    }

    @Override
    protected Integer onGetValue(float progress) {
        return  ((int) (((startValue) & 0xff) * (1 - progress)) + (int) (((endValue) & 0xff) * progress)) +
                ((int) (((startValue >> 8) & 0xff) * (1 - progress)) + (int) (((endValue >> 8) & 0xff) * progress) << 8) +
                ((int) (((startValue >> 16) & 0xff) * (1 - progress)) + (int) (((endValue >> 16) & 0xff) * progress) << 16) +
                ((int) (((startValue >> 24) & 0xff) * (1 - progress)) + (int) (((endValue >> 24) & 0xff) * progress) << 24);
    }

}
