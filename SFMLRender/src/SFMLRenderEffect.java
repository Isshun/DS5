import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.Transform;
import org.smallbox.faraway.engine.RenderEffect;

/**
 * Created by Alex on 27/05/2015.
 */
public class SFMLRenderEffect extends RenderEffect {
    private RenderStates _render;
    private Transform _transform;

    @Override
    public void setTranslate(int x, int y) {
        _transform = new Transform();
        _transform = Transform.translate(_transform, x, y);
        _render = new RenderStates(_transform);
    }

    public RenderStates getRender() {
        if (_render == null) {
            if (_transform == null) {
                _transform = new Transform();
            }
            _render = new RenderStates(_transform);
        }
        return _render;
    }

    public void setTransform(Transform transform) {
        _transform = transform;
        _render = new RenderStates(_transform);
    }

    public void setRender(RenderStates render) {
        _render = render;
    }
}
