//package org.smallbox.faraway.engine.lua;
//
//import org.luaj.vm2.*;
//import org.luaj.vm2.compiler.LuaC;
//import org.luaj.vm2.lib.globals.CoerceJavaToLua;
//import org.luaj.vm2.lib.globals.JsePlatform;
//import org.smallbox.faraway.game.Game;
//
///**
// * Created by Alex on 19/06/2015.
// */
//public class LuaBridge {
//    class Cat
//    {
//        String name;
//        Cat(String n) { name = n; }
//        public void talk() { System.out.println("Cat " + name + " meows!"); }
//        public void walk() { System.out.println("Cat " + name + " walks..."); }
//    }
//
//    public LuaBridge() {
//        //run the lua script defining your function
//
////        Globals globals = JsePlatform.standardGlobals();
////        LuaValue chunk = globals.loadfile("data/quests/refugees.lua");
////        chunk.get("onTalk");
//
////        LuaFunction function = (LuaFunction)chunk.get("onTalk");
//        //chunk.call();
//
////        Cat cat = new Cat("Felix");
////
////        LuaFunction onTalk = (LuaFunction) chunk.get("onTalk"); // Get Lua function
////        LuaValue b = onTalk.call(luaDog); // Call the function
////        System.out.println("onTalk answered: " + b);
////        LuaFunction onWalk = (LuaFunction) chunk.get("onWalk");
////        LuaValue[] dogs = { luaDog };
////        Varargs dist = onWalk.invoke(LuaValue.varargsOf(dogs)); // Alternative
////        System.out.println("onWalk returned: " + dist);
//    }
//}
