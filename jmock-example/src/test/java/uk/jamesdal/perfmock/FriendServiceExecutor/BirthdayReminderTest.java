package uk.jamesdal.perfmock.FriendServiceExecutor;

import org.junit.Rule;
import org.junit.Test;
import uk.jamesdal.perfmock.Expectations;
import uk.jamesdal.perfmock.integration.junit4.perf.PerfMockery;
import uk.jamesdal.perfmock.perf.PerfRule;
import uk.jamesdal.perfmock.perf.annotations.PerfComparator;
import uk.jamesdal.perfmock.perf.annotations.PerfMode;
import uk.jamesdal.perfmock.perf.annotations.PerfRequirement;
import uk.jamesdal.perfmock.perf.annotations.PerfTest;
import uk.jamesdal.perfmock.perf.generators.ConstGenerator;
import uk.jamesdal.perfmock.perf.generators.ListGenerator;
import uk.jamesdal.perfmock.perf.generators.LocalDateGenerator;
import uk.jamesdal.perfmock.perf.models.Normal;

import java.time.LocalDate;
import java.util.List;

public class BirthdayReminderTest {
    @Rule
    public PerfRule perfRule = new PerfRule();

    @Rule
    public PerfMockery ctx = new PerfMockery(perfRule);

    private final FriendService friendService = ctx.mock(FriendService.class);
    private final CalendarService calendarService = ctx.mock(CalendarService.class);

    private final LocalDate TODAY = LocalDate.parse("2000-01-01");
    private final LocalDate NEXT_YEAR = LocalDate.parse("2001-01-01");
    private final LocalDateGenerator dateGenerator = new LocalDateGenerator(TODAY, NEXT_YEAR);
    private static final String NAME = "NAME";

    private final Profile profile = ctx.mock(Profile.class);
    private final ListGenerator<Profile> profileListGenerator = new ListGenerator<>(
            new ConstGenerator<>(profile),
            new Normal(21.0, 5.0)
    );

    @Test
    @PerfTest(iterations = 2000, warmups = 100)
    @PerfRequirement(mode = PerfMode.MEAN, comparator = PerfComparator.LESS_THAN, value = 50000)
    public void remindTest() {
        BirthdayReminder birthdayReminder = new BirthdayReminder(friendService, calendarService);

        List<Profile> profiles = profileListGenerator.generate();

        ctx.checking(new Expectations() {{
            oneOf(friendService).getFriendsProfilePictures(); will(returnValue(profiles)); taking(FriendServiceImplTest.FIXED_THREAD_POOL_3_DIST);
            exactly(profiles.size()).of(profile).getNextBirthday(); will(returnValue(dateGenerator.generate())); taking(milli(100));
            atMost(profiles.size()).of(profile).name(); will(returnValue(NAME)); taking(milli(10));
            allowing(calendarService).addBirthdayReminder(with(any(LocalDate.class)), with(NAME)); taking(seconds(2));
        }});

        birthdayReminder.remind(TODAY);
    }
}
