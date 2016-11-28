package org.smallbox.faraway.client.renderer;

public class JobRenderer extends BaseRenderer {
    private int[][]         _areas;
    private int             _floor;

    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress) {
        //TODO
//        if (_areas == null) {
//            _areas = new int[Application.gameManager.getGame().getInfo().worldWidth][Application.gameManager.getGame().getInfo().worldHeight];
//        }
//
//        int offsetX = viewport.getPosX();
//        int offsetY = viewport.getPosY();
//        int floor = WorldHelper.getCurrentFloor();
//        ModuleHelper.getJobModule().getJobs().stream().filter(job -> !job.isFinish()).forEach(job ->
//                job.draw((x, y, z) -> {
//                    if (floor == z)
//                        renderer.draw(job.getIconDrawable(), offsetX + x * Constant.TILE_WIDTH, offsetY + y * Constant.TILE_HEIGHT);
//                }));
    }


    @Override
    public void onRefresh(int frame) {
    }

    @Override
    public void onFloorChange(int floor) {
        _floor = floor;
    }
}