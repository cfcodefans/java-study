var System = java.lang.System;

var now = System.currentTimeMillis();
var then = now;

var result = 0.0;
for (var i = 0; i < 1000000; i++) {
	result += Math.sqrt(i);
}

now = System.currentTimeMillis();
print("after " + (now - then) + " ms " + result);