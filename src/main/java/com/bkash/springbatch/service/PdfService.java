package com.bkash.springbatch.service;

import com.bkash.springbatch.dao.CompanyFundingRecordRepository;
import com.bkash.springbatch.model.CompanyFundingRecordEntity;
import com.bkash.springbatch.utils.NumberToSpelling;
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

    CompanyFundingRecordRepository companyFundingRecordRepository;

    @Autowired
    SpringTemplateEngine templateEngine;

    ITextRenderer renderer;

    public PdfService(CompanyFundingRecordRepository companyFundingRecordRepository, ITextRenderer renderer) {
        this.companyFundingRecordRepository = companyFundingRecordRepository;
        this.renderer = renderer;
    }

    public CompanyFundingRecordEntity generatePdf(){
        Context context = new Context();

        List<CompanyFundingRecordEntity> companyFundingRecordEntities = (List<CompanyFundingRecordEntity>) companyFundingRecordRepository.findAll();
        if(companyFundingRecordEntities ==null){
            throw new RuntimeException("Report list is empty") ;
        }

        context.setVariable("records", companyFundingRecordEntities);
        BigDecimal grandTotal = calculateGrandTotal(companyFundingRecordEntities);
        context.setVariable("grandTotal", new DecimalFormat("#.00").format(grandTotal.doubleValue()));
        context.setVariable("grandTotalWord", NumberToSpelling
                .generateBalanceInWord((new DecimalFormat("#.00").format(grandTotal))));

        generatePdf(context, "report", "report");

        return new CompanyFundingRecordEntity();
    }

    private void generatePdf(Context context, String inputFileName, String outputFileName){
       try{
           String htmlContentToRender = templateEngine.process(inputFileName, context);
           String xHtml = xhtmlConvert(htmlContentToRender);

           String baseUrl = FileSystems
                   .getDefault()
                   .getPath("src", "main", "resources","templates")
                   .toUri()
                   .toURL()
                   .toString();
           renderer.setDocumentFromString(xHtml, baseUrl);
           renderer.layout();

           OutputStream outputStream = new FileOutputStream("src//"+outputFileName+".pdf");
           renderer.createPDF(outputStream);
           outputStream.close();
       }catch (Exception e){
           e.printStackTrace();
       }
    }

    private BigDecimal calculateGrandTotal(List<CompanyFundingRecordEntity> companyFundingRecordEntities) {
        BigDecimal grandTotal = new BigDecimal(0);
        try {
            for(int i = 0; i< companyFundingRecordEntities.size(); i++){
                grandTotal = grandTotal.add(companyFundingRecordEntities.get(i).getRaisedAmount());
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
