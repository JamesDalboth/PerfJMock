package uk.jamesdal.perfmock.WeatherMan.impl;

import uk.jamesdal.perfmock.WeatherMan.WeatherApi;
import uk.jamesdal.perfmock.WeatherMan.WeatherInformation;
import uk.jamesdal.perfmock.perf.models.Normal;
import uk.jamesdal.perfmock.production.SleepFailure;

import java.time.LocalDate;

public class ApiImpl implements WeatherApi {
    @Override
    public WeatherInformation getInfo(LocalDate date) {
        try {
            Thread.sleep((long) (1000 * new Normal(5.0, 1.0).sample()));
        } catch (InterruptedException e) {
            throw new SleepFailure();
        }
        return new WeatherInformation() {};
    }
}
