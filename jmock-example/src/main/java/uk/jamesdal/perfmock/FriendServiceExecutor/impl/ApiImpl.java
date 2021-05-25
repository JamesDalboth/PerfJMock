package uk.jamesdal.perfmock.FriendServiceExecutor.impl;

import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import uk.jamesdal.perfmock.FriendServiceExecutor.FriendApi;
import uk.jamesdal.perfmock.FriendServiceExecutor.Profile;
import uk.jamesdal.perfmock.perf.generators.IntegerGenerator;
import uk.jamesdal.perfmock.perf.generators.ListGenerator;
import uk.jamesdal.perfmock.perf.generators.LocalDateGenerator;
import uk.jamesdal.perfmock.perf.models.Normal;
import uk.jamesdal.perfmock.production.SleepFailure;

import java.time.LocalDate;
import java.util.List;

public class ApiImpl implements FriendApi {

    private LocalDateGenerator generator;

    public ApiImpl() {}

    public ApiImpl(LocalDateGenerator generator) {
        this.generator = generator;
    }

    private final ListGenerator<Integer> listGenerator = new ListGenerator<>(
            new IntegerGenerator(new UniformIntegerDistribution(0, 100)),
            new Normal(21.0, 5.0)
    );

    @Override
    public List<Integer> getFriends() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new SleepFailure();
        }
        return listGenerator.generate();
    }

    @Override
    public Profile getProfilePic(Integer id) {
        try {
            double mean = id < 50 ? 1 : 9;
            long sleep = (long) new Normal(mean, 0.5).sample();
            if (sleep > 0) Thread.sleep(sleep * 1000);
        } catch (InterruptedException e) {
            throw new SleepFailure();
        }
        return new Profile() {
            @Override
            public LocalDate getNextBirthday() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new SleepFailure();
                }
                return generator.generate();
            }

            @Override
            public String name() {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new SleepFailure();
                }
                return "Billy Bob";
            }
        };
    }
}
