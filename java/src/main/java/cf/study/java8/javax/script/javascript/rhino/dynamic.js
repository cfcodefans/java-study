var x = 'sharedScope';
function f() {
	return x;
}

// Dynamic scope works with nested function too
function initClosure(prefix) {
	return function test() {return prefix + x;};
}

var closure = initClosure('nested: ');