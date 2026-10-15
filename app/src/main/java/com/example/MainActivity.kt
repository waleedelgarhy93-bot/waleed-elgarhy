package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.local.AppDatabase
import com.example.data.model.SchoolStage
import com.example.data.repository.SecurityRepository
import com.example.ui.navigation.Screen
import com.example.ui.screens.*
import com.example.ui.theme.EduBlue
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AuthState
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.SecurityViewModel
import com.example.ui.viewmodel.SecurityViewModelFactory

class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    private val securityViewModel: SecurityViewModel by viewModels {
        val database = AppDatabase.getDatabase(applicationContext, lifecycleScope)
        val repository = SecurityRepository(database)
        SecurityViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                MainAppContent(
                    authViewModel = authViewModel,
                    securityViewModel = securityViewModel
                )
            }
        }
    }
}

data class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val testTag: String
)

@Composable
fun MainAppContent(
    authViewModel: AuthViewModel,
    securityViewModel: SecurityViewModel
) {
    val navController = rememberNavController()
    val authState by authViewModel.authState.collectAsStateWithLifecycle()

    val schools by securityViewModel.filteredSchools.collectAsStateWithLifecycle()
    val allSchools by securityViewModel.allSchools.collectAsStateWithLifecycle()
    val teachers by securityViewModel.filteredTeachers.collectAsStateWithLifecycle()
    val allTeachers by securityViewModel.allTeachers.collectAsStateWithLifecycle()
    val incidents by securityViewModel.filteredIncidents.collectAsStateWithLifecycle()
    val allIncidents by securityViewModel.allIncidents.collectAsStateWithLifecycle()
    val allLogs by securityViewModel.allLogs.collectAsStateWithLifecycle()

    val schoolSearchQuery by securityViewModel.schoolSearchQuery.collectAsStateWithLifecycle()
    val selectedStageFilter by securityViewModel.selectedStageFilter.collectAsStateWithLifecycle()
    val teacherSearchQuery by securityViewModel.teacherSearchQuery.collectAsStateWithLifecycle()
    val incidentSearchQuery by securityViewModel.incidentSearchQuery.collectAsStateWithLifecycle()
    val incidentSeverityFilter by securityViewModel.incidentSeverityFilter.collectAsStateWithLifecycle()
    val incidentStatusFilter by securityViewModel.incidentStatusFilter.collectAsStateWithLifecycle()
    val importMessage by securityViewModel.importMessage.collectAsStateWithLifecycle()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavItems = listOf(
        BottomNavItem(
            route = Screen.Dashboard.route,
            title = "الرئيسية",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            testTag = "nav_home"
        ),
        BottomNavItem(
            route = Screen.Incidents.route,
            title = "الأحداث",
            selectedIcon = Icons.Filled.Warning,
            unselectedIcon = Icons.Outlined.WarningAmber,
            testTag = "nav_incidents"
        ),
        BottomNavItem(
            route = Screen.Reports.route,
            title = "التقارير",
            selectedIcon = Icons.Filled.Assessment,
            unselectedIcon = Icons.Outlined.Assessment,
            testTag = "nav_reports"
        ),
        BottomNavItem(
            route = Screen.Settings.route,
            title = "الإعدادات",
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings,
            testTag = "nav_settings"
        )
    )

    val showBottomBar = currentRoute in listOf(
        Screen.Dashboard.route,
        Screen.Incidents.route,
        Screen.Reports.route,
        Screen.Settings.route
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar && authState.isAuthenticated) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    bottomNavItems.forEach { item ->
                        val isSelected = currentRoute == item.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.title
                                )
                            },
                            label = {
                                Text(
                                    text = item.title,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = EduBlue,
                                selectedTextColor = EduBlue,
                                indicatorColor = EduBlue.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier.testTag(item.testTag)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Splash
            composable(Screen.Splash.route) {
                SplashScreen(
                    isAuthenticated = authState.isAuthenticated,
                    onNavigateNext = { isAuth ->
                        val target = if (isAuth) Screen.Dashboard.route else Screen.Login.route
                        navController.navigate(target) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            // Login
            composable(Screen.Login.route) {
                LoginScreen(
                    authState = authState,
                    onLoginClick = { u, p ->
                        authViewModel.login(u, p)
                    },
                    onQuickAdminClick = {
                        authViewModel.quickAdminLogin()
                    }
                )

                // If authenticated, navigate to dashboard
                LaunchedEffect(authState.isAuthenticated) {
                    if (authState.isAuthenticated) {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                }
            }

            // Dashboard
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    schools = allSchools,
                    teachers = allTeachers,
                    incidents = allIncidents,
                    adminName = authState.adminName,
                    onNavigateToStage = { stage ->
                        securityViewModel.setStageFilter(stage)
                        navController.navigate(Screen.Schools.createRoute(stage.name))
                    },
                    onNavigateToTeachers = {
                        securityViewModel.setStageFilter(null)
                        navController.navigate(Screen.Teachers.route)
                    },
                    onNavigateToIncidents = {
                        navController.navigate(Screen.Incidents.route)
                    },
                    onNavigateToReports = {
                        navController.navigate(Screen.Reports.route)
                    },
                    onNavigateToExcelImport = {
                        navController.navigate(Screen.ExcelImport.route)
                    },
                    onNavigateToAddIncident = {
                        navController.navigate(Screen.AddEditIncident.createRoute())
                    },
                    onNavigateToSettings = {
                        navController.navigate(Screen.Settings.route)
                    }
                )
            }

            // Schools Screen
            composable(
                route = Screen.Schools.route,
                arguments = listOf(
                    navArgument("stage") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val stageParam = backStackEntry.arguments?.getString("stage")
                LaunchedEffect(stageParam) {
                    if (stageParam != null) {
                        try {
                            securityViewModel.setStageFilter(SchoolStage.valueOf(stageParam))
                        } catch (e: Exception) {
                            securityViewModel.setStageFilter(null)
                        }
                    }
                }

                SchoolsScreen(
                    schools = schools,
                    searchQuery = schoolSearchQuery,
                    selectedStage = selectedStageFilter,
                    onSearchChange = { securityViewModel.setSchoolSearch(it) },
                    onStageSelect = { securityViewModel.setStageFilter(it) },
                    onSchoolClick = { schoolId ->
                        navController.navigate(Screen.SchoolDetail.createRoute(schoolId))
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            // School Detail Screen
            composable(
                route = Screen.SchoolDetail.route,
                arguments = listOf(
                    navArgument("schoolId") { type = NavType.LongType }
                )
            ) { backStackEntry ->
                val schoolId = backStackEntry.arguments?.getLong("schoolId") ?: 0L
                val currentSchool = allSchools.find { it.id == schoolId }

                SchoolDetailScreen(
                    school = currentSchool,
                    teachers = allTeachers,
                    incidents = allIncidents,
                    dailyLogs = allLogs,
                    onAddIncidentClick = { schoolName ->
                        navController.navigate(Screen.AddEditIncident.createRoute(schoolName = schoolName))
                    },
                    onTeacherClick = { teacherId ->
                        navController.navigate(Screen.TeacherDetail.createRoute(teacherId))
                    },
                    onIncidentClick = { incidentId ->
                        navController.navigate(Screen.IncidentDetail.createRoute(incidentId))
                    },
                    onAddDailyLog = { log ->
                        securityViewModel.addDailyLog(log)
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            // Teachers Screen
            composable(Screen.Teachers.route) {
                TeachersScreen(
                    teachers = teachers,
                    searchQuery = teacherSearchQuery,
                    selectedStage = selectedStageFilter,
                    onSearchChange = { securityViewModel.setTeacherSearch(it) },
                    onStageSelect = { securityViewModel.setStageFilter(it) },
                    onTeacherClick = { teacherId ->
                        navController.navigate(Screen.TeacherDetail.createRoute(teacherId))
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            // Teacher Detail Screen
            composable(
                route = Screen.TeacherDetail.route,
                arguments = listOf(
                    navArgument("teacherId") { type = NavType.LongType }
                )
            ) { backStackEntry ->
                val teacherId = backStackEntry.arguments?.getLong("teacherId") ?: 0L
                val currentTeacher = allTeachers.find { it.id == teacherId }

                TeacherDetailScreen(
                    teacher = currentTeacher,
                    incidents = allIncidents,
                    dailyLogs = allLogs,
                    onSchoolClick = { schoolName ->
                        val s = allSchools.find { it.name == schoolName }
                        if (s != null) {
                            navController.navigate(Screen.SchoolDetail.createRoute(s.id))
                        }
                    },
                    onIncidentClick = { incidentId ->
                        navController.navigate(Screen.IncidentDetail.createRoute(incidentId))
                    },
                    onAddDailyLog = { log ->
                        securityViewModel.addDailyLog(log)
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            // Incidents Screen
            composable(Screen.Incidents.route) {
                IncidentsScreen(
                    incidents = incidents,
                    searchQuery = incidentSearchQuery,
                    selectedSeverity = incidentSeverityFilter,
                    selectedStatus = incidentStatusFilter,
                    onSearchChange = { securityViewModel.setIncidentSearch(it) },
                    onSeverityFilter = { securityViewModel.setIncidentSeverityFilter(it) },
                    onStatusFilter = { securityViewModel.setIncidentStatusFilter(it) },
                    onIncidentClick = { incidentId ->
                        navController.navigate(Screen.IncidentDetail.createRoute(incidentId))
                    },
                    onAddIncidentClick = {
                        navController.navigate(Screen.AddEditIncident.createRoute())
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            // Incident Detail Screen
            composable(
                route = Screen.IncidentDetail.route,
                arguments = listOf(
                    navArgument("incidentId") { type = NavType.LongType }
                )
            ) { backStackEntry ->
                val incidentId = backStackEntry.arguments?.getLong("incidentId") ?: 0L
                val currentIncident = allIncidents.find { it.id == incidentId }

                IncidentDetailScreen(
                    incident = currentIncident,
                    onUpdateStatus = { inc, newStatus ->
                        securityViewModel.updateIncident(inc.copy(status = newStatus))
                    },
                    onDeleteIncident = { inc ->
                        securityViewModel.deleteIncident(inc)
                        navController.popBackStack()
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            // Add / Edit Incident Screen
            composable(
                route = Screen.AddEditIncident.route,
                arguments = listOf(
                    navArgument("schoolName") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    },
                    navArgument("incidentId") {
                        type = NavType.LongType
                        defaultValue = -1L
                    }
                )
            ) { backStackEntry ->
                val schoolNameParam = backStackEntry.arguments?.getString("schoolName")
                val incidentIdParam = backStackEntry.arguments?.getLong("incidentId") ?: -1L
                val existingIncident = if (incidentIdParam > 0) allIncidents.find { it.id == incidentIdParam } else null

                AddEditIncidentScreen(
                    initialSchoolName = schoolNameParam,
                    existingIncident = existingIncident,
                    schoolsList = allSchools,
                    onSaveIncident = { incident ->
                        if (incident.id > 0) {
                            securityViewModel.updateIncident(incident)
                        } else {
                            securityViewModel.addIncident(incident)
                        }
                        navController.popBackStack()
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            // Excel Import Screen
            composable(Screen.ExcelImport.route) {
                ExcelImportScreen(
                    importMessage = importMessage,
                    onImportSchools = { text ->
                        securityViewModel.importSchoolsFromText(text)
                    },
                    onImportTeachers = { text ->
                        securityViewModel.importTeachersFromText(text)
                    },
                    onClearMessage = {
                        securityViewModel.clearImportMessage()
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            // Reports Screen
            composable(Screen.Reports.route) {
                ReportsScreen(
                    incidents = allIncidents,
                    supervisorName = authState.adminName,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // Settings Screen
            composable(Screen.Settings.route) {
                SettingsScreen(
                    adminName = authState.adminName,
                    onResetData = {
                        securityViewModel.resetData()
                    },
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
