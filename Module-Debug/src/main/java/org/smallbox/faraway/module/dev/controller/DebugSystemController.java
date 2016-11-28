package org.smallbox.faraway.module.dev.controller;

import org.hyperic.sigar.*;
import org.smallbox.faraway.core.lua.BindLua;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.module.character.controller.LuaController;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;

/**
 * Created by Alex on 15/08/2016.
 */
public class DebugSystemController extends LuaController {
    @BindLua
    private UILabel lbHeap;

    @BindLua
    private View gaugeHeapContent;

    private static String getStateString(char state) {
        switch (state) {
            case ProcState.SLEEP:
                return "Sleeping";
            case ProcState.RUN:
                return "Running";
            case ProcState.STOP:
                return "Suspended";
            case ProcState.ZOMBIE:
                return "Zombie";
            case ProcState.IDLE:
                return "Idle";
            default:
                return String.valueOf(state);
        }
    }

    public void printProcessList() {
        SigarProxy sp = Humidor.getInstance().getSigar();
        try
        {
            long[] pidList = sp.getProcList();
            for(int i=0; i<pidList.length; i++)
            {
                ProcExe temp = sp.getProcExe(pidList[i]);
                String exeName = temp.getName();
                System.out.println(pidList[i] + " " + exeName);
            }
        } catch (SigarException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onGameUpdate(Game game) {
        Runtime runtime = Runtime.getRuntime();
        int mb = 1024 * 1024;
        long total = runtime.totalMemory();
        long free = runtime.freeMemory();
        long used = total - free;
        lbHeap.setText(String.format("Heap: %d / %d MB", used / mb, total / mb));
        gaugeHeapContent.setSize((int)(used * 158 / total), 16);

//        ManagementFactory.getMemoryPoolMXBeans().stream()
//                .filter(mpBean -> mpBean.getType() == MemoryType.HEAP)
//                .forEach(mpBean -> System.out.printf("Name: %s: %s\n", mpBean.getName(), mpBean.getUsage()));

//        try {
//            Sigar sigar = new Sigar();
////            ProcessFinder find = new ProcessFinder(sigar);
////            long pid = find.findSingleProcess("State.Name.ct=java");
////            long[] pids = find.find("State.Name.ct=java");
//
//            if (SystemUtils.IS_OS_WINDOWS) {
//                int pid = Kernel32.INSTANCE.GetCurrentProcessId();
////                ProcMem memory = new ProcMem();
////                memory.gather(sigar, pid);
////                System.out.println(Long.toString(memory.getSize()));
//                ProcState procState = sigar.getProcState(pid);
////                procCpu.gather(sigar, pid);
////                System.out.println(String.valueOf(procCpu.getPercent()));
////                procState.
//                System.out.println(procState.getName() + ": " + getStateString(procState.getState()));
//            }
//            sigar.close();
//        } catch (SigarException e) {
//            e.printStackTrace();
//        }
    }
}
