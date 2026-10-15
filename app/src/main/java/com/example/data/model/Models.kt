package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class SchoolStage(val displayNameAr: String) {
    PRIMARY("المرحلة الابتدائية"),
    PREPARATORY("المرحلة الإعدادية"),
    SECONDARY("المرحلة الثانوية");

    companion object {
        fun fromString(str: String): SchoolStage {
            return when {
                str.contains("ابتدائ") -> PRIMARY
                str.contains("إعداد") || str.contains("اعداد") -> PREPARATORY
                str.contains("ثانو") -> SECONDARY
                else -> PRIMARY
            }
        }
    }
}

enum class SeverityLevel(val displayNameAr: String) {
    LOW("عادي"),
    MEDIUM("متوسط"),
    HIGH("عاجل"),
    CRITICAL("خطير جداً");

    companion object {
        fun fromString(str: String): SeverityLevel {
            return when {
                str.contains("خطير") -> CRITICAL
                str.contains("عاجل") -> HIGH
                str.contains("متوسط") -> MEDIUM
                else -> LOW
            }
        }
    }
}

enum class IncidentStatus(val displayNameAr: String) {
    IN_PROGRESS("جاري"),
    RESOLVED("تم الحل"),
    CLOSED("مغلق");

    companion object {
        fun fromString(str: String): IncidentStatus {
            return when {
                str.contains("حل") -> RESOLVED
                str.contains("مغلق") -> CLOSED
                else -> IN_PROGRESS
            }
        }
    }
}

enum class IncidentType(val displayNameAr: String) {
    TEACHER_TEACHER("مدرس / مدرس"),
    TEACHER_STUDENT("مدرس / طالب"),
    STUDENT_STUDENT("طالب / طالب"),
    PARENT("ولي أمر"),
    ADMIN_COMPLAINT("شكوى إدارية"),
    ABSENCE("غياب وحضور"),
    VIOLATION("مخالفة أمنية / انضباطية"),
    OTHER("أخرى");

    companion object {
        fun fromString(str: String): IncidentType {
            return when {
                str.contains("مدرس / مدرس") || str.contains("مدرس/مدرس") -> TEACHER_TEACHER
                str.contains("مدرس / طالب") || str.contains("مدرس/طالب") -> TEACHER_STUDENT
                str.contains("طالب / طالب") || str.contains("طالب/طالب") -> STUDENT_STUDENT
                str.contains("ولي أمر") || str.contains("أمر") -> PARENT
                str.contains("إدارية") || str.contains("شكوى") -> ADMIN_COMPLAINT
                str.contains("غياب") -> ABSENCE
                str.contains("مخالفة") -> VIOLATION
                else -> OTHER
            }
        }
    }
}

@Entity(tableName = "schools")
data class School(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val stage: SchoolStage,
    val classroomsCount: Int = 0,
    val staffCount: Int = 0,
    val principalName: String = "",
    val vicePrincipalName: String = "",
    val address: String = "",
    val phone: String = "",
    val securityOfficerName: String = "مستر مصطفى فرغلي",
    val notes: String = ""
)

@Entity(tableName = "teachers")
data class Teacher(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val schoolName: String,
    val stage: SchoolStage,
    val jobTitle: String = "معلم",
    val phone: String = "",
    val nationalId: String = "",
    val subject: String = "",
    val hireDate: String = "",
    val address: String = "",
    val notes: String = ""
)

@Entity(tableName = "incidents")
data class SecurityIncident(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val incidentType: IncidentType,
    val description: String,
    val schoolName: String,
    val partiesInvolved: String,
    val severity: SeverityLevel,
    val actionTaken: String = "",
    val status: IncidentStatus = IncidentStatus.IN_PROGRESS,
    val dateString: String = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date()),
    val timeString: String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
    val timestamp: Long = System.currentTimeMillis(),
    val supervisor: String = "مستر مصطفى فرغلي",
    val imageUri: String? = null
)

@Entity(tableName = "daily_logs")
data class DailyLogEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateString: String = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date()),
    val schoolName: String = "",
    val teacherName: String = "",
    val note: String,
    val supervisor: String = "مستر مصطفى فرغلي",
    val timestamp: Long = System.currentTimeMillis()
)
