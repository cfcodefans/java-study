//importClass(Packages.java.lang);

function print(obj) {
	java.lang.System.out.println(obj);
}

print(this);

defineClass("Foo");

var foo = new Foo();
print(foo);
print(foo.counter);
print(foo.counter);
print(foo.counter);
print(foo.counter);
foo.resetCounter();
print(foo.counter);
print(foo.counter);

var bar = new Foo(37);
print(bar.counter);

print(foo.varargs(3, "hi"));

foo[7] = 43;
print(foo[7]);

foo.a = 23;
print(foo.a);

print(foo.a + foo[7]);