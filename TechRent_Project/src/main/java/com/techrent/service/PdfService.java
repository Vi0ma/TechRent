package com.techrent.service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.techrent.model.Materiel;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

public class PdfService {

    public void genererPdfMateriel(List<Materiel> listeMateriel, File fichierDestination) throws Exception {

        PdfWriter writer = new PdfWriter(fichierDestination);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);


        document.add(new Paragraph("INVENTAIRE TECHRENT")
                .setFontSize(20)
                .setBold()
                .setFontColor(ColorConstants.BLUE)
                .setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("Date : " + LocalDate.now())
                .setFontSize(10)
                .setItalic()
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginBottom(20));


        float[] columnWidths = {3, 5, 3, 3, 3};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));


        addHeader(table, "RÉFÉRENCE");
        addHeader(table, "NOM");
        addHeader(table, "CATÉGORIE");
        addHeader(table, "PRIX/J");
        addHeader(table, "ÉTAT");


        for (Materiel m : listeMateriel) {
            table.addCell(new Paragraph(m.getReference()).setFontSize(10));
            table.addCell(new Paragraph(m.getNom()).setFontSize(10));
            table.addCell(new Paragraph(m.getCategorie().getLibelle()).setFontSize(10));
            table.addCell(new Paragraph(m.getPrixParJour() + " MAD").setFontSize(10));
            table.addCell(new Paragraph(m.getEtat()).setFontSize(10));
        }

        document.add(table);
        document.close();
    }

    private void addHeader(Table table, String text) {
        table.addHeaderCell(new Cell().add(new Paragraph(text).setBold().setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(ColorConstants.GRAY)
                .setTextAlignment(TextAlignment.CENTER));
    }
}