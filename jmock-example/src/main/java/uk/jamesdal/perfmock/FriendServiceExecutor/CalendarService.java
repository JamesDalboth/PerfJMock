package uk.jamesdal.perfmock.FriendServiceExecutor;

import java.time.LocalDate;

public interface CalendarService {
    void addBirthdayReminder(LocalDate nextBirthday, String name);
}
