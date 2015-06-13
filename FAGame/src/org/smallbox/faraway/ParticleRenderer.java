package org.smallbox.faraway;

/**
 * Created by Alex on 05/06/2015.
 */
public abstract class ParticleRenderer {
    public abstract void onDraw(GFXRenderer renderer, int x, int y);
    public abstract void init();
    public abstract void refresh();
    public abstract void setParticle(String particle);
}
