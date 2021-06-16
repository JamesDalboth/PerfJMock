package uk.jamesdal.perfmock.WeatherMan;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import uk.jamesdal.perfmock.Expectations;
import uk.jamesdal.perfmock.integration.junit4.perf.PerfMockery;
import uk.jamesdal.perfmock.perf.PerfRule;
import uk.jamesdal.perfmock.perf.annotations.PerfComparator;
import uk.jamesdal.perfmock.perf.annotations.PerfMode;
import uk.jamesdal.perfmock.perf.annotations.PerfRequirement;
import uk.jamesdal.perfmock.perf.annotations.PerfTest;
import uk.jamesdal.perfmock.perf.models.Normal;
import uk.jamesdal.perfmock.perf.postproc.PerfStatistics;
import uk.jamesdal.perfmock.perf.postproc.reportgenerators.ConsoleReportGenerator;

import java.time.LocalDate;
import java.util.Random;
import java.util.function.Supplier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertTrue;
import static uk.jamesdal.perfmock.perf.postproc.PerfStatistics.matchesDistr;

public class WeatherControllerTest {

    @Rule
    public PerfRule perfRule = new PerfRule(new ConsoleReportGenerator());

    @Rule
    public PerfMockery ctx = new PerfMockery(perfRule);

    private final WeatherDatabase weatherDatabase = ctx.mock(WeatherDatabase.class);
    private final WeatherApi weatherApi = ctx.mock(WeatherApi.class);

    private final WeatherInformation info = ctx.mock(WeatherInformation.class);

    private LocalDate date = LocalDate.parse("2050-02-12");
    private LocalDate date2 = LocalDate.parse("2050-02-13");

    @Test
    @PerfTest(iterations = 2000, warmups = 100)
    @PerfRequirement(mode = PerfMode.MEAN, comparator = PerfComparator.LESS_THAN, value = 30000)
    public void grabsFuturePrediction() {
        WeatherController ctlr = new WeatherController(weatherApi, weatherDatabase);

        ctx.checking(new Expectations() {{
            allowing(weatherDatabase); will(returnValue(null)); taking(seconds(new Uniform(5.0, 10.0)));
            allowing(weatherApi); will(returnValue(info)); taking(seconds(new Normal(5.0, 2.0)));
        }});

        ctlr.predict(date);
    }

    @Test
    public void grabsFuturePredictionRepeat() {
        WeatherController ctlr = new WeatherController(weatherApi, weatherDatabase);

        ctx.repeat(2000, 10, () -> {
            ctx.checking(new Expectations() {{
                allowing(weatherDatabase); will(returnValue(null)); taking(seconds(new Uniform(5.0, 10.0)));
                allowing(weatherApi); will(returnValue(info)); taking(seconds(new Normal(5.0, 2.0)));
            }});

            ctlr.predict(date);
        });

        assertThat(ctx.perfResults(),
                matchesDistr(new NormalDistribution(12500, 2500), 0.05)
        );
    }

    private Matcher<LocalDate> pastDate() {
        return new TypeSafeMatcher<LocalDate>() {
            @Override
            protected boolean matchesSafely(LocalDate localDate) {
                return localDate.isBefore(LocalDate.now());
            }

            @Override
            public void describeTo(Description description) {

            }
        };
    }

    private Matcher<LocalDate> futureDate() {
        return new TypeSafeMatcher<LocalDate>() {
            @Override
            protected boolean matchesSafely(LocalDate localDate) {
                return localDate.isAfter(LocalDate.now());
            }

            @Override
            public void describeTo(Description description) {}
        };
    }

    @Test
    public void grabsFuturePredictionFailing() {
        WeatherController ctlr = new WeatherController(weatherApi, weatherDatabase);

        ctx.repeat(2000, 10, () -> {
            ctx.checking(new Expectations() {{
                oneOf(weatherDatabase).getInfo(date); will(returnValue(null)); taking(seconds(5));
                oneOf(weatherApi).getInfo(date); will(returnValue(info)); taking(seconds(new Normal(5.0, 1.0)));
            }});

            ctlr.predict(date);
        });

        assertTrue(ctx.perfResults().meanMeasuredTime() < 5000);
    }

    @Test
    public void grabsFuturePredictionDistribution() {
        WeatherController ctlr = new WeatherController(weatherApi, weatherDatabase);

        Supplier<LocalDate> dateSupplier = () -> {
            if (new Random().nextBoolean()) {
                return date2;
            }
            return date;
        };

        ctx.repeat(2000, 100, () -> {
            ctx.checking(new Expectations() {{
                allowing(weatherApi).getInfo(with(futureDate())); will(returnValue(info)); taking(seconds(new Normal(5.0, 1.0)));
                allowing(weatherApi).getInfo(with(pastDate())); will(returnValue(info)); taking(seconds(new Normal(5.0, 1.0)));
                allowing(weatherDatabase).storeInfo(with(any(WeatherInformation.class))); taking(seconds(5));
                allowing(weatherDatabase).getInfo(with(pastDate())); will(returnValue(null)); taking(seconds(5));
                allowing(weatherDatabase).getInfo(with(futureDate())); will(returnValue(null)); taking(seconds(15));
            }});

            ctlr.predict(dateSupplier.get());
        });
    }
}