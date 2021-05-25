package uk.jamesdal.perfmock.perf.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface PerfTest {

    // Number of iterations to run
    int iterations() default 1;

    int warmups() default 1;

}