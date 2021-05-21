package uk.jamesdal.perfmock.FriendServiceExecutor;

import java.time.LocalDate;

public interface Profile {
    LocalDate getNextBirthday();

    String name();
}
