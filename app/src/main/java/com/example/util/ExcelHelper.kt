package com.example.util

import android.content.Context
import android.net.Uri
import com.example.data.model.School
import com.example.data.model.SchoolStage
import com.example.data.model.Teacher
import java.io.BufferedReader
import java.io.InputStreamReader

object ExcelHelper {

    data class ImportResult<T>(
        val items: List<T>,
        val errors: List<String>,
        val totalProcessed: Int
    )

    fun parseSchoolsFromText(content: String): ImportResult<School> {
        val lines = content.lines().filter { it.isNotBlank() }
        val schools = mutableListOf<School>()
        val errors = mutableListOf<String>()

        var startIndex = 0
        if (lines.isNotEmpty() && (lines[0].contains("الاسم") || lines[0].contains("اسم المدرسة"))) {
            startIndex = 1
        }

        for (i in startIndex until lines.size) {
            val line = lines[i]
            val delimiter = when {
                line.contains("\t") -> "\t"
                line.contains(";") -> ";"
                else -> ","
            }
            val tokens = line.split(delimiter).map { it.trim().removeSurrounding("\"") }
            if (tokens.isEmpty() || tokens[0].isBlank()) continue

            try {
                val name = tokens.getOrNull(0) ?: ""
                val stageStr = tokens.getOrNull(1) ?: "ابتدائي"
                val stage = SchoolStage.fromString(stageStr)
                val classrooms = tokens.getOrNull(2)?.toIntOrNull() ?: 12
                val staff = tokens.getOrNull(3)?.toIntOrNull() ?: 35
                val principal = tokens.getOrNull(4) ?: ""
                val vicePrincipal = tokens.getOrNull(5) ?: ""
                val address = tokens.getOrNull(6) ?: "مركز سمالوط"

                if (name.isNotBlank()) {
                    schools.add(
                        School(
                            name = name,
                            stage = stage,
                            classroomsCount = classrooms,
                            staffCount = staff,
                            principalName = principal,
                            vicePrincipalName = vicePrincipal,
                            address = address
                        )
                    )
                }
            } catch (e: Exception) {
                errors.add("خطأ في السطر ${i + 1}: ${e.localizedMessage}")
            }
        }

        return ImportResult(schools, errors, lines.size - startIndex)
    }

    fun parseTeachersFromText(content: String): ImportResult<Teacher> {
        val lines = content.lines().filter { it.isNotBlank() }
        val teachers = mutableListOf<Teacher>()
        val errors = mutableListOf<String>()

        var startIndex = 0
        if (lines.isNotEmpty() && (lines[0].contains("الاسم") || lines[0].contains("اسم المعلم"))) {
            startIndex = 1
        }

        for (i in startIndex until lines.size) {
            val line = lines[i]
            val delimiter = when {
                line.contains("\t") -> "\t"
                line.contains(";") -> ";"
                else -> ","
            }
            val tokens = line.split(delimiter).map { it.trim().removeSurrounding("\"") }
            if (tokens.isEmpty() || tokens[0].isBlank()) continue

            try {
                val name = tokens.getOrNull(0) ?: ""
                val schoolName = tokens.getOrNull(1) ?: "مدرسة سمالوط"
                val stageStr = tokens.getOrNull(2) ?: "ابتدائي"
                val stage = SchoolStage.fromString(stageStr)
                val jobTitle = tokens.getOrNull(3) ?: "معلم"
                val phone = tokens.getOrNull(4) ?: ""
                val nationalId = tokens.getOrNull(5) ?: ""
                val subject = tokens.getOrNull(6) ?: ""
                val hireDate = tokens.getOrNull(7) ?: ""

                if (name.isNotBlank()) {
                    teachers.add(
                        Teacher(
                            name = name,
                            schoolName = schoolName,
                            stage = stage,
                            jobTitle = jobTitle,
                            phone = phone,
                            nationalId = nationalId,
                            subject = subject,
                            hireDate = hireDate
                        )
                    )
                }
            } catch (e: Exception) {
                errors.add("خطأ في السطر ${i + 1}: ${e.localizedMessage}")
            }
        }

        return ImportResult(teachers, errors, lines.size - startIndex)
    }

    fun readContentFromUri(context: Context, uri: Uri): String? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8)).use { reader ->
                    reader.readText()
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    fun getSampleTeachersCsv(): String {
        return """الاسم,المدرسة,المرحلة,الوظيفة,رقم الهاتف,الرقم القومي,المادة,تاريخ التعيين
أ/ مصطفى كمال محمود,مدرسة سمالوط الثانوية بنين,ثانوي,معلم أول أ,01011223344,28605151700987,لغة إنجليزية,2010/10/01
أ/ إيمان صابر عبد الغني,مدرسة سمالوط الإعدادية بنات,إعدادي,معلم خبير,01144556677,28409201700432,علوم,2007/03/15
أ/ رأفت نصحي فهيم,مدرسة قلوصنا الإعدادية المشتركة,إعدادي,معلم أول,01233445566,28903101700765,رياضيات,2013/09/01
أ/ مروة إسماعيل حسن,مدرسة الشهيد أحمد كمال الابتدائية,ابتدائي,معلمة فصل,01099881122,29507111700345,معلم فصل,2019/11/01
أ/ حسام فاروق جودة,مدرسة شوشة الابتدائية المشتركة,ابتدائي,أخصائي صحافة,01122339900,29108141700123,إعلام تربوي,2015/02/01"""
    }

    fun getSampleSchoolsCsv(): String {
        return """الاسم,المرحلة,عدد الفصول,عدد العاملين,المدير,الوكيل,العنوان
مدرسة منقطين الثانوية المشتركة,المرحلة الثانوية,16,45,أ/ صلاح الدين حسن,أ/ مروان صبحي,قرية منقطين - سمالوط
مدرسة حسن عبد الرحيم الإعدادية,المرحلة الإعدادية,18,50,أ/ مجدي كمال رمزي,أ/ سعاد بدوي,قرية البيهو - سمالوط
مدرسة طحا الأعمدة الابتدائية,المرحلة الابتدائية,20,55,أ/ سمير عبد الواحد,أ/ هدى ناصف,قرية طحا الأعمدة - سمالوط
مدرسة الأقباط الإعدادية بنين,المرحلة الإعدادية,15,42,أ/ مكرم حبيب شنودة,أ/ سامر لطفي,سمالوط البلد
مدرسة الشهداء الابتدائية المشتركة,المرحلة الابتدائية,14,38,أ/ أحمد عبد المنعم,أ/ منى عبد العزيز,قرية دفش - سمالوط"""
    }
}
