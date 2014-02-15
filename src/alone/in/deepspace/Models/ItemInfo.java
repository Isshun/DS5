package alone.in.DeepSpace.Models;


public class ItemInfo {
	public BaseItem.Type	mType;
	public String			mName;
	public boolean			mSolid;
	public int				mWidth;
	public int				mHeight;
	public int				mMatter;
	public int				mPower;
	public int				mZone;

	public ItemInfo(BaseItem.Type structureRoom, String name, boolean solid, int width, int height, int matter, int power, int zone) {
		mType = structureRoom;
		mName = name;
		mSolid = solid;
		mWidth = width;
		mHeight = height;
		mMatter = matter;
		mPower = power;
		mZone = zone;
	}
}
