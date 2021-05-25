package uk.jamesdal.perfmock.WeatherMan;

import uk.jamesdal.perfmock.WeatherMan.impl.ApiImpl;
import uk.jamesdal.perfmock.WeatherMan.impl.DatabaseImpl;
import uk.jamesdal.perfmock.production.ProductionTest;

import java.time.LocalDate;
import java.util.Random;

public class WeatherProduction implements ProductionTest.Runner {

    private final Random random = new Random();

    private LocalDate date = LocalDate.parse("2050-02-12");
    private LocalDate date2 = LocalDate.parse("2050-02-13");

    @Override
    public void run() {
        LocalDate d;
        if (new Random().nextBoolean()) {
            d = date2;
        } else {
            d = date;
        }

        WeatherController weatherController = new WeatherController(new ApiImpl(), new DatabaseImpl());
        weatherController.predict(d);
    }
}
