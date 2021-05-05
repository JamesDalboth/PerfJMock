package uk.jamesdal.perfmock.perf.annotations;

import uk.jamesdal.perfmock.perf.postproc.PerfRequirements;

import java.lang.annotation.*;

@Repeatable(PerfRequirements.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface PerfRequirement {
    PerfMode mode();

    PerfComparator comparator();

    double value();
}