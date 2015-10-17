//package org.smallbox.faraway.game.model.job;
//
//import org.smallbox.faraway.core.drawable.AnimDrawable;
//import org.smallbox.faraway.core.drawable.IconDrawable;
//import org.smallbox.faraway.game.helper.WorldHelper;
//import org.smallbox.faraway.game.model.GameData;
//import org.smallbox.faraway.game.model.MovableModel;
//import org.smallbox.faraway.game.model.OldReceiptModel;
//import org.smallbox.faraway.game.model.character.base.CharacterModel;
//import org.smallbox.faraway.game.model.item.MapObjectModel;
//import org.smallbox.faraway.game.model.item.ParcelModel;
//import org.smallbox.faraway.game.module.ModuleHelper;
//import org.smallbox.faraway.util.Log;
//import org.smallbox.faraway.util.MoveListener;
//
//import java.util.ArrayList;
//
//public class JobBuild extends BaseBuildJobModel {
//
//	private JobBuild(int x, int y) {
//		super(null, x, y, new IconDrawable("data/res/ic_build.png", 0, 0, 32, 32), new AnimDrawable("data/res/actions.png", 0, 64, 32, 32, 7, 10));
//	}
//
//    @Override
//    protected void onStart(CharacterModel character) {
//        int bestDistance = Integer.MAX_VALUE;
//        for (OldReceiptModel receipt: _receipts) {
//            receipt.reset();
//            if (bestDistance > receipt.getTotalDistance() && receipt.hasComponentsOnMap()) {
//                bestDistance = receipt.getTotalDistance();
//                _receipt = receipt;
//            }
//        }
//
//        if (_receipt == null) {
//            throw new RuntimeException("Try to start JobCraft but no receipt have enough component");
//        }
//
//        // Start receipt and get first component
//        _receipt.start(this);
//		if (_receipt.getNextInput() != null) {
//			moveToIngredient(character, _receipt.getNextInput());
//		} else {
//			moveToMainItem();
//		}
//    }
//
//    @Override
//    public void onQuit(CharacterModel character) {
//        if (_receipt != null) {
//            _receipt.close();
//            _receipt = null;
//        }
//    }
//
//    @Override
//    public CharacterModel.TalentType getTalentNeeded() {
//        return CharacterModel.TalentType.BUILD;
//    }
//
//	public static BaseJobModel create(MapObjectModel item) {
//        if (item == null) {
//            throw new RuntimeException("Cannot add Craft job (item is null)");
//        }
//
//        JobBuild job = new JobBuild(item.getX(), item.getY());
//        job._mainItem = item;
//        job._mainItem.addJob(job);
//        job._mainItem.setJobBuild(job);
//        job._receipts = new ArrayList<>();
//        job._receipts.add(OldReceiptModel.createFromComponentInfo(item, item.getInfo().components));
//		job.setCost(item.getInfo().cost);
//		job.setStrategy(j -> {
//            if (j.getCharacter().getType().needs.joy != null) {
//                j.getCharacter().getNeeds().joy += j.getCharacter().getType().needs.joy.change.work;
//            }
//        });
//        job.onCheck(null);
//
//		return job;
//	}
//
//	@Override
//	public boolean onCheck(CharacterModel character) {
//        for (OldReceiptModel receipt: _receipts) {
//            if (receipt.hasComponentsOnMap()) {
//                _message = "Waiting";
//                return true;
//            }
//        }
//        _message = "Missing components";
//        return false;
//	}
//
//	@Override
//	protected void onFinish() {
//		Log.info("Character #" + _character.getId() + ": build close");
//        _mainItem.removeJob(this);
//        _mainItem.setJobBuild(null);
//	}
//
//	@Override
//	public JobActionReturn onAction(CharacterModel character) {
//		if (_character == null) {
//			Log.error("Action on job with null characters");
//		}
//
//		// Wrong call
//		if (_mainItem == null) {
//			Log.error("Character: actionBuild on null job or null job's item");
//			ModuleHelper.getJobModule().quitJob(this, JobAbortReason.INVALID);
//			return JobActionReturn.ABORT;
//		}
//
//		// Item is no longer exists
//		if (_mainItem != WorldHelper.getStructure(_posX, _posY) && _mainItem != WorldHelper.getItem(_posX, _posY)) {
//            Log.warning("Character #" + character.getId() + ": actionBuild on invalid mapObject");
//			ModuleHelper.getJobModule().quitJob(this, JobAbortReason.INVALID);
//			return JobActionReturn.ABORT;
//		}
//
//		// Move to ingredient
//		if (_status == Status.WAITING) {
//			moveToIngredient(_character, _receipt.getNextInput());
//			return JobActionReturn.CONTINUE;
//		}
//
//		// Build
//		CharacterModel.TalentEntry talent = character.getTalent(CharacterModel.TalentType.BUILD);
//		_mainItem.addProgress(talent.work());
//		if (!_mainItem.isComplete()) {
//			Log.debug("Character #" + character.getId() + ": build progress");
//			return JobActionReturn.CONTINUE;
//		}
//
//		return JobActionReturn.FINISH;
//	}
//
//	protected void moveToIngredient(CharacterModel character, OldReceiptModel.OrderModel order) {
//		ParcelModel parcel = order.consumable.getParcel();
//		_posX = parcel.x;
//		_posY = parcel.y;
//		_status = Status.MOVE_TO_INGREDIENT;
//		character.moveTo(this, parcel, new MoveListener() {
//			@Override
//			public void onReach(BaseJobModel job, MovableModel movable) {
//				order.consumable.lock(null);
//				order.status = OldReceiptModel.OrderModel.Status.CARRY;
//				character.addInventory(order.consumable, order.quantity);
//				if (order.consumable.getQuantity() == 0) {
//					ModuleHelper.getWorldModule().removeConsumable(order.consumable);
//				}
//
//				// Get next consumable (same ingredient)
//				if (_receipt.getNextOrder() != null && _receipt.getNextOrder().consumable.getInfo() == order.consumable.getInfo()
//						&& _receipt.getNextOrder().quantity + _character.getInventory().getQuantity() <= GameData.config.inventoryMaxQuantity) {
//					_receipt.nextOrder();
//					moveToIngredient(character, _receipt.getNextInput());
//				} else {
//					moveToMainItem();
//				}
//
//				_message = "Carry " + order.consumable.getInfo().label + " to " + _mainItem.getInfo().label;
//			}
//
//			@Override
//			public void onFail(BaseJobModel job, MovableModel movable) {
//			}
//
//			@Override
//			public void onSuccess(BaseJobModel job, MovableModel movable) {
//			}
//		});
//		_message = "Move to " + order.consumable.getInfo().label;
//	}
//
//	protected void moveToMainItem() {
//		_status = Status.MOVE_TO_FACTORY;
//		_posX = _mainItem.getX();
//		_posY = _mainItem.getY();
//
//		// Store component in factory
//		_character.moveTo(this, _mainItem.getParcel(), new MoveListener<CharacterModel>() {
//			@Override
//			public void onReach(BaseJobModel job, CharacterModel character) {
//				if (_receipt != null) {
//					if (character.getInventory() != null && _receipt.getNextInput() != null && character.getInventory().getInfo() == _receipt.getNextInput().consumable.getInfo()) {
//						_receipt.closeCarryingOrders();
//						_receipt.nextOrder();
//						_mainItem.addComponent(character.getInventory());
//						character.setInventory(null);
//					}
//					_status = _receipt.isComplete() ? Status.MAIN_ACTION : Status.WAITING;
//				}
//			}
//
//			@Override
//			public void onFail(BaseJobModel job, CharacterModel character) {
//			}
//
//			@Override
//			public void onSuccess(BaseJobModel job, CharacterModel character) {
//			}
//		});
//	}
//
//    @Override
//    public double getProgress() { return (double)_mainItem.getProgress() / _mainItem.getInfo().cost; }
//
//	@Override
//	public boolean canBeResume() {
//		return false;
//	}
//
//	@Override
//	public String getLabel() {
//		return "build " + _mainItem.getLabel();
//	}
//
//	@Override
//	public String getShortLabel() {
//		return "build " + _mainItem.getLabel();
//	}
//}
