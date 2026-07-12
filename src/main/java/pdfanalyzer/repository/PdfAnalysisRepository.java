package pdfanalyzer.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pdfanalyzer.entity.PdfAnalysis;

@Repository
public interface PdfAnalysisRepository extends JpaRepository<PdfAnalysis, Long> {
}