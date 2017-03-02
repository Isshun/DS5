//package org.smallbox.faraway.core.module.job.model;
//
//import org.smallbox.faraway.core.data.ItemInfo;
//import org.smallbox.faraway.client.drawable.AnimDrawable;
//import org.smallbox.faraway.client.drawable.IconDrawable;
//import org.smallbox.faraway.core.game.helper.WorldHelper;
//import org.smallbox.faraway.modules.character.model.CharacterTalentExtra;
//import org.smallbox.faraway.core.module.character.model.PathModel;
//import org.smallbox.faraway.modules.character.model.base.CharacterModel;
//import org.smallbox.faraway.core.module.job.model.abs.JobModel;
//import org.smallbox.faraway.core.module.path.PathManager;
//import org.smallbox.faraway.core.module.world.model.ParcelModel;
//import org.smallbox.faraway.core.module.world.model.resource.ResourceModel;
//import org.smallbox.faraway.core.engine.module.java.ModuleHelper;
//import org.smallbox.faraway.util.Log;
//import org.smallbox.faraway.util.Utils;
//
///**
// * Created by Alex on 15/10/2015.
// */
//public class CutJob extends JobModel {
//    private ResourceModel _resource;
//
//    private CutJob(ItemInfo.ItemInfoAction actionInfo, ParcelModel jobParcel) {
//        super(actionInfo, jobParcel, new IconDrawable("data/res/ic_cut.png", 0, 0, 32, 32), new AnimDrawable("data/res/actions.png", 0, 0, 32, 32, 8, 1));
//    }
//
//    public static JobModel createModules(ResourceModel res) {
//        // Resource is not cut-able
//        if (res == null) {
//            return null;
//        }
//
//        if (res.getInfo().actions != null) {
//            for (ItemInfo.ItemInfoAction action: res.getInfo().actions) {
//                if ("cut".equals(action.type)) {
//                    CutJob job = new CutJob(action, res.getParcel());
//                    job.setOnActionListener(j -> {
//                        if (j.getCharacter().getType().needs.joy != null) {
//                            j.getCharacter().getNeeds().addValue("entertainment", j.getCharacter().getType().needs.joy.change.work);
//                        }
//                    });
//                    job._resource = res;
//                    return job;
//                }
//            }
//        }
//
//        return null;
//    }
//
//    @Override
//    public boolean onCheck(CharacterModel character) {
//        Log.info("isJobLaunchable job: " + this);
//
//        // Item is null
//        if (_resource == null) {
//            _reason = JobAbortReason.ABORT;
//            return false;
//        }
//
//        if ((_parcel == null || !_parcel.isWalkable()) && getFreeParcel() == null) {
//            _reason = JobAbortReason.BLOCKED;
//            return false;
//        }
//
//        // Item is no longer exists
//        if (_resource != _resource.getParcel().getResource()) {
//            _reason = JobAbortReason.ABORT;
//            return false;
//        }
//
//        if (!Application.pathManager.hasPath(character.getParcel(), _resource.getParcel())) {
//            return false;
//        }
//
////        // Resource is depleted
////        if (_resource.getMatterSupply() <= 0) {
////            _reason = JobAbortReason.ABORT;
////            return false;
////        }
//
////        // No space left in inventory
////        if (!characters.hasInventorySpaceLeft()) {
////            _reason = JobAbortReason.NO_LEFT_CARRY;
////            return false;
////        }
//
//        return true;
//    }
//
//    private ParcelModel getFreeParcel() {
//        int x = _resource.getParcel().x;
//        int y = _resource.getParcel().y;
//        ParcelModel parcel = null;
//
//        // Corner
//        if (!WorldHelper.isBlocked(x - 1, y - 1)) parcel = WorldHelper.getParcel(x-1, y-1);
//        if (!WorldHelper.isBlocked(x + 1, y - 1)) parcel = WorldHelper.getParcel(x+1, y-1);
//        if (!WorldHelper.isBlocked(x-1, y+1)) parcel = WorldHelper.getParcel(x-1, y+1);
//        if (!WorldHelper.isBlocked(x+1, y+1)) parcel = WorldHelper.getParcel(x+1, y+1);
//
//        // Cross
//        if (!WorldHelper.isBlocked(x, y-1)) parcel = WorldHelper.getParcel(x, y-1);
//        if (!WorldHelper.isBlocked(x, y+1)) parcel = WorldHelper.getParcel(x, y+1);
//        if (!WorldHelper.isBlocked(x-1, y)) parcel = WorldHelper.getParcel(x-1, y);
//        if (!WorldHelper.isBlocked(x+1, y)) parcel = WorldHelper.getParcel(x+1, y);
//
//        _parcel = parcel;
//        if (parcel != null) {
//            _targetParcel = parcel;
//        }
//
//        return parcel;
//    }
//
//    @Override
//    protected void onStart(CharacterModel character) {
//        PathModel path = Application.pathManager.getBestApprox(character.getParcel(), _jobParcel);
//
//        if (path != null) {
//            _targetParcel = path.getLastParcel();
//            Log.info("best path to: " + _targetParcel.x + "x" + _targetParcel.y + " (" + character.getPersonals().getFirstName() + ")");
//            character.move(path);
//        }
//    }
//
//    @Override
//    protected void onFinish() {
//        Log.info("Cut complete");
//        ModuleHelper.getWorldModule().removeResource(_resource);
//
//        if (_actionInfo.products != null) {
//            _actionInfo.products.stream().filter(productInfo -> productInfo.rate > Math.random())
//                    .forEach(productInfo -> ModuleHelper.getWorldModule().putConsumable(_resource.getParcel(), productInfo.item, Utils.getRandom(productInfo.quantity)));
//        }
//    }
//
//    @Override
//    public JobActionReturn onAction(CharacterModel character) {
//        // Wrong call
//        if (_resource == null) {
//            Log.error("Character: action cut on null job or null job's item");
//            return JobActionReturn.ABORT;
//        }
//
//        if (!_resource.isResource()) {
//            Log.error("Character: action cut on non resource");
//            return JobActionReturn.ABORT;
//        }
//
//        if (!"cut".equals(_actionInfo.type)) {
//            Log.error("Character: action cut on non cut-able item");
//            return JobActionReturn.ABORT;
//        }
//
//        _progress += character.getTalents().get(CharacterTalentExtra.TalentType.CUT).work();
//        if (_progress < _cost) {
//            return JobActionReturn.CONTINUE;
//        }
//
//        // Remove a single unit
//        _progress = 0;
//
////        // Check if resource is depleted
////        if (!_resource.isDepleted()) {
////            return JobActionReturn.CONTINUE;
////        }
////
////        return JobActionReturn.COMPLETE;
//        return JobActionReturn.COMPLETE;
//    }
//
//    @Override
//    public String getLabel() {
//        return "Cut " + _resource.getLabel();
//    }
//
//    @Override
//    public ParcelModel getTargetParcel() {
//        return _resource.getParcel();
//    }
//
//    @Override
//    public CharacterTalentExtra.TalentType getTalentNeeded() {
//        return CharacterTalentExtra.TalentType.CUT;
//    }
//
//    @Override
//    public void draw(onDrawCallback callback) {
//        callback.onDraw(_resource.getParcel().x, _resource.getParcel().y);
//    }
//}
