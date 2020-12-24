package org.smallbox.faraway.core.groovy;

import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;

/**
 * Created by Alex on 26/11/2016.
 */
@ApplicationObject
public class GroovyManager {

    public interface GroovyBridge {
        void extend(ItemInfo map);
    }


    public void init() {
//
//        InputStream groovyClassIS = GroovyManager.class
//                .getResourceAsStream("/org/jboss/loom/tools/groovy/Foo.groovy");
//
//        GroovyClassLoader gcl = new GroovyClassLoader();
//        Class clazz = gcl.parseClass(groovyClassIS, "SomeClassName.groovy");
//        Object obj = null;
//        try {
//            obj = clazz.newInstance();
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
//        IConfig action = (IConfig) obj;
//        System.out.println( action.foo());

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
