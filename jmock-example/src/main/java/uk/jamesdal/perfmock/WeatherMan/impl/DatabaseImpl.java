package uk.jamesdal.perfmock.WeatherMan.impl;

import uk.jamesdal.perfmock.WeatherMan.WeatherDatabase;
import uk.jamesdal.perfmock.WeatherMan.WeatherInformation;
import uk.jamesdal.perfmock.perf.models.Normal;
import uk.jamesdal.perfmock.perf.models.Uniform;
import uk.jamesdal.perfmock.production.SleepFailure;

import java.time.LocalDate;

public class DatabaseImpl implements WeatherDatabase {
    @Override
    public WeatherInformation getInfo(LocalDate date) {
        try {
            Thread.sleep((long) (1000 * new Uniform(5.0, 10.0).sample()));
        } catch (InterruptedException e) {
            throw new SleepFailure();
        }
        return null;
    }

    @Override
    public void storeInfo(WeatherInformation info) {
        try {
            Thread.sleep((long) (1000 * new Uniform(5.0, 10.0).sample()));
        } catch (InterruptedException e) {
            throw new SleepFailure();
        }
    }
}
