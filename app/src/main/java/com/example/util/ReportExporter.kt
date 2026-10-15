package com.example.util

import android.content.Context
import android.content.Intent
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.example.data.model.SecurityIncident
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ReportExporter {

    enum class ReportPeriod(val titleAr: String) {
        DAILY("التقرير اليومي"),
        WEEKLY("التقرير الأسبوعي"),
        MONTHLY("التقرير الشهري"),
        ANNUAL("التقرير السنوي")
    }

    fun generateReportText(
        period: ReportPeriod,
        incidents: List<SecurityIncident>,
        supervisorName: String = "مستر مصطفى فرغلي"
    ): String {
        val total = incidents.size
        val resolved = incidents.count { it.status.name == "RESOLVED" }
        val inProgress = incidents.count { it.status.name == "IN_PROGRESS" }
        val closed = incidents.count { it.status.name == "CLOSED" }

        val criticalCount = incidents.count { it.severity.name == "CRITICAL" }
        val highCount = incidents.count { it.severity.name == "HIGH" }
        val mediumCount = incidents.count { it.severity.name == "MEDIUM" }
        val lowCount = incidents.count { it.severity.name == "LOW" }

        val currentDate = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date())
        val currentTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())

        val sb = StringBuilder()
        sb.append("📋 *إدارة أمن الإدارة التعليمية بسمالوط*\n")
        sb.append("══════════════════════════\n")
        sb.append("🔹 *نوع التقرير:* ${period.titleAr}\n")
        sb.append("🔹 *التاريخ:* $currentDate ($currentTime)\n")
        sb.append("🔹 *المسؤول الأمني:* $supervisorName\n")
        sb.append("══════════════════════════\n\n")

        sb.append("📊 *الملخص الإحصائي العام:*\n")
        sb.append("• إجمالي الوقائع المسجلة: $total\n")
        sb.append("• تم الحل والتسوية: $resolved ✅\n")
        sb.append("• قيد المتابعة والإجراء: $inProgress ⏳\n")
        sb.append("• وقائع مغلقة: $closed 📁\n\n")

        sb.append("⚠️ *مستوى الخطورة:*\n")
        sb.append("• خطير جداً: $criticalCount 🚨\n")
        sb.append("• عاجل: $highCount 🔴\n")
        sb.append("• متوسط: $mediumCount 🟡\n")
        sb.append("• عادي: $lowCount 🟢\n\n")

        sb.append("📑 *بيان تفصيلي بأهم الأحداث والوقائع:*\n")
        if (incidents.isEmpty()) {
            sb.append("• لا توجد وقائع مسجلة خلال هذه الفترة - الحالة الأمنية مستقرة تماماً بفضل الله.\n")
        } else {
            incidents.take(15).forEachIndexed { index, item ->
                sb.append("${index + 1}) *${item.title}*\n")
                sb.append("   - المدرسة: ${item.schoolName}\n")
                sb.append("   - النوع: ${item.incidentType.displayNameAr}\n")
                sb.append("   - الخطورة: ${item.severity.displayNameAr} | الحالة: ${item.status.displayNameAr}\n")
                sb.append("   - الأطراف: ${item.partiesInvolved}\n")
                if (item.actionTaken.isNotBlank()) {
                    sb.append("   - الإجراء: ${item.actionTaken}\n")
                }
                sb.append("   - التاريخ: ${item.dateString} ${item.timeString}\n\n")
            }
            if (incidents.size > 15) {
                sb.append("... بالإضافة إلى ${incidents.size - 15} واقعة أخرى مسجلة في قاعدة بيانات الإدارة.\n\n")
            }
        }

        sb.append("══════════════════════════\n")
        sb.append("تحريراً في: $currentDate\n")
        sb.append("مسؤول أمن الإدارة التعليمية بسمالوط:\n")
        sb.append("*$supervisorName*")

        return sb.toString()
    }

    fun exportToPdfPrint(
        context: Context,
        period: ReportPeriod,
        incidents: List<SecurityIncident>,
        supervisorName: String = "مستر مصطفى فرغلي"
    ) {
        val currentDate = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date())
        val html = buildHtmlReport(period, incidents, supervisorName, currentDate)

        val webView = WebView(context)
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
                val printAdapter = webView.createPrintDocumentAdapter("تقرير_أمن_سمالوط_${period.name}")
                val jobName = "تقرير أمن سمالوط - ${period.titleAr}"
                printManager?.print(jobName, printAdapter, PrintAttributes.Builder().build())
            }
        }
        webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
    }

    private fun buildHtmlReport(
        period: ReportPeriod,
        incidents: List<SecurityIncident>,
        supervisorName: String,
        currentDate: String
    ): String {
        val rows = StringBuilder()
        incidents.forEachIndexed { i, it ->
            val bg = when (it.severity.name) {
                "CRITICAL" -> "#FFEBEE"
                "HIGH" -> "#FBE9E7"
                "MEDIUM" -> "#FFF8E1"
                else -> "#E8F5E9"
            }
            rows.append("""
                <tr style="background-color: $bg;">
                    <td style="padding: 8px; border: 1px solid #ddd;">${i + 1}</td>
                    <td style="padding: 8px; border: 1px solid #ddd; font-weight: bold;">${it.title}</td>
                    <td style="padding: 8px; border: 1px solid #ddd;">${it.schoolName}</td>
                    <td style="padding: 8px; border: 1px solid #ddd;">${it.incidentType.displayNameAr}</td>
                    <td style="padding: 8px; border: 1px solid #ddd;">${it.severity.displayNameAr}</td>
                    <td style="padding: 8px; border: 1px solid #ddd;">${it.status.displayNameAr}</td>
                    <td style="padding: 8px; border: 1px solid #ddd;">${it.actionTaken}</td>
                    <td style="padding: 8px; border: 1px solid #ddd;">${it.dateString}</td>
                </tr>
            """.trimIndent())
        }

        return """
            <!DOCTYPE html>
            <html dir="rtl" lang="ar">
            <head>
                <meta charset="utf-8">
                <title>تقرير أمن سمالوط</title>
                <style>
                    body { font-family: 'Segoe UI', Tahoma, Arial, sans-serif; padding: 24px; direction: rtl; text-align: right; }
                    .header { text-align: center; border-bottom: 3px double #1565C0; padding-bottom: 12px; margin-bottom: 20px; }
                    .title { color: #1565C0; margin: 0; font-size: 24px; }
                    .sub { color: #555; margin: 6px 0; font-size: 14px; }
                    table { width: 100%; border-collapse: collapse; margin-top: 15px; font-size: 12px; }
                    th { background-color: #1565C0; color: white; padding: 10px; border: 1px solid #ccc; }
                    .footer { margin-top: 30px; text-align: left; padding-left: 40px; font-weight: bold; }
                    .stats { display: flex; justify-content: space-between; margin: 15px 0; background: #F0F4F8; padding: 10px; border-radius: 8px; }
                </style>
            </head>
            <body>
                <div class="header">
                    <h2 class="title">مديرية التربية والتعليم بالمنيا - إدارة سمالوط التعليمية</h2>
                    <h3 style="margin: 5px 0; color: #2E7D32;">مكتب أمن الإدارة التعليمية</h3>
                    <p class="sub">${period.titleAr} - تحريراً في: $currentDate</p>
                </div>
                
                <div class="stats">
                    <div><b>إجمالي الوقائع:</b> ${incidents.size}</div>
                    <div><b>تم الحل:</b> ${incidents.count { it.status.name == "RESOLVED" }}</div>
                    <div><b>جاري المتابعة:</b> ${incidents.count { it.status.name == "IN_PROGRESS" }}</div>
                    <div><b>المسؤول الأمني:</b> $supervisorName</div>
                </div>

                <table>
                    <thead>
                        <tr>
                            <th>م</th>
                            <th>موضوع الواقعة</th>
                            <th>المدرسة</th>
                            <th>نوع الواقعة</th>
                            <th>الخطورة</th>
                            <th>الحالة</th>
                            <th>الإجراء المتخذ</th>
                            <th>التاريخ</th>
                        </tr>
                    </thead>
                    <tbody>
                        $rows
                    </tbody>
                </table>

                <div class="footer">
                    <p>مسؤول أمن الإدارة التعليمية بسمالوط</p>
                    <p style="color: #1565C0; font-size: 16px;">$supervisorName</p>
                </div>
            </body>
            </html>
        """.trimIndent()
    }

    fun exportIncidentsToCsv(incidents: List<SecurityIncident>): String {
        val sb = StringBuilder()
        sb.append("م,العنوان,المدرسة,النوع,الخطورة,الحالة,الأطراف,الإجراء,التاريخ,الوقت\n")
        incidents.forEachIndexed { i, it ->
            sb.append("${i + 1},\"${it.title}\",\"${it.schoolName}\",\"${it.incidentType.displayNameAr}\",\"${it.severity.displayNameAr}\",\"${it.status.displayNameAr}\",\"${it.partiesInvolved}\",\"${it.actionTaken}\",\"${it.dateString}\",\"${it.timeString}\"\n")
        }
        return sb.toString()
    }

    fun shareCsvToExternal(context: Context, csvText: String, filename: String = "تقرير_وقائع_أمن_سمالوط.csv") {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_SUBJECT, filename)
            putExtra(Intent.EXTRA_TEXT, csvText)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(Intent.createChooser(intent, "تصدير ملف Excel / CSV"))
        } catch (e: Exception) {
            Toast.makeText(context, "تعذر مشاركة الملف", Toast.LENGTH_SHORT).show()
        }
    }
}
