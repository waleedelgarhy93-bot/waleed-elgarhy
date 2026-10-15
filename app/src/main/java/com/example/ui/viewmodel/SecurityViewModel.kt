package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.DailyLogEntry
import com.example.data.model.IncidentStatus
import com.example.data.model.IncidentType
import com.example.data.model.School
import com.example.data.model.SchoolStage
import com.example.data.model.SecurityIncident
import com.example.data.model.SeverityLevel
import com.example.data.model.Teacher
import com.example.data.repository.SecurityRepository
import com.example.util.ExcelHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SecurityViewModel(
    private val repository: SecurityRepository
) : ViewModel() {

    val allSchools: StateFlow<List<School>> = repository.allSchools
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTeachers: StateFlow<List<Teacher>> = repository.allTeachers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allIncidents: StateFlow<List<SecurityIncident>> = repository.allIncidents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allLogs: StateFlow<List<DailyLogEntry>> = repository.allLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filters & Search
    private val _schoolSearchQuery = MutableStateFlow("")
    val schoolSearchQuery: StateFlow<String> = _schoolSearchQuery.asStateFlow()

    private val _selectedStageFilter = MutableStateFlow<SchoolStage?>(null)
    val selectedStageFilter: StateFlow<SchoolStage?> = _selectedStageFilter.asStateFlow()

    private val _teacherSearchQuery = MutableStateFlow("")
    val teacherSearchQuery: StateFlow<String> = _teacherSearchQuery.asStateFlow()

    private val _incidentSearchQuery = MutableStateFlow("")
    val incidentSearchQuery: StateFlow<String> = _incidentSearchQuery.asStateFlow()

    private val _incidentSeverityFilter = MutableStateFlow<SeverityLevel?>(null)
    val incidentSeverityFilter: StateFlow<SeverityLevel?> = _incidentSeverityFilter.asStateFlow()

    private val _incidentStatusFilter = MutableStateFlow<IncidentStatus?>(null)
    val incidentStatusFilter: StateFlow<IncidentStatus?> = _incidentStatusFilter.asStateFlow()

    // Import Status Message
    private val _importMessage = MutableStateFlow<String?>(null)
    val importMessage: StateFlow<String?> = _importMessage.asStateFlow()

    // Filtered lists
    val filteredSchools: StateFlow<List<School>> = combine(
        allSchools,
        _schoolSearchQuery,
        _selectedStageFilter
    ) { list, query, stage ->
        list.filter { school ->
            val matchesQuery = query.isBlank() ||
                    school.name.contains(query, ignoreCase = true) ||
                    school.principalName.contains(query, ignoreCase = true) ||
                    school.address.contains(query, ignoreCase = true)
            val matchesStage = stage == null || school.stage == stage
            matchesQuery && matchesStage
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredTeachers: StateFlow<List<Teacher>> = combine(
        allTeachers,
        _teacherSearchQuery,
        _selectedStageFilter
    ) { list, query, stage ->
        list.filter { teacher ->
            val matchesQuery = query.isBlank() ||
                    teacher.name.contains(query, ignoreCase = true) ||
                    teacher.schoolName.contains(query, ignoreCase = true) ||
                    teacher.subject.contains(query, ignoreCase = true) ||
                    teacher.phone.contains(query) ||
                    teacher.nationalId.contains(query)
            val matchesStage = stage == null || teacher.stage == stage
            matchesQuery && matchesStage
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredIncidents: StateFlow<List<SecurityIncident>> = combine(
        allIncidents,
        _incidentSearchQuery,
        _incidentSeverityFilter,
        _incidentStatusFilter
    ) { list, query, severity, status ->
        list.filter { incident ->
            val matchesQuery = query.isBlank() ||
                    incident.title.contains(query, ignoreCase = true) ||
                    incident.schoolName.contains(query, ignoreCase = true) ||
                    incident.description.contains(query, ignoreCase = true) ||
                    incident.partiesInvolved.contains(query, ignoreCase = true) ||
                    incident.incidentType.displayNameAr.contains(query, ignoreCase = true)
            val matchesSeverity = severity == null || incident.severity == severity
            val matchesStatus = status == null || incident.status == status
            matchesQuery && matchesSeverity && matchesStatus
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSchoolSearch(query: String) { _schoolSearchQuery.value = query }
    fun setStageFilter(stage: SchoolStage?) { _selectedStageFilter.value = stage }
    fun setTeacherSearch(query: String) { _teacherSearchQuery.value = query }
    fun setIncidentSearch(query: String) { _incidentSearchQuery.value = query }
    fun setIncidentSeverityFilter(severity: SeverityLevel?) { _incidentSeverityFilter.value = severity }
    fun setIncidentStatusFilter(status: IncidentStatus?) { _incidentStatusFilter.value = status }
    fun clearImportMessage() { _importMessage.value = null }

    // Actions
    fun addSchool(school: School) = viewModelScope.launch { repository.insertSchool(school) }
    fun updateSchool(school: School) = viewModelScope.launch { repository.updateSchool(school) }
    fun deleteSchool(school: School) = viewModelScope.launch { repository.deleteSchool(school) }

    fun addTeacher(teacher: Teacher) = viewModelScope.launch { repository.insertTeacher(teacher) }
    fun updateTeacher(teacher: Teacher) = viewModelScope.launch { repository.updateTeacher(teacher) }
    fun deleteTeacher(teacher: Teacher) = viewModelScope.launch { repository.deleteTeacher(teacher) }

    fun addIncident(incident: SecurityIncident) = viewModelScope.launch { repository.insertIncident(incident) }
    fun updateIncident(incident: SecurityIncident) = viewModelScope.launch { repository.updateIncident(incident) }
    fun deleteIncident(incident: SecurityIncident) = viewModelScope.launch { repository.deleteIncident(incident) }

    fun addDailyLog(log: DailyLogEntry) = viewModelScope.launch { repository.insertDailyLog(log) }
    fun deleteDailyLog(log: DailyLogEntry) = viewModelScope.launch { repository.deleteDailyLog(log) }

    fun importSchoolsFromText(text: String) {
        viewModelScope.launch {
            val result = ExcelHelper.parseSchoolsFromText(text)
            if (result.items.isNotEmpty()) {
                repository.insertSchools(result.items)
                _importMessage.value = "تم استيراد ${result.items.size} مدرسة بنجاح من أصل ${result.totalProcessed} صف!"
            } else {
                _importMessage.value = "لم يتم العثور على بيانات مدارس صالحة للاستيراد."
            }
        }
    }

    fun importTeachersFromText(text: String) {
        viewModelScope.launch {
            val result = ExcelHelper.parseTeachersFromText(text)
            if (result.items.isNotEmpty()) {
                repository.insertTeachers(result.items)
                _importMessage.value = "تم استيراد ${result.items.size} معلم وموظف بنجاح من أصل ${result.totalProcessed} صف!"
            } else {
                _importMessage.value = "لم يتم العثور على بيانات معلمين صالحة للاستيراد."
            }
        }
    }

    fun resetData() = viewModelScope.launch {
        repository.resetAllData()
        _importMessage.value = "تمت استعادة البيانات الافتراضية لإدارة سمالوط بنجاح."
    }
}

class SecurityViewModelFactory(private val repository: SecurityRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SecurityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SecurityViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
