package org.smallbox.faraway.core;

import org.smallbox.faraway.core.game.modelInfo.ItemInfo;

/**
 * Created by Alex on 26/11/2016.
 */
public class GroovyManager {

    public interface GroovyBridge {
        void extend(ItemInfo map);
    }


    public void init() {
//        try {
//            Binding binding = new Binding();
//            binding.setProperty("bridge", new GroovyBridge() {
//                @Override
//                public void extend(ItemInfo map) {
//                    System.out.println(map);
//                }
//            });
//
////            Class scriptClass = new GroovyScriptEngine( "." ).loadScriptByName( "ReloadingTest.groovy" ) ;
////            Object scriptInstance = scriptClass.newInstance() ;
////            scriptClass.getDeclaredMethod( "sayHello", new Class[] {} ).invoke( scriptInstance, new Object[] {} ) ;
//
////            Class scriptClass = new GroovyClassLoader().parseClass( new File( "ReloadingTest.groovy" ) ) ;
////            Object scriptInstance = scriptClass.newInstance() ;
////            scriptClass.getDeclaredMethod( "sayHello", new Class[] {} ).invoke( scriptInstance, new Object[] {} ) ;
//
//            GroovyScriptEngine engine = new GroovyScriptEngine(".");
//            Script greeter = engine.createScript("ReloadingTest.groovy", binding);
//            greeter.evaluate("import org.smallbox.faraway.core.game.modelInfo.*");
//            greeter.run();
////            greeter.invokeMethod("sayHello", null);
////            greeter.sayHello();
////            greeter.getDeclaredMethod( "hello_world", new Class[] {} ).invoke( scriptInstance, new Object[] {} ) ;
////
//////            System.out.println(greeter.sayHello());
////            System.out.println(binding.getVariable("greet"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ResourceException e) {
//            e.printStackTrace();
//        } catch (ScriptException e) {
//            e.printStackTrace();
//        }
    }
}
