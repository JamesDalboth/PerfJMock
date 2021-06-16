package uk.jamesdal.perfmock.perf.generators;

import java.util.Random;

public class WeightedChoiceGenerator<V> implements Generator<V> {

    private final V[] choices;
    private final double[] weights;

    private final Random random = new Random();

    public WeightedChoiceGenerator(V[] choices, double[] weights) {
        this.choices = choices;
        this.weights = weights;
    }

    @Override
    public V generate() {
        double weightTotal = 0.0;
        for (double weight : weights) {
            weightTotal += weight;
        }

        double x = random.nextDouble() * weightTotal;

        double count = 0;

        for (int i = 0; i < weights.length; i++) {
            count += weights[i];
            if (count > x) {
                return choices[i];
            }
        }

        return null;
    }
}
