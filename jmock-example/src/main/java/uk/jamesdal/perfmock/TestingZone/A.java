package uk.jamesdal.perfmock.TestingZone;

public class A {
    public void run(B b, C c) {
        b.doSomething();
        if (c.condition()) {
            expensiveOperation();
        }
    }

    private void expensiveOperation() {

    }
}
