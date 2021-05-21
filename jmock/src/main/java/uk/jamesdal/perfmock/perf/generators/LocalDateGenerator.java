package uk.jamesdal.perfmock.perf.generators;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Random;

public class LocalDateGenerator implements Generator<LocalDate> {
    private final LocalDate lower;
    private final LocalDate upper;
    private final Random random;

    public LocalDateGenerator(LocalDate lower, LocalDate upper) {
        this.lower = lower;
        this.upper = upper;
        this.random = new Random();
    }

    @Override
    public LocalDate generate() {
        double a = random.nextDouble();
        long daysBetween = ChronoUnit.DAYS.between(lower, upper);
        long daysAfterLower = Math.round(daysBetween * a);
        return lower.plus(daysAfterLower, ChronoUnit.DAYS);
    }
}
