package org.smallbox.faraway.engine.lua;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

public class test extends TwoArgFunction {

//    @Override
//    public LuaValue call() {
////        LuaValue library = tableOf();
////        library.set( "sinh", new sinh() );
////        library.set( "cosh", new cosh() );
////        env.set( "launchRandomQuest", library );
////        return library;
//        return valueOf("hello from java");
//    }

    public test() {}

    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaValue library = tableOf();
        library.set( "sinh", new sinh() );
        library.set( "cosh", new cosh() );
        env.set( "launchRandomQuest", library );
        return library;
    }

    static class hello extends ZeroArgFunction {
        public LuaValue call() {
            return LuaValue.valueOf("hoho");
        }
    }

    static class sinh extends OneArgFunction {
        public LuaValue call(LuaValue x) {
            return LuaValue.valueOf(Math.sinh(x.checkdouble()));
        }
    }

    static class cosh extends OneArgFunction {
        public LuaValue call(LuaValue x) {
            return LuaValue.valueOf(Math.cosh(x.checkdouble()));
        }
    }
}