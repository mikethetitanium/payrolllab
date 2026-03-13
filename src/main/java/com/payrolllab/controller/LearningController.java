package com.payrolllab.controller;

import com.payrolllab.entity.ReportSource;
import com.payrolllab.repository.ReportSourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/learning")
@RequiredArgsConstructor
public class LearningController {
    
    private final ReportSourceRepository reportSourceRepository;
    
    @GetMapping
    public String learningHome(Model model) {
        return "learning/index";
    }
    
    @GetMapping("/sql-queries")
    public String sqlQueries(Model model) {
        model.addAttribute("reports", reportSourceRepository.findAll());
        return "learning/sql-queries";
    }
    
    @GetMapping("/report-metadata")
    public String reportMetadata(Model model) {
        return "learning/report-metadata";
    }
    
    @GetMapping("/data-model")
    public String dataModel(Model model) {
        return "learning/data-model";
    }
    
    @GetMapping("/report-flow")
    public String reportFlow(Model model) {
        return "learning/report-flow";
    }
}
