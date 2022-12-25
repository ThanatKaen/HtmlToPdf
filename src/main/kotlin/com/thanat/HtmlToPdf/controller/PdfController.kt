package com.thanat.HtmlToPdf

import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.layout.font.FontProvider;
import kotlin.io.buffered

@RestController
@RequestMapping("/pdf")
class PdfController {
    private final val LOGGER = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    @PostMapping("/create")
    fun pdf(@RequestBody pdfModel: PdfModel): ResponseEntity<InputStreamResource> {
        val timestamp: Timestamp = Timestamp(System.currentTimeMillis());
        val fileName: String = "./src/main/resources/temp/" + timestamp.getTime() + ".pdf";

        val writerProperties: WriterProperties  = WriterProperties();
        writerProperties.addXmpMetadata();

        val converterProperties: ConverterProperties = ConverterProperties();
        val fontProvider: FontProvider = DefaultFontProvider(false, true, true);
        converterProperties.setFontProvider(fontProvider);

        val pdfWriter: PdfWriter = PdfWriter(fileName, writerProperties)
        pdfWriter.use{
            val document: PdfDocument = PdfDocument(pdfWriter)
            document.use{
                document.setDefaultPageSize(PageSize(pdfModel.width, pdfModel.height));
                HtmlConverter.convertToPdf(pdfModel.html, document, converterProperties);
            }
        }

        val file = File(fileName);
        try {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + timestamp.getTime() + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(InputStreamResource(FileInputStream(file)));
        } finally {
            if (file.exists() && !file.isDirectory()) {
                cleanUp(file.toPath());
                LOGGER.log(Level.INFO, "Delete is Successful!");
            } else {
                LOGGER.log(Level.WARNING, "Delete is Failed!");
            }
        }
    }

    fun cleanUp(path:Path) {
        try {
            Files.delete(path);
        } catch (e: IOException) {
            LOGGER.log(Level.WARNING, e.message);
        }
    }

}
