package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyLogEntry
import com.example.data.model.IncidentStatus
import com.example.data.model.School
import com.example.data.model.SecurityIncident
import com.example.data.model.Teacher
import com.example.ui.theme.*
import com.example.util.WhatsAppHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolDetailScreen(
    school: School?,
    teachers: List<Teacher>,
    incidents: List<SecurityIncident>,
    dailyLogs: List<DailyLogEntry>,
    onAddIncidentClick: (String) -> Unit,
    onTeacherClick: (Long) -> Unit,
    onIncidentClick: (Long) -> Unit,
    onAddDailyLog: (DailyLogEntry) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) }
    var showAddLogDialog by remember { mutableStateOf(false) }
    var logNoteText by remember { mutableStateOf("") }

    if (school == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val schoolTeachers = teachers.filter { it.schoolName == school.name }
    val schoolIncidents = incidents.filter { it.schoolName == school.name }
    val ongoingIncidents = schoolIncidents.filter { it.status == IncidentStatus.IN_PROGRESS }
    val schoolLogs = dailyLogs.filter { it.schoolName == school.name }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = school.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "رجوع")
                    }
                },
                actions = {
                    if (school.phone.isNotBlank()) {
                        IconButton(onClick = { WhatsAppHelper.makePhoneCall(context, school.phone) }) {
                            Icon(Icons.Default.Phone, contentDescription = "اتصال بالمدرسة", tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = EduBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onAddIncidentClick(school.name) },
                containerColor = EduGreen,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.AddModerator, contentDescription = null) },
                text = { Text("تسجيل واقعة للمدرسة", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("fab_school_add_incident")
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // School Summary Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = school.stage.displayNameAr,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = EduBlue
                        )
                        Text(
                            text = school.address,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "مدير المدرسة:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = school.principalName.ifBlank { "غير مسجل" },
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "وكيل المدرسة:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = school.vicePrincipalName.ifBlank { "غير مسجل" },
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "عدد الفصول: ${school.classroomsCount} | عدد العاملين: ${school.staffCount}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "مسؤول الأمن: مستر مصطفى فرغلي",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = EduGreenDark
                        )
                    }
                }
            }

            // Tabs: العاملون | الأحداث الجارية | سجل الأحداث | الحدث اليومي
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = EduBlue
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("العاملون (${schoolTeachers.size})", style = MaterialTheme.typography.labelMedium) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            "الأحداث الجارية (${ongoingIncidents.size})",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (ongoingIncidents.isNotEmpty()) SeverityHigh else LocalContentColor.current
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("سجل الأحداث (${schoolIncidents.size})", style = MaterialTheme.typography.labelMedium) }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = { Text("الحدث اليومي (${schoolLogs.size})", style = MaterialTheme.typography.labelMedium) }
                )
            }

            // Tab Content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 70.dp)
            ) {
                when (selectedTab) {
                    0 -> {
                        // Staff / Teachers
                        if (schoolTeachers.isEmpty()) {
                            EmptyTabNotice("لا يوجد معلمون مسجلون في هذه المدرسة حتى الآن")
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(schoolTeachers, key = { it.id }) { teacher ->
                                    TeacherItemCompact(
                                        teacher = teacher,
                                        onClick = { onTeacherClick(teacher.id) },
                                        onWhatsAppClick = {
                                            WhatsAppHelper.openWhatsApp(
                                                context,
                                                teacher.phone,
                                                "السلام عليكم أ/ ${teacher.name}، معك إدارة أمن الإدارة التعليمية بسمالوط (مسؤول الأمن: مستر مصطفى فرغلي) بخصوص مدرسة ${school.name}."
                                            )
                                        },
                                        onCallClick = {
                                            WhatsAppHelper.makePhoneCall(context, teacher.phone)
                                        }
                                    )
                                }
                            }
                        }
                    }
                    1 -> {
                        // Ongoing Incidents
                        if (ongoingIncidents.isEmpty()) {
                            EmptyTabNotice("لا توجد أحداث جارية حالياً بالمدرسة - الحالة مستقرة والحمد لله")
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(ongoingIncidents, key = { it.id }) { incident ->
                                    RecentIncidentItem(
                                        incident = incident,
                                        onClick = { onIncidentClick(incident.id) }
                                    )
                                }
                            }
                        }
                    }
                    2 -> {
                        // All Incident History
                        if (schoolIncidents.isEmpty()) {
                            EmptyTabNotice("سجل أحداث هذه المدرسة نظيف تماماً")
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(schoolIncidents, key = { it.id }) { incident ->
                                    RecentIncidentItem(
                                        incident = incident,
                                        onClick = { onIncidentClick(incident.id) }
                                    )
                                }
                            }
                        }
                    }
                    3 -> {
                        // Daily Logs
                        Column(modifier = Modifier.fillMaxSize()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "ملاحظات المرور اليومي والأمني",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                TextButton(onClick = { showAddLogDialog = true }) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("إضافة ملاحظة")
                                }
                            }

                            if (schoolLogs.isEmpty()) {
                                EmptyTabNotice("لا توجد ملاحظات يومية مسجلة لهذه المدرسة")
                            } else {
                                LazyColumn(
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(schoolLogs, key = { it.id }) { log ->
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                        ) {
                                            Column(modifier = Modifier.padding(14.dp)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(
                                                        text = log.dateString,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = EduBlue
                                                    )
                                                    Text(
                                                        text = log.supervisor,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Text(
                                                    text = log.note,
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Log Dialog
    if (showAddLogDialog) {
        AlertDialog(
            onDismissRequest = { showAddLogDialog = false },
            title = { Text("تسجيل ملاحظة مرور أمني يومي") },
            text = {
                OutlinedTextField(
                    value = logNoteText,
                    onValueChange = { logNoteText = it },
                    label = { Text("نص الملاحظة اليومية للمدرسة") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (logNoteText.isNotBlank()) {
                            onAddDailyLog(
                                DailyLogEntry(
                                    schoolName = school.name,
                                    note = logNoteText.trim(),
                                    supervisor = "مستر مصطفى فرغلي"
                                )
                            )
                            logNoteText = ""
                            showAddLogDialog = false
                        }
                    }
                ) {
                    Text("حفظ الملاحظة")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddLogDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@Composable
fun TeacherItemCompact(
    teacher: Teacher,
    onClick: () -> Unit,
    onWhatsAppClick: () -> Unit,
    onCallClick: () -> Unit
) {
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
                    .size(40.dp)
                    .background(EduBlue.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = EduBlue)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = teacher.name,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "${teacher.jobTitle} • ${teacher.subject}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            // WhatsApp button
            IconButton(onClick = onWhatsAppClick) {
                Icon(
                    imageVector = Icons.Default.Chat,
                    contentDescription = "مراسلة واتساب",
                    tint = EduGreen
                )
            }
            // Call button
            IconButton(onClick = onCallClick) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "اتصال",
                    tint = EduBlue
                )
            }
        }
    }
}

@Composable
fun EmptyTabNotice(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
