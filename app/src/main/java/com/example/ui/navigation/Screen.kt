package com.example.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Dashboard : Screen("dashboard")
    object Schools : Screen("schools?stage={stage}") {
        fun createRoute(stageName: String? = null): String {
            return if (stageName != null) "schools?stage=$stageName" else "schools"
        }
    }
    object SchoolDetail : Screen("school_detail/{schoolId}") {
        fun createRoute(schoolId: Long): String = "school_detail/$schoolId"
    }
    object Teachers : Screen("teachers")
    object TeacherDetail : Screen("teacher_detail/{teacherId}") {
        fun createRoute(teacherId: Long): String = "teacher_detail/$teacherId"
    }
    object Incidents : Screen("incidents")
    object IncidentDetail : Screen("incident_detail/{incidentId}") {
        fun createRoute(incidentId: Long): String = "incident_detail/$incidentId"
    }
    object AddEditIncident : Screen("add_incident?schoolName={schoolName}&incidentId={incidentId}") {
        fun createRoute(schoolName: String? = null, incidentId: Long? = null): String {
            val params = mutableListOf<String>()
            if (!schoolName.isNullOrBlank()) params.add("schoolName=$schoolName")
            if (incidentId != null) params.add("incidentId=$incidentId")
            return if (params.isNotEmpty()) "add_incident?${params.joinToString("&")}" else "add_incident"
        }
    }
    object ExcelImport : Screen("excel_import")
    object Reports : Screen("reports")
    object Settings : Screen("settings")
}
