package uk.jamesdal.perfmock.perf.postproc;

import uk.jamesdal.perfmock.perf.PerfRequirement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface PerfRequirements {
    PerfRequirement[] value();
}
