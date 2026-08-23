package com.example.mycalendar2026sar;

import android.content.Context;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.List;
import java.util.Locale;

public class ReportUtils {

    public static String generateHtmlReport(List<Transaction> transactions, String title, double totalIn, double totalOut) {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><style>")
            .append("table { width: 100%; border-collapse: collapse; font-family: sans-serif; }")
            .append("th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }")
            .append("th { background-color: #333; color: white; }")
            .append(".income { color: #4CAF50; font-weight: bold; }")
            .append(".expense { color: #F44336; font-weight: bold; }")
            .append(".footer { margin-top: 20px; border-top: 2px solid #333; padding-top: 10px; font-weight: bold; }")
            .append("h2 { color: #333; text-align: center; }")
            .append("</style></head><body>")
            .append("<h2>").append(title).append("</h2>")
            .append("<table><thead><tr>")
            .append("<th>Date & Time</th><th>Description</th><th>Account</th><th>Amount</th>")
            .append("</tr></thead><tbody>");

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.US);

        for (Transaction t : transactions) {
            html.append("<tr>")
                .append("<td>").append(sdf.format(new java.util.Date(t.getTimestamp()))).append("</td>")
                .append("<td>").append(t.getTitle()).append("</td>")
                .append("<td>").append(t.getAccount() != null ? t.getAccount() : "---").append("</td>")
                .append("<td class=\"").append(t.isCashIn() ? "income" : "expense").append("\">")
                .append(String.format(Locale.US, "%,.2f", t.getAmount()))
                .append("</td></tr>");
        }

        html.append("</tbody></table>")
            .append("<div class=\"footer\">")
            .append("<p>Total Cash In: <span class=\"income\">").append(String.format(Locale.US, "%,.2f", totalIn)).append("</span></p>")
            .append("<p>Total Cash Out: <span class=\"expense\">").append(String.format(Locale.US, "%,.2f", totalOut)).append("</span></p>")
            .append("<p>Balance: ").append(String.format(Locale.US, "%,.2f", totalIn - totalOut)).append("</p>")
            .append("</div></body></html>");

        return html.toString();
    }

    public static String generateCsvReport(List<Transaction> transactions, double totalIn, double totalOut) {
        StringBuilder csv = new StringBuilder();
        csv.append("Date,Time,Description,Account,Amount,Type\n");

        java.text.SimpleDateFormat dateSdf = new java.text.SimpleDateFormat("dd/MM/yyyy", Locale.US);
        java.text.SimpleDateFormat timeSdf = new java.text.SimpleDateFormat("hh:mm a", Locale.US);

        for (Transaction t : transactions) {
            csv.append(dateSdf.format(new java.util.Date(t.getTimestamp()))).append(",")
               .append(timeSdf.format(new java.util.Date(t.getTimestamp()))).append(",")
               .append("\"").append(t.getTitle()).append("\",")
               .append("\"").append(t.getAccount() != null ? t.getAccount() : "").append("\",")
               .append(t.getAmount()).append(",")
               .append(t.isCashIn() ? "Income" : "Expense").append("\n");
        }

        csv.append("\nSummary\n")
           .append("Total Cash In,").append(totalIn).append("\n")
           .append("Total Cash Out,").append(totalOut).append("\n")
           .append("Balance,").append(totalIn - totalOut).append("\n");

        return csv.toString();
    }

    public static void printHtml(Context context, String html, String jobName) {
        WebView webView = new WebView(context);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                PrintManager printManager = (PrintManager) context.getSystemService(Context.PRINT_SERVICE);
                PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter(jobName);
                printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());
            }
        });
        webView.loadDataWithBaseURL(null, html, "text/HTML", "UTF-8", null);
    }
}
