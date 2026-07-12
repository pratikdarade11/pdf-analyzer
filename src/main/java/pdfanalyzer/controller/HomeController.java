package pdfanalyzer.controller;


import pdfanalyzer.dto.AnalysisResponse;
import pdfanalyzer.entity.PdfAnalysis;
import pdfanalyzer.repository.PdfAnalysisRepository;
import pdfanalyzer.service.GeminiService;
import pdfanalyzer.service.PdfService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {

    private final PdfService pdfService;
    private final GeminiService geminiService;
    private final PdfAnalysisRepository repository;

    public HomeController(PdfService pdfService,
                          GeminiService geminiService,
                          PdfAnalysisRepository repository) {

        this.pdfService = pdfService;
        this.geminiService = geminiService;
        this.repository = repository;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @PostMapping("/analyze")
    public String analyze(@RequestParam String pdfUrl,
                          Model model) {

        try {

            String text =
                    pdfService.extractTextFromPdf(pdfUrl);

            if (text.length() > 15000) {
                text = text.substring(0, 15000);
            }

            AnalysisResponse response =
                    geminiService.analyzePdf(text);

            PdfAnalysis analysis = new PdfAnalysis();

            analysis.setPdfUrl(pdfUrl);
            analysis.setDocumentType(response.getDocumentType());
            analysis.setTitle(response.getTitle());
            analysis.setAuthors(response.getAuthors());
            analysis.setSummary(response.getSummary());
            analysis.setKeyTakeaway(response.getKeyTakeaway());

            repository.save(analysis);

            model.addAttribute("analysis", analysis);

        } catch (Exception e) {

            PdfAnalysis error = new PdfAnalysis();

            error.setDocumentType("Error");
            error.setTitle("Analysis Failed");
            error.setAuthors("-");
            error.setSummary(e.getMessage());
            error.setKeyTakeaway("Please try another PDF.");

            model.addAttribute("analysis", error);

        }

        return "result";

    }

}