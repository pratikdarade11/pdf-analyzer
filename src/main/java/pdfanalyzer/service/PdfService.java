package pdfanalyzer.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;

@Service
public class PdfService {

    public String extractTextFromPdf(String pdfUrl) throws Exception {

        URL url = new URL(pdfUrl);

        InputStream inputStream = url.openStream();

        PDDocument document = Loader.loadPDF(inputStream.readAllBytes());

        PDFTextStripper stripper = new PDFTextStripper();

        String text = stripper.getText(document);

        document.close();

        inputStream.close();

        return text;

    }

}