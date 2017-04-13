package cf.study.java.lang.annotation;

@TypeAnnotation(typeName = Shape.TYPE_NAME, id = 0)
class Shape {
	public static final int TYPE_ID = Shape.class.hashCode();
	public static final String TYPE_NAME = "Shape";

	@MethodAt(caller = TYPE_NAME)
	public Shape draw() {
		return this;
	}
}

class Rectangle extends Shape {
	public static final String TYPE_NAME = "Rectangle";
	
	@FieldAt(definedBy=TYPE_NAME)
	public Point2D start;

	@MethodAt(caller = TYPE_NAME)
	public Shape draw() {
		return this;
	}
	
	@MethodAt(caller = TYPE_NAME)
	public Shape draw(@ParamAt(definedBy=TYPE_NAME) IDrawable canvas) {
		canvas.doDraw(this);
		return this;
	}

	public Rectangle(Point2D start) {
		super();
		this.start = start;
	}
	
	public Rectangle() {
		start = new Point2D();
	}
}

interface IDrawable {
	IDrawable doDraw(Shape shape);
}

@TypeAnnotation(typeName = Point.TYPE_NAME, id = 1)
class Point extends Shape {
	public static final int TYPE_ID = Point.class.hashCode();
	public static final String TYPE_NAME = "Point";

	@MethodAt(caller = TYPE_NAME)
	public Point draw() {
		return this;
	}
	
}

class Point2D extends Point {
	public static final int TYPE_ID = Point2D.class.hashCode();
	public static final String TYPE_NAME = "Point2D";
	
	public int x = 0, y = 0;
	
	public Point2D(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}
	
	public Point2D() {}
}
