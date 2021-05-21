package uk.jamesdal.perfmock.FriendServiceExecutor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class BirthdayReminder {

    private final FriendService friendService;
    private final CalendarService calendarService;

    public BirthdayReminder(FriendService friendService, CalendarService calendarService) {
        this.friendService = friendService;
        this.calendarService = calendarService;
    }

    public void remind(LocalDate date) {
        List<Profile> friendsProfiles = friendService.getFriendsProfilePictures();

        for (Profile profile : friendsProfiles) {
             LocalDate nextBirthday = profile.getNextBirthday();
             if (date.plus(1, ChronoUnit.MONTHS).isAfter(nextBirthday)) {
                 calendarService.addBirthdayReminder(nextBirthday, profile.name());
             }
        }
    }
}
