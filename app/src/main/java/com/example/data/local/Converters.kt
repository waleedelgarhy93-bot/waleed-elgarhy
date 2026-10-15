package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.IncidentStatus
import com.example.data.model.IncidentType
import com.example.data.model.SchoolStage
import com.example.data.model.SeverityLevel

class Converters {
    @TypeConverter
    fun fromSchoolStage(stage: SchoolStage): String = stage.name

    @TypeConverter
    fun toSchoolStage(value: String): SchoolStage = try {
        SchoolStage.valueOf(value)
    } catch (e: Exception) {
        SchoolStage.PRIMARY
    }

    @TypeConverter
    fun fromSeverityLevel(severity: SeverityLevel): String = severity.name

    @TypeConverter
    fun toSeverityLevel(value: String): SeverityLevel = try {
        SeverityLevel.valueOf(value)
    } catch (e: Exception) {
        SeverityLevel.LOW
    }

    @TypeConverter
    fun fromIncidentStatus(status: IncidentStatus): String = status.name

    @TypeConverter
    fun toIncidentStatus(value: String): IncidentStatus = try {
        IncidentStatus.valueOf(value)
    } catch (e: Exception) {
        IncidentStatus.IN_PROGRESS
    }

    @TypeConverter
    fun fromIncidentType(type: IncidentType): String = type.name

    @TypeConverter
    fun toIncidentType(value: String): IncidentType = try {
        IncidentType.valueOf(value)
    } catch (e: Exception) {
        IncidentType.OTHER
    }
}
