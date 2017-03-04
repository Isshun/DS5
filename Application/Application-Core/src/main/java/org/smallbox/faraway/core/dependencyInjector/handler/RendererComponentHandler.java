//package org.smallbox.faraway.core.dependencyInjector.handler;
//
//import org.smallbox.faraway.client.renderer.BaseRenderer;
//import org.smallbox.faraway.core.Application;
//import org.smallbox.faraway.util.Log;
//
///**
// * Created by Alex on 13/01/2017.
// */
//
//@ComponentHandlerAnnotation(type = BaseRenderer.class)
//public class RendererComponentHandler extends ComponentHandler<BaseRenderer> {
//    @Override
//    public void invoke(Class<BaseRenderer> cls) {
//        try {
//            BaseRenderer renderer = (BaseRenderer) cls.newInstance();
//            Application.dependencyInjector.register(renderer);
////            ApplicationClient.mainRenderer.add(renderer);
//        } catch ( IllegalAccessException | InstantiationException e) {
//            throw new GameException(e);
//        }
//    }
//}
