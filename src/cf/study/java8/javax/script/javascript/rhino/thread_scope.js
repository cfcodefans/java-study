function g() {
	var x = 'local';
	return f();
}

java.lang.System.out.println(g());

function g2() {
	var x = 'local';
	return closure();
}

java.lang.System.out.println(g2());