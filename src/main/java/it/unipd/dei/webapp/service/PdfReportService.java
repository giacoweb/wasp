package it.unipd.dei.webapp.service;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import it.unipd.dei.webapp.dao.ReportSessionDAO;
import it.unipd.dei.webapp.model.ReportSession;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

public class PdfReportService {

    public byte[] generateGuestPdf(UUID userId) throws Exception {

        ReportSession reportSession = new ReportSessionDAO().findBySessionToken(userId);
        if (reportSession == null || reportSession.getReportJson() == null || reportSession.getReportJson().equals("{}")) {
            throw new IllegalArgumentException("No data to save.");
        }

        JSONObject root = new JSONObject(reportSession.getReportJson());


        try {
            PDDocument document = new PDDocument();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            PDPageContentStream stream = new PDPageContentStream(document, page);

            //Defining the two font types
            PDType1Font boldFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDType1Font normalFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            float yPos = 800;
            writeText(stream, boldFont, 18, 50, yPos, "FinTrack - Guest Report");
            yPos -= 40;

            // ==========================================
            // LOAN
            // ==========================================
            if(root.has("loan"))
            {
                JSONObject loanNode = root.getJSONObject("loan");
                writeText(stream, boldFont, 14, 50, yPos, "LOAN CALCULATOR RESULT");
                yPos -= 20;

                writeText(stream, normalFont, 11, 50, yPos, "Principal: " + loanNode.optString("principal") + "€");
                writeText(stream, normalFont, 11, 230, yPos, "Annual Rate: " + loanNode.optString("annualRate") + "%");
                writeText(stream, normalFont, 11, 400, yPos, "Total Months: " + loanNode.optString("months") + "months");
                yPos -= 35;

                float[] colX = {50, 100, 200, 310, 420};
                yPos = drawTableHeader(stream, boldFont, colX, yPos, new String[]{"months", "Total Payment", "Principal Paid", "Interest Paid", "Remaining Balance"});

                if (loanNode.has("schedule")) {

                    JSONArray arr = loanNode.getJSONArray("schedule");
                    for (int i = 0; i < arr.length(); i++){
                        JSONObject r = arr.getJSONObject(i);
                        String[] rigaDati = {
                                String.valueOf(r.getInt("month")),
                                "EUR " + r.getString("totalPayment"),
                                "EUR " + r.getString("principalPaid"),
                                "EUR " + r.getString("interestPaid"),
                                "EUR " + r.getString("remainingBalance")
                        };

                        if (yPos < 80) {
                            stream.close();
                            page = new PDPage(PDRectangle.A4);
                            document.addPage(page);
                            stream = new PDPageContentStream(document, page);
                            yPos = 800;
                            yPos = drawTableHeader(stream, boldFont, colX, yPos, new String[]{"months", "Total Payment", "Principal Paid", "Interest Paid", "Remaining Balance"});
                        }

                        yPos = drawTableRow(stream, normalFont, colX, yPos, rigaDati);




                    }


                }

                yPos -= 30;

            }


            // ==========================================
            // COMPOUND INTEREST
            // ==========================================
            if (root.has("compound")) {
                JSONObject compNode = root.getJSONObject("compound");

                if (yPos < 150) {
                    stream.close();
                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);
                    stream = new PDPageContentStream(document, page);
                    yPos = 800;
                }

                writeText(stream, boldFont, 14, 50, yPos, "COMPOUND CALCULATOR RESULT");
                yPos -= 20;

                writeText(stream, normalFont, 11, 50, yPos, "Initial Capital: " + compNode.optString("initialCapital") + "€");
                writeText(stream, normalFont, 11, 200, yPos, "Monthly Contr.: " + compNode.optString("monthlyContribution") + "€");
                writeText(stream, normalFont, 11, 350, yPos, "Annual Rate: " + compNode.optString("annualRate") + "%");
                writeText(stream, normalFont, 11, 480, yPos, "Years: " + compNode.optString("years"));
                yPos -= 35;

                float[] colX = {50, 200};
                yPos = drawTableHeader(stream, boldFont, colX, yPos, new String[]{"Year", "Balance"});
                if (compNode.has("projection"))
                {
                    JSONArray arr = compNode.getJSONArray("projection");
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject r = arr.getJSONObject(i);
                        String[] dataRow = {
                                String.valueOf(r.getInt("year")),
                                "EUR " + r.getString("balance")
                        };

                        if (yPos < 80) {
                            stream.close();
                            page = new PDPage(PDRectangle.A4);
                            document.addPage(page);
                            stream = new PDPageContentStream(document, page);
                            yPos = 800;
                            yPos = drawTableHeader(stream, boldFont, colX, yPos, new String[]{"Year", "Balance"});
                        }

                        yPos = drawTableRow(stream, normalFont, colX, yPos, dataRow);

                    }

                }

                yPos -= 30;

            }



            if (root.has("budget")) {
                JSONObject budNode = root.getJSONObject("budget");

                if (yPos < 250) {
                    stream.close();
                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);
                    stream = new PDPageContentStream(document, page);
                    yPos = 800;
                }

                writeText(stream, boldFont, 14, 50, yPos, "BUDGET CALCULATOR RESULT");
                yPos -= 20;

                writeText(stream, boldFont, 12, 50, yPos, "Total Income: " + budNode.optString("totalIncome")+"€");
                writeText(stream, boldFont, 12, 230, yPos, "Total Expenses: " + budNode.optString("totalExpenses")+"€");
                writeText(stream, boldFont, 12, 430, yPos, "Net Value: " + budNode.optString("net")+"€");
                yPos -= 40;

                float[] colX = {50, 300};
                writeText(stream, boldFont, 13, 50, yPos, "Income Detail");
                yPos -= 15;
                yPos = drawTableHeader(stream, boldFont, colX, yPos, new String[]{"Category", "Amount"});

                JSONObject incomeObj = budNode.optJSONObject("income");
                if (incomeObj != null) {
                    for (String key : incomeObj.keySet()) {
                        yPos = drawTableRow(stream, normalFont, colX, yPos, new String[]{key, incomeObj.getString(key)+"€"});
                    }
                }

                yPos -= 20;

                writeText(stream, boldFont, 13, 50, yPos, "Expenses Detail");
                yPos -= 15;
                yPos = drawTableHeader(stream, boldFont, colX, yPos, new String[]{"Category", "Amount"});
                JSONObject expenseObj = budNode.optJSONObject("expenses");
                if (expenseObj != null) {
                    for (String key : expenseObj.keySet()) {
                        yPos = drawTableRow(stream, normalFont, colX, yPos, new String[]{key, expenseObj.getString(key)+"€"});
                    }
                }

            }

            // ==========================================
            // FOOTER CON CTA
            // ==========================================
            if (yPos < 60) {
                stream.close();
                page = new PDPage(PDRectangle.A4);
                document.addPage(page);
                stream = new PDPageContentStream(document, page);
            }

            writeText(stream, normalFont, 10, 50, 40, "Create a free account at http://localhost:8080/FinTrack/register to track this data over time");
            stream.close();
            document.save(out);
            return out.toByteArray();



        } catch (Exception e) {
            throw new RuntimeException(e);
        }




    }

    private void writeText(PDPageContentStream stream, PDType1Font font, float fontSize, float x, float y, String text) throws IOException {
        stream.beginText();
        stream.setFont(font, fontSize);
        stream.newLineAtOffset(x, y);
        stream.showText(text);
        stream.endText();
    }

    private float drawTableHeader(PDPageContentStream stream, PDType1Font font, float[] colXPath, float currentY, String[] headers) throws IOException {
        stream.setLineWidth(1f);
        stream.moveTo(40, currentY + 12);
        stream.lineTo(550, currentY + 12);
        stream.stroke();
        for (int i = 0; i < headers.length; i++) {
            writeText(stream, font, 11, colXPath[i], currentY, headers[i]);
        }
        stream.moveTo(40, currentY - 5);
        stream.lineTo(550, currentY - 5);
        stream.stroke();
        return currentY - 20;
    }

    private float drawTableRow(PDPageContentStream stream, PDType1Font font, float[] colXPath, float currentY, String[] rowData) throws IOException {
        for (int i = 0; i < rowData.length; i++) {
            writeText(stream, font, 10, colXPath[i], currentY, rowData[i] != null ? rowData[i] : "");
        }

        stream.setLineWidth(0.3f);
        stream.moveTo(40, currentY - 3);
        stream.lineTo(550, currentY - 3);
        stream.stroke();
        return currentY - 15;
    }

}
