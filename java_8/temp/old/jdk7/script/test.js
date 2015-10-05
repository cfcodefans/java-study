

function foo(p) {
	var re = "foo(" + p +")";
	print(re);
	return re;
}

var bar = foo;

print(foo);
print(bar);

print("hello JavaScript");