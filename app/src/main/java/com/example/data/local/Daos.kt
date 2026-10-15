package com.example.data.local

import androidx.room.*
import com.example.data.model.DailyLogEntry
import com.example.data.model.School
import com.example.data.model.SchoolStage
import com.example.data.model.SecurityIncident
import com.example.data.model.Teacher
import kotlinx.coroutines.flow.Flow

@Dao
interface SchoolDao {
    @Query("SELECT * FROM schools ORDER BY name ASC")
    fun getAllSchools(): Flow<List<School>>

    @Query("SELECT * FROM schools WHERE stage = :stage ORDER BY name ASC")
    fun getSchoolsByStage(stage: SchoolStage): Flow<List<School>>

    @Query("SELECT * FROM schools WHERE id = :id LIMIT 1")
    suspend fun getSchoolById(id: Long): School?

    @Query("SELECT * FROM schools WHERE name = :name LIMIT 1")
    suspend fun getSchoolByName(name: String): School?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchool(school: School): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchools(schools: List<School>)

    @Update
    suspend fun updateSchool(school: School)

    @Delete
    suspend fun deleteSchool(school: School)

    @Query("SELECT COUNT(*) FROM schools")
    suspend fun getCount(): Int

    @Query("DELETE FROM schools")
    suspend fun deleteAll()
}

@Dao
interface TeacherDao {
    @Query("SELECT * FROM teachers ORDER BY name ASC")
    fun getAllTeachers(): Flow<List<Teacher>>

    @Query("SELECT * FROM teachers WHERE schoolName = :schoolName ORDER BY name ASC")
    fun getTeachersBySchool(schoolName: String): Flow<List<Teacher>>

    @Query("SELECT * FROM teachers WHERE stage = :stage ORDER BY name ASC")
    fun getTeachersByStage(stage: SchoolStage): Flow<List<Teacher>>

    @Query("SELECT * FROM teachers WHERE id = :id LIMIT 1")
    suspend fun getTeacherById(id: Long): Teacher?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeacher(teacher: Teacher): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeachers(teachers: List<Teacher>)

    @Update
    suspend fun updateTeacher(teacher: Teacher)

    @Delete
    suspend fun deleteTeacher(teacher: Teacher)

    @Query("SELECT COUNT(*) FROM teachers")
    suspend fun getCount(): Int

    @Query("DELETE FROM teachers")
    suspend fun deleteAll()
}

@Dao
interface SecurityIncidentDao {
    @Query("SELECT * FROM incidents ORDER BY timestamp DESC")
    fun getAllIncidents(): Flow<List<SecurityIncident>>

    @Query("SELECT * FROM incidents WHERE schoolName = :schoolName ORDER BY timestamp DESC")
    fun getIncidentsBySchool(schoolName: String): Flow<List<SecurityIncident>>

    @Query("SELECT * FROM incidents WHERE partiesInvolved LIKE '%' || :name || '%' OR description LIKE '%' || :name || '%' ORDER BY timestamp DESC")
    fun getIncidentsByParty(name: String): Flow<List<SecurityIncident>>

    @Query("SELECT * FROM incidents WHERE id = :id LIMIT 1")
    suspend fun getIncidentById(id: Long): SecurityIncident?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncident(incident: SecurityIncident): Long

    @Update
    suspend fun updateIncident(incident: SecurityIncident)

    @Delete
    suspend fun deleteIncident(incident: SecurityIncident)

    @Query("SELECT COUNT(*) FROM incidents")
    suspend fun getCount(): Int

    @Query("DELETE FROM incidents")
    suspend fun deleteAll()
}

@Dao
interface DailyLogDao {
    @Query("SELECT * FROM daily_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<DailyLogEntry>>

    @Query("SELECT * FROM daily_logs WHERE schoolName = :schoolName ORDER BY timestamp DESC")
    fun getLogsBySchool(schoolName: String): Flow<List<DailyLogEntry>>

    @Query("SELECT * FROM daily_logs WHERE teacherName = :teacherName ORDER BY timestamp DESC")
    fun getLogsByTeacher(teacherName: String): Flow<List<DailyLogEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: DailyLogEntry): Long

    @Delete
    suspend fun deleteLog(log: DailyLogEntry)
}
