package uk.jamesdal.perfmock.WeatherMan.impl;

import uk.jamesdal.perfmock.WeatherMan.WeatherDatabase;
import uk.jamesdal.perfmock.WeatherMan.WeatherInformation;
import uk.jamesdal.perfmock.perf.models.Normal;
import uk.jamesdal.perfmock.production.SleepFailure;

import java.time.LocalDate;

public class DatabaseImpl implements WeatherDatabase {
    @Override
    public WeatherInformation getInfo(LocalDate date) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new SleepFailure();
        }
        if (date.isAfter(LocalDate.now())) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                throw new SleepFailure();
            }
        }
        return null;
    }

    @Override
    public void storeInfo(WeatherInformation info) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new SleepFailure();
        }
    }
}
