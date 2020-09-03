package io.covidtracker.coronavirustracker.controllers;

import io.covidtracker.coronavirustracker.models.LocationStats;
import io.covidtracker.coronavirustracker.services.CoronaVirusDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    CoronaVirusDataService coronaVirusDataService;

    @GetMapping("/")
    public String home(Model model) {
        List<LocationStats> stats = coronaVirusDataService.getStats();
        int total = stats.stream().mapToInt(LocationStats::getReportedCases).sum();
        model.addAttribute("locationStats", stats);
        model.addAttribute("totalReportedCases", total);
        return "home";
    }

}
