package me.blahberrys.meteorloot.meteor;

import me.blahberrys.meteorloot.Settings;

public class MeteorShape {

	public final Shape shape;
	public final String shapeName;

	public MeteorShape() {
		this.shapeName = generateShapeName();
		this.shape = generateShape(this.shapeName);
	}

	public Shape getShape() {
		return this.shape;
	}

	public String getShapeName() {
		return this.shapeName;
	}

	public enum Shape {
		SPHERE, CUBE, SCHEMATIC;

		public Shape shape;

		public Shape getShape() {
			return this.shape;
		}
	}

	private String generateShapeName() {
		String shape = Settings.getInstance().activeCycle.get(0);
		if (Settings.getInstance().shapeCycle.size() > 0)
			Settings.getInstance().activeCycle.remove(0);
		if (Settings.getInstance().activeCycle.isEmpty())
			Settings.getInstance().registerShapeCycle();
		return shape;
	}

	private Shape generateShape(String shape) {
		switch (shape) {
		case "SPHERE":
			return Shape.SPHERE;
		case "CUBE":
			return Shape.CUBE;
		default:
			return Shape.SCHEMATIC;
		}
	}
}