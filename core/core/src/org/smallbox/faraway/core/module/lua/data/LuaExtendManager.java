//package org.smallbox.faraway.core.module.lua.data;
//
//import org.smallbox.faraway.core.module.lua.data.extend.*;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
///**
// * Created by Alex on 29/09/2015.
// */
//public class LuaExtendManager {
//    private static final List<LuaExtend> EXTENDS = Arrays.asList(
//            new LuaUIExtend(),
//            new LuaItemExtend(),
//            new LuaPlanetExtend(),
//            new LuaReceiptExtend(),
//            new LuaCursorExtend(),
//            new LuaCharacterBuffExtend(),
//            new LuaCharacterDiseaseExtend(),
//            new LuaLangExtend());
//
//    private static LuaExtendManager _self;
//
//    public List<LuaExtend> getExtends() {
//        return EXTENDS;
//    }
//
//    public static LuaExtendManager getInstance() {
//        if (_self == null) {
//            _self = new LuaExtendManager();
//        }
//        return _self;
//    }
//}
