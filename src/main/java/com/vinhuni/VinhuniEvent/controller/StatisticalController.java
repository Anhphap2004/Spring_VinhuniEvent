package com.vinhuni.VinhuniEvent.controller;

import java.time.LocalDateTime;

import com.vinhuni.VinhuniEvent.config.RequiredRole;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.vinhuni.VinhuniEvent.model.ThongKeViewModel;
import com.vinhuni.VinhuniEvent.repository.StatisticalRepository;
@RequiredRole(1)
@Controller
public class StatisticalController {

    private final StatisticalRepository statisticalRepository;

    public StatisticalController(StatisticalRepository statisticalRepository) {
        this.statisticalRepository = statisticalRepository;
    }

    @GetMapping("/admin/statistical")
    public String index(Model model) {

        LocalDateTime now = LocalDateTime.now();

        ThongKeViewModel vm = new ThongKeViewModel();

        vm.setTotalEvents(statisticalRepository.countTotalEvents());
        vm.setTotalRegistrations(statisticalRepository.countTotalRegistrations());
        vm.setTotalUsers(statisticalRepository.countTotalUsers());

        vm.setTotalAdmins(statisticalRepository.countAdmins());
        vm.setTotalStudents(statisticalRepository.countStudents());
        vm.setTotalOrganizers(statisticalRepository.countOrganizers());

        vm.setUpcomingEvents(statisticalRepository.countUpcomingEvents(now));
        vm.setOngoingEvents(statisticalRepository.countOngoingEvents(now));

        vm.setTopEvents(statisticalRepository.findTopEvents(PageRequest.of(0, 5)));
        vm.setLatestEvents(statisticalRepository.findLatestEvents(PageRequest.of(0, 5)));

        vm.setTotalCheckIn(statisticalRepository.countCheckIn());
        vm.setTotalAbsent(statisticalRepository.countAbsent());

        model.addAttribute("vm", vm);
        return "admin/statistical/index";
    }
}