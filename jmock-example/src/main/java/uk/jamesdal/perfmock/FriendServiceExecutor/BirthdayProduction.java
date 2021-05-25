package uk.jamesdal.perfmock.FriendServiceExecutor;

import uk.jamesdal.perfmock.FriendServiceExecutor.impl.ApiImpl;
import uk.jamesdal.perfmock.perf.generators.LocalDateGenerator;
import uk.jamesdal.perfmock.production.ProductionTest;
import uk.jamesdal.perfmock.production.SleepFailure;

import java.time.LocalDate;
import java.util.concurrent.Executors;

public class BirthdayProduction implements ProductionTest.Runner {

    private final LocalDate TODAY = LocalDate.parse("2000-01-01");
    private final LocalDate NEXT_YEAR = LocalDate.parse("2001-01-01");
    private final LocalDateGenerator dateGenerator = new LocalDateGenerator(TODAY, NEXT_YEAR);

    @Override
    public void run() {
        FriendService friendService = new FriendServiceImpl(new ApiImpl(dateGenerator), Executors.newFixedThreadPool(3));
        BirthdayReminder birthdayReminder = new BirthdayReminder(friendService, (nextBirthday, name) -> {try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new SleepFailure();
        }});
        birthdayReminder.remind(TODAY);
    }
}
