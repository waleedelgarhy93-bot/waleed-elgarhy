package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.populateInitialData
import com.example.data.model.DailyLogEntry
import com.example.data.model.School
import com.example.data.model.SchoolStage
import com.example.data.model.SecurityIncident
import com.example.data.model.Teacher
import kotlinx.coroutines.flow.Flow

class SecurityRepository(private val database: AppDatabase) {

    private val schoolDao = database.schoolDao()
    private val teacherDao = database.teacherDao()
    private val incidentDao = database.securityIncidentDao()
    private val dailyLogDao = database.dailyLogDao()

    // Schools
    val allSchools: Flow<List<School>> = schoolDao.getAllSchools()
    fun getSchoolsByStage(stage: SchoolStage): Flow<List<School>> = schoolDao.getSchoolsByStage(stage)
    suspend fun getSchoolById(id: Long): School? = schoolDao.getSchoolById(id)
    suspend fun insertSchool(school: School): Long = schoolDao.insertSchool(school)
    suspend fun insertSchools(schools: List<School>) = schoolDao.insertSchools(schools)
    suspend fun updateSchool(school: School) = schoolDao.updateSchool(school)
    suspend fun deleteSchool(school: School) = schoolDao.deleteSchool(school)

    // Teachers
    val allTeachers: Flow<List<Teacher>> = teacherDao.getAllTeachers()
    fun getTeachersBySchool(schoolName: String): Flow<List<Teacher>> = teacherDao.getTeachersBySchool(schoolName)
    fun getTeachersByStage(stage: SchoolStage): Flow<List<Teacher>> = teacherDao.getTeachersByStage(stage)
    suspend fun getTeacherById(id: Long): Teacher? = teacherDao.getTeacherById(id)
    suspend fun insertTeacher(teacher: Teacher): Long = teacherDao.insertTeacher(teacher)
    suspend fun insertTeachers(teachers: List<Teacher>) = teacherDao.insertTeachers(teachers)
    suspend fun updateTeacher(teacher: Teacher) = teacherDao.updateTeacher(teacher)
    suspend fun deleteTeacher(teacher: Teacher) = teacherDao.deleteTeacher(teacher)

    // Incidents
    val allIncidents: Flow<List<SecurityIncident>> = incidentDao.getAllIncidents()
    fun getIncidentsBySchool(schoolName: String): Flow<List<SecurityIncident>> = incidentDao.getIncidentsBySchool(schoolName)
    fun getIncidentsByParty(name: String): Flow<List<SecurityIncident>> = incidentDao.getIncidentsByParty(name)
    suspend fun getIncidentById(id: Long): SecurityIncident? = incidentDao.getIncidentById(id)
    suspend fun insertIncident(incident: SecurityIncident): Long = incidentDao.insertIncident(incident)
    suspend fun updateIncident(incident: SecurityIncident) = incidentDao.updateIncident(incident)
    suspend fun deleteIncident(incident: SecurityIncident) = incidentDao.deleteIncident(incident)

    // Daily Logs
    val allLogs: Flow<List<DailyLogEntry>> = dailyLogDao.getAllLogs()
    fun getLogsBySchool(schoolName: String): Flow<List<DailyLogEntry>> = dailyLogDao.getLogsBySchool(schoolName)
    fun getLogsByTeacher(teacherName: String): Flow<List<DailyLogEntry>> = dailyLogDao.getLogsByTeacher(teacherName)
    suspend fun insertDailyLog(log: DailyLogEntry): Long = dailyLogDao.insertLog(log)
    suspend fun deleteDailyLog(log: DailyLogEntry) = dailyLogDao.deleteLog(log)

    // Reset Data
    suspend fun resetAllData() {
        database.clearAllTables()
        populateInitialData(database)
    }
}
