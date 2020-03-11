package com.bkash.springbatch.service;

import com.bkash.springbatch.dao.ReportRepository;
import com.bkash.springbatch.model.Report;
import com.bkash.springbatch.utils.NumberToSpelling;
import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.FileSystems;
import java.text.DecimalFormat;
import java.util.List;

@Service
public class PdfService {

    private ReportRepository reportRepository;

    @Autowired
    SpringTemplateEngine templateEngine;

    public PdfService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public Report generatePdf()throws IOException, DocumentException {
        Context context = new Context();

        List<Report> reports = reportRepository.findByBankName("THE CITY BANK LTD.");
        if(reports==null){
            throw new RuntimeException("Report list is empty") ;
        }

        context.setVariable("reports",reports);
        BigDecimal grandTotal = calculateGrandTotal(reports);
        context.setVariable("grandTotal", new DecimalFormat("#.00").format(grandTotal.doubleValue()));
        context.setVariable("grandTotalWord", NumberToSpelling
                .generateBalanceInWord((new DecimalFormat("#.00").format(grandTotal))));

        String htmlContentToRender = templateEngine.process("report", context);
        String xHtml = xhtmlConvert(htmlContentToRender);

        ITextRenderer renderer = new ITextRenderer();

        String baseUrl = FileSystems
                .getDefault()
                .getPath("src", "main", "resources","templates")
                .toUri()
                .toURL()
                .toString();
        renderer.setDocumentFromString(xHtml, baseUrl);
        renderer.layout();

        OutputStream outputStream = new FileOutputStream("src//report.pdf");
        renderer.createPDF(outputStream);
        outputStream.close();

        return new Report();
    }

    private BigDecimal calculateGrandTotal(List<Report> reports) {
        BigDecimal grandTotal = new BigDecimal(0);
        try {
            for(int i=0;i< reports.size();i++){
                grandTotal = grandTotal.add(new BigDecimal(reports.get(i).getRefundAmount()));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return grandTotal;
    }

    private String xhtmlConvert(String html) throws UnsupportedEncodingException {
        Tidy tidy = new Tidy();
        tidy.setInputEncoding("UTF-8");
        tidy.setOutputEncoding("UTF-8");
        tidy.setXHTML(true);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(html.getBytes("UTF-8"));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        tidy.parseDOM(inputStream, outputStream);
        return outputStream.toString("UTF-8");
    }
}
