package org.smallbox.faraway.game.model.item;

import static org.smallbox.faraway.game.model.item.ItemInfo.ItemInfoPlant.GrowingInfo;

public class ResourceModel extends ItemModel {
	private double 	        	_quantity;
	private int 	        	_tile;
	private int 	        	_totalQuantity;
    private double          	_growRate;
	private GrowingInfo 		_growState;

	public ResourceModel(ItemInfo info, int id) {
		super(info, id);

		if (_info.plant != null) {
			_totalQuantity = _info.plant.mature;
		}
    }

	public ResourceModel(ItemInfo info) {
		super(info);

		if (_info.plant != null) {
			_totalQuantity = _info.plant.mature;
		}
    }

	public void addQuantity(double quantity) {
		_quantity = Math.max(0, _quantity + quantity);
		_needRefresh = true;
	}

	public void	setValue(double quantity) {
		_quantity = quantity;
		_needRefresh = true;
	}
	
	public double 	getQuantity(int max) {return Math.min(_quantity, max);}
	public double 	getTotalQuantity() {return _totalQuantity;}
	public int 		getQuantity() { return (int)_quantity; }
	public double 	getRealQuantity() { return _quantity; }
	public int 		getTile() { return _tile; }
	public double 	getMaturity() { return _info.isPlant ? _quantity / _info.plant.mature : -1; }
	public double 	getGrowRate() { return _growRate; }
	public GrowingInfo getGrowState() { return _growState; }

	public boolean 	canBeMined() { return _info.isRock; }
	public boolean 	canBeHarvested() { return _info.isPlant; }
    public boolean 	isDepleted() { return _quantity < 1; }
	public boolean 	isMature() { return _quantity >= _totalQuantity; }
	public boolean 	isRock() { return _info.isRock; }
	public boolean isSolid() { return _info.isRock; }
    public boolean  isPlant() { return _info.isPlant; }

    public void 	setGrowRate(double growRate) { _growRate = growRate; }
	public void 	setGrowState(GrowingInfo growState) { _growState = growState; }
	public void 	setTile(int tile) { _tile = tile; }
	public void 	setQuantity(double quantity) { _quantity = quantity; }
}
