//TODO implement alternative mod if this doesn't upgrade?
///*
//package net.shadowmage.ancientwarfare.core.interop;
//
//import com.draco18s.wildlife.entity.ai.EntityAIMilking;
//
//import net.minecraft.entity.ai.EntityAITasks;
//import net.minecraft.entity.passive.EntityAnimal;
//import net.minecraft.entity.passive.EntityCow;
//
//public class InteropHarderWildlife extends InteropHarderWildlifeDummy {
//
//    @Override
//    public boolean getMilkable(EntityAnimal animal) {
//        if (animal instanceof EntityCow) {
//            EntityAIMilking milkingTask = getMilkingTask((EntityCow) animal);
//            if (milkingTask != null) {
//                return milkingTask.getMilkable();
//            }
//        }
//        return false;
//    }
//
//    @Override
//    public void doMilking(EntityAnimal animal) {
//        if (animal instanceof EntityCow) {
//            EntityAIMilking milkingTask = getMilkingTask((EntityCow) animal);
//            if (milkingTask != null) {
//                milkingTask.doMilking();
//            }
//        }
//    }
//
//    private EntityAIMilking getMilkingTask(EntityCow theCow) {
//        EntityAIMilking milkingTask = null;
//        for(Object obj : theCow.tasks.taskEntries) {
//            EntityAITasks.EntityAITaskEntry task = (EntityAITasks.EntityAITaskEntry) obj;
//            if(task.action instanceof EntityAIMilking) {
//                milkingTask = (EntityAIMilking) task.action;
//            }
//        }
//        return milkingTask;
//    }
//}
//*/
