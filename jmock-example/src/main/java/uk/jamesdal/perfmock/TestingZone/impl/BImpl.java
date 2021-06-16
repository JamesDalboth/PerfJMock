package uk.jamesdal.perfmock.TestingZone.impl;

import uk.jamesdal.perfmock.TestingZone.B;
import uk.jamesdal.perfmock.TestingZone.MockObject;

public class BImpl implements B {

    private final MockObject object;

    public BImpl(MockObject object) {
        this.object = object;
    }

    @Override
    public void run() {
        object.run();
    }

    @Override
    public void doSomething() {

    }

    @Override
    public void doSomethingElse() {

    }
}
