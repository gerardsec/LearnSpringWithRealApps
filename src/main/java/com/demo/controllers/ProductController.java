package com.demo.controllers;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.demo.services.ProductService;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.HtmlExporter;

@Controller
@RequestMapping("product")
public class ProductController {

  @Autowired
  private ProductService productService;

  @RequestMapping(method = RequestMethod.GET)
  public String index(ModelMap modelMap) {
    return "product/index";
  }

  @RequestMapping(value = "report", method = RequestMethod.GET)
  public void report(HttpServletResponse response) throws Exception {
    response.setContentType("text/html");
    JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(productService.report());
    InputStream inputStream = this.getClass().getResourceAsStream("/reports/report.jrxml");
    JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
    JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, dataSource);

    JRPdfExporter exporter = new JRPdfExporter();
    exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
    //Para generar el pdf al navegador
    ByteArrayOutputStream pdfReportStream = new ByteArrayOutputStream();
    exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(pdfReportStream));

    SimplePdfReportConfiguration reportConfig = new
            SimplePdfReportConfiguration();
    reportConfig.setSizePageToContent(true);
    reportConfig.setForceLineBreakPolicy(false);

    SimplePdfExporterConfiguration exportConfig = new SimplePdfExporterConfiguration();
    exportConfig.setMetadataAuthor("UAM Admision");
    //Genera el PDF encriptado. Se necesita poner un password para editarlo
    exportConfig.setEncrypted(true);
    //Permite imprimir el PDF
    exportConfig.setAllowedPermissionsHint("PRINTING");
    //Impide que se imprima el PDF
    //exportConfig.setDeniedPermissionsHint("PRINTING");

    exporter.setConfiguration(reportConfig);
    exporter.setConfiguration(exportConfig);
    exporter.exportReport();
    //Envie el archivo pdf como response al navegador
    response.setContentType("application/pdf");
    response.setHeader("Content-Lenght", String.valueOf(pdfReportStream.size()));
    response.addHeader("Content-Disposition","inline; filename=productReport.pdf;");
    OutputStream responseOutputStream = response.getOutputStream();
    responseOutputStream.write((pdfReportStream.toByteArray()));
    responseOutputStream.close();
  }

}
