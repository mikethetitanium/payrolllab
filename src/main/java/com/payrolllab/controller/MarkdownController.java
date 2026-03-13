package com.payrolllab.controller;

import com.payrolllab.service.MarkdownService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/docs")
@RequiredArgsConstructor
@Slf4j
public class MarkdownController {
    
    private final MarkdownService markdownService;
    
    /**
     * Display a markdown file as HTML
     * @param filename The markdown file name (without .md extension)
     * @param model The model to pass data to template
     * @return The view name
     */
    @GetMapping("/{filename}")
    public String viewMarkdown(@PathVariable String filename, Model model) {
        log.info("Viewing markdown file: {}", filename);
        
        try {
            // Security: Only allow alphanumeric, underscore, and hyphen
            if (!filename.matches("[a-zA-Z0-9_-]+")) {
                log.warn("Invalid filename requested: {}", filename);
                model.addAttribute("error", "Invalid filename");
                return "error";
            }
            
            // Read markdown file from project root
            Path filePath = Paths.get(filename + ".md");
            log.debug("Looking for file at: {}", filePath.toAbsolutePath());
            
            if (!Files.exists(filePath)) {
                log.warn("File not found: {}", filePath);
                model.addAttribute("error", "File not found: " + filename + ".md");
                return "error";
            }
            
            String markdownContent = Files.readString(filePath);
            String htmlContent = markdownService.markdownToHtml(markdownContent);
            
            model.addAttribute("title", filename.replace("_", " ").replace("-", " "));
            model.addAttribute("content", htmlContent);
            
            log.info("Successfully rendered markdown: {}", filename);
            return "markdown-viewer";
        } catch (IOException e) {
            log.error("Error reading markdown file: {}", filename, e);
            model.addAttribute("error", "Error reading file: " + e.getMessage());
            return "error";
        }
    }
}
