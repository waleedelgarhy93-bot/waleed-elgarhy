package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object WhatsAppHelper {

    fun formatEgyptianNumber(rawPhone: String): String {
        val clean = rawPhone.replace(Regex("[^0-9+]"), "")
        return when {
            clean.startsWith("+20") -> clean.substring(1)
            clean.startsWith("20") -> clean
            clean.startsWith("01") -> "20" + clean.substring(1)
            clean.startsWith("1") && clean.length == 10 -> "20$clean"
            clean.startsWith("0") -> "20" + clean.substring(1)
            else -> if (clean.length >= 9) "20$clean" else clean
        }
    }

    fun openWhatsApp(
        context: Context,
        rawPhone: String,
        customMessage: String? = null
    ) {
        val formattedNumber = formatEgyptianNumber(rawPhone)
        if (formattedNumber.length < 10) {
            Toast.makeText(context, "رقم الهاتف غير صالح للمراسلة", Toast.LENGTH_SHORT).show()
            return
        }

        val message = customMessage ?: "السلام عليكم ورحمة الله وبركاته، معك أمن الإدارة التعليمية بسمالوط (مسؤول الأمن: مستر مصطفى فرغلي) بخصوص متابعة العمل المدرسي."
        val encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8.toString())
        val url = "https://wa.me/$formattedNumber?text=$encodedMessage"

        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback to browser
            try {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(browserIntent)
            } catch (ex: Exception) {
                Toast.makeText(context, "تعذر فتح تطبيق واتساب أو المتصفح", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun makePhoneCall(context: Context, rawPhone: String) {
        val clean = rawPhone.replace(Regex("[^0-9+]"), "")
        if (clean.isBlank()) {
            Toast.makeText(context, "رقم الهاتف غير مسجل", Toast.LENGTH_SHORT).show()
            return
        }
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$clean")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "تعذر فتح تطبيق الهاتف", Toast.LENGTH_SHORT).show()
        }
    }

    fun shareTextToWhatsApp(context: Context, reportText: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            `package` = "com.whatsapp"
            putExtra(Intent.EXTRA_TEXT, reportText)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // General share fallback
            val fallbackIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, reportText)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(fallbackIntent, "مشاركة التقرير الأمني عبر"))
        }
    }
}
