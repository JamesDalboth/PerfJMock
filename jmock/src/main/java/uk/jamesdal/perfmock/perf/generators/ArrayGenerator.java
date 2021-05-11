package uk.jamesdal.perfmock.perf.generators;

import org.apache.commons.math3.distribution.RealDistribution;

import java.lang.reflect.Array;

public class ArrayGenerator implements Generator<Object> {

    private final Generator<?> elemGenerator;
    private final Class<?> componentType;
    private final RealDistribution sizeDistr;

    public ArrayGenerator(Class<?> componentType, Generator<?> elemGenerator,
                          RealDistribution sizeDistr) {
        this.elemGenerator = elemGenerator;
        this.componentType = componentType;
        this.sizeDistr = sizeDistr;
    }

    @Override
    public Object generate() {
        int size = new IntegerGenerator(sizeDistr).generate();
        Object arr = Array.newInstance(componentType, size);
        for (int i = 0; i < size; i++) {
            Array.set(arr, i, elemGenerator.generate());
        }

        return arr;
    }

    public static Object create(Class<?> componentType,
                                Generator<?> elemGenerator,
                                RealDistribution sizeDistr) {
        ArrayGenerator arrayGenerator = new ArrayGenerator(
                componentType, elemGenerator, sizeDistr
        );
        return arrayGenerator.generate();
    }
}
