package io.covidtracker.coronavirustracker.services;

import io.covidtracker.coronavirustracker.models.LocationStats;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thymeleaf.model.IStandaloneElementTag;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;


@Service
public class CoronaVirusDataService {

    private String COVID_DATA = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";

    private List<LocationStats> stats = new ArrayList<>();

    public List<LocationStats> getStats() {
        return stats;
    }

    @PostConstruct
    @Scheduled(cron = "* * 1 * * *")
    public void fetchData() throws IOException, InterruptedException {
        List<LocationStats> newStats = new ArrayList<>();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(COVID_DATA)).build();
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

        StringReader reader = new StringReader(httpResponse.body());
        Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(reader);

        for (CSVRecord record : records) {
            LocationStats locationStat = new LocationStats();
            locationStat.setState(record.get("Province/State"));
            locationStat.setCountry(record.get("Country/Region"));
            int latest = Integer.parseInt(record.get(record.size()-1));
            int previous = Integer.parseInt(record.get(record.size()-2));
            locationStat.setReportedCases(latest);
            locationStat.setDiff(latest-previous);
            newStats.add(locationStat);
        }
        this.stats = newStats;
    }
}
