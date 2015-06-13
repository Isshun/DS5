package org.smallbox.faraway.model;

public class Position {
	Position()
	{
	    set(0.f, 0.f, 0.f);
	}

	public Position(float x, float y)
	{
	    set(x, y, 0.f);
	}

	Position(float x, float y, float z)
	{
	    set(x, y, z);
	}

	void set(float x, float y)
	{
	    this.x = x;
	    this.y = y;
	}

	void set(float x, float y, float z)
	{
	    this.x = x;
	    this.y = y;
	    this.z = z;
	}

	float getDistanceTo(Position p)
	{
	    return (float) Math.sqrt(Math.pow((x - p.x), 2) + Math.pow((y - p.y), 2));
	}

	int getGridX()
	{
	    return (int) Math.floor((x + 32.f) / 64.f);
	}

	int getGridY()
	{
	    return (int) Math.floor((y + 32.f) / 64.f);
	}

	public float x;
    public float y;
    float z;

    int f;
    int g;
    int h;
}
