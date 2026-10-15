package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.IncidentStatus
import com.example.data.model.School
import com.example.data.model.SchoolStage
import com.example.data.model.SecurityIncident
import com.example.data.model.Teacher
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    schools: List<School>,
    teachers: List<Teacher>,
    incidents: List<SecurityIncident>,
    adminName: String,
    onNavigateToStage: (SchoolStage) -> Unit,
    onNavigateToTeachers: () -> Unit,
    onNavigateToIncidents: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToExcelImport: () -> Unit,
    onNavigateToAddIncident: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val primaryCount = schools.count { it.stage == SchoolStage.PRIMARY }
    val prepCount = schools.count { it.stage == SchoolStage.PREPARATORY }
    val secCount = schools.count { it.stage == SchoolStage.SECONDARY }
    val inProgressIncidentsCount = incidents.count { it.status == IncidentStatus.IN_PROGRESS }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToAddIncident,
                containerColor = EduGreen,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.AddModerator, contentDescription = null) },
                text = { Text("تسجيل واقعة جديدة", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("fab_add_incident")
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingPadding(bottom = 80.dp)
        ) {
            // Header Banner
            item {
                DashboardHeader(
                    adminName = adminName,
                    totalSchools = schools.size,
                    totalTeachers = teachers.size,
                    inProgressIncidents = inProgressIncidentsCount
                )
            }

            // Quick Actions & Stage Cards Section
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Text(
                        text = "المنظومة التعليمية والأقسام الأمنية",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
                    )

                    // Grid-like rows of M3 Cards
                    // Row 1: Primary & Preparatory Stages
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DashboardCard(
                            title = "المرحلة الابتدائية",
                            subtitle = "$primaryCount مدرسة مسجلة",
                            badgeText = "ابتدائي",
                            icon = Icons.Default.ChildCare,
                            accentColor = Color(0xFF00897B),
                            modifier = Modifier.weight(1f),
                            testTag = "card_primary_stage",
                            onClick = { onNavigateToStage(SchoolStage.PRIMARY) }
                        )

                        DashboardCard(
                            title = "المرحلة الإعدادية",
                            subtitle = "$prepCount مدرسة مسجلة",
                            badgeText = "إعدادي",
                            icon = Icons.Default.MenuBook,
                            accentColor = EduBlue,
                            modifier = Modifier.weight(1f),
                            testTag = "card_prep_stage",
                            onClick = { onNavigateToStage(SchoolStage.PREPARATORY) }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Row 2: Secondary Stage & Teachers/Staff
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DashboardCard(
                            title = "المرحلة الثانوية",
                            subtitle = "$secCount مدرسة مسجلة",
                            badgeText = "ثانوي",
                            icon = Icons.Default.AccountBalance,
                            accentColor = Color(0xFF6A1B9A),
                            modifier = Modifier.weight(1f),
                            testTag = "card_secondary_stage",
                            onClick = { onNavigateToStage(SchoolStage.SECONDARY) }
                        )

                        DashboardCard(
                            title = "المعلمين والموظفين",
                            subtitle = "${teachers.size} كادر تعليمي",
                            badgeText = "واتساب 🟢",
                            icon = Icons.Default.Groups,
                            accentColor = EduGreen,
                            modifier = Modifier.weight(1f),
                            testTag = "card_teachers",
                            onClick = onNavigateToTeachers
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Row 3: Incidents & Reports
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DashboardCard(
                            title = "الأحداث والوقائع",
                            subtitle = "${incidents.size} واقعة مسجلة",
                            badgeText = if (inProgressIncidentsCount > 0) "$inProgressIncidentsCount جاري" else "مستقرة",
                            badgeColor = if (inProgressIncidentsCount > 0) SeverityHigh else EduGreen,
                            icon = Icons.Default.WarningAmber,
                            accentColor = if (inProgressIncidentsCount > 0) SeverityHigh else EduBlue,
                            modifier = Modifier.weight(1f),
                            testTag = "card_incidents",
                            onClick = onNavigateToIncidents
                        )

                        DashboardCard(
                            title = "التقارير والإحصائيات",
                            subtitle = "يومي / شهري / PDF",
                            badgeText = "تصدير",
                            icon = Icons.Default.Assessment,
                            accentColor = EduGoldDark,
                            modifier = Modifier.weight(1f),
                            testTag = "card_reports",
                            onClick = onNavigateToReports
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Full-width Excel Import Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToExcelImport() }
                            .testTag("card_excel_import"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .background(EduGreenLight, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TableChart,
                                    contentDescription = null,
                                    tint = EduGreen,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "استيراد بيانات Excel",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        color = EduGreen,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "XLSX / CSV",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "استيراد ملفات المعلمين والمدارس وتحديث السجلات دفعة واحدة",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ChevronLeft,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Recent Incidents Quick Section
            if (incidents.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "أحدث الوقائع المسجلة",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            TextButton(onClick = onNavigateToIncidents) {
                                Text("عرض الكل (${incidents.size})")
                            }
                        }

                        incidents.take(3).forEach { incident ->
                            RecentIncidentItem(incident = incident, onClick = onNavigateToIncidents)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardHeader(
    adminName: String,
    totalSchools: Int,
    totalTeachers: Int,
    inProgressIncidents: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = EduBlueDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(EduBlueDark, EduBlue)
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(EduGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = EduBlueDark,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "إدارة أمن الإدارة التعليمية بسمالوط",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "المسؤول: $adminName",
                        style = MaterialTheme.typography.bodySmall,
                        color = EduGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatBadge(
                    count = totalSchools.toString(),
                    label = "المدارس",
                    icon = Icons.Default.School
                )
                StatBadge(
                    count = totalTeachers.toString(),
                    label = "العاملون",
                    icon = Icons.Default.People
                )
                StatBadge(
                    count = inProgressIncidents.toString(),
                    label = "قيد الإجراء",
                    icon = Icons.Default.PendingActions,
                    accentColor = if (inProgressIncidents > 0) EduGold else Color.White
                )
            }
        }
    }
}

@Composable
fun StatBadge(
    count: String,
    label: String,
    icon: ImageVector,
    accentColor: Color = Color.White
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = count,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = accentColor
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun DashboardCard(
    title: String,
    subtitle: String,
    badgeText: String,
    badgeColor: Color = EduBlue,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(accentColor.copy(alpha = 0.12f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Surface(
                    color = badgeColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = badgeText,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = badgeColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun RecentIncidentItem(
    incident: SecurityIncident,
    onClick: () -> Unit
) {
    val severityColor = when (incident.severity.name) {
        "CRITICAL" -> SeverityCritical
        "HIGH" -> SeverityHigh
        "MEDIUM" -> SeverityMedium
        else -> SeverityLow
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(40.dp)
                    .background(severityColor, RoundedCornerShape(2.dp))
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = incident.title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = "${incident.schoolName} • ${incident.dateString}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Surface(
                color = severityColor.copy(alpha = 0.12f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = incident.severity.displayNameAr,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = severityColor,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

private fun PaddingPadding(bottom: androidx.compose.ui.unit.Dp): PaddingValues =
    PaddingValues(bottom = bottom)
