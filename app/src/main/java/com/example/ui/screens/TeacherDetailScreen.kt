package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.example.data.model.SecurityIncident
import com.example.data.model.Teacher
import com.example.ui.theme.*
import com.example.util.WhatsAppHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherDetailScreen(
    teacher: Teacher?,
    incidents: List<SecurityIncident>,
    dailyLogs: List<DailyLogEntry>,
    onSchoolClick: (String) -> Unit,
    onIncidentClick: (Long) -> Unit,
    onAddDailyLog: (DailyLogEntry) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var showAddLogDialog by remember { mutableStateOf(false) }
    var noteText by remember { mutableStateOf("") }

    if (teacher == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val linkedIncidents = incidents.filter {
        it.partiesInvolved.contains(teacher.name) ||
                it.description.contains(teacher.name) ||
                it.schoolName == teacher.schoolName
    }
    val teacherLogs = dailyLogs.filter { it.teacherName == teacher.name }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "بيانات المعلم / الموظف",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "رجوع")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = EduBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .background(EduBlue.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = EduBlue,
                                modifier = Modifier.size(42.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = teacher.name,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "${teacher.jobTitle} • ${teacher.subject}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Communication Buttons: WhatsApp 🟢 + Call 📞
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = {
                                    WhatsAppHelper.openWhatsApp(
                                        context,
                                        teacher.phone,
                                        "السلام عليكم أ/ ${teacher.name}، معك إدارة أمن الإدارة التعليمية بسمالوط (مسؤول الأمن: مستر مصطفى فرغلي) بخصوص متابعة العمل المدرسي."
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("teacher_detail_whatsapp_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = EduGreen),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Chat, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("واتساب 🟢", fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = { WhatsAppHelper.makePhoneCall(context, teacher.phone) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("teacher_detail_call_button"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = null, tint = EduBlue)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("اتصال 📞", fontWeight = FontWeight.Bold, color = EduBlue)
                            }
                        }
                    }
                }
            }

            // Information Grid
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "البيانات الوظيفية والشخصية",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = EduBlue,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        InfoRow(icon = Icons.Default.School, label = "المدرسة", value = teacher.schoolName, isClickable = true, onClick = { onSchoolClick(teacher.schoolName) })
                        InfoRow(icon = Icons.Default.Category, label = "المرحلة", value = teacher.stage.displayNameAr)
                        InfoRow(icon = Icons.Default.Work, label = "الوظيفة الحالية", value = teacher.jobTitle)
                        InfoRow(icon = Icons.Default.MenuBook, label = "التخصص / المادة", value = teacher.subject)
                        InfoRow(icon = Icons.Default.Phone, label = "رقم الهاتف", value = teacher.phone.ifBlank { "غير مسجل" })
                        InfoRow(icon = Icons.Default.Badge, label = "الرقم القومي", value = teacher.nationalId.ifBlank { "غير مسجل" })
                        InfoRow(icon = Icons.Default.CalendarToday, label = "تاريخ التعيين", value = teacher.hireDate.ifBlank { "غير مسجل" })
                        if (teacher.address.isNotBlank()) {
                            InfoRow(icon = Icons.Default.Home, label = "محل الإقامة", value = teacher.address)
                        }
                    }
                }
            }

            // Linked Incidents
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "الأحداث والوقائع المرتبطة (${linkedIncidents.size})",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    if (linkedIncidents.isEmpty()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Text(
                                text = "لا توجد وقائع مسجلة مرتبطة بالمعلم - السجل نظيف تماماً",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    } else {
                        linkedIncidents.forEach { incident ->
                            RecentIncidentItem(incident = incident, onClick = { onIncidentClick(incident.id) })
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }

            // Daily Logs
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "سجل الملاحظات اليومية (${teacherLogs.size})",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        TextButton(onClick = { showAddLogDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("إضافة ملاحظة")
                        }
                    }

                    if (teacherLogs.isEmpty()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Text(
                                text = "لا توجد ملاحظات مرور أمني مسجلة للمعلم",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    } else {
                        teacherLogs.forEach { log ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = log.dateString, style = MaterialTheme.typography.labelSmall, color = EduBlue)
                                        Text(text = log.supervisor, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = log.note, style = MaterialTheme.typography.bodyMedium)
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
            title = { Text("إضافة ملاحظة للمعلم") },
            text = {
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text("الملاحظة الأمنية أو الإدارية") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (noteText.isNotBlank()) {
                            onAddDailyLog(
                                DailyLogEntry(
                                    schoolName = teacher.schoolName,
                                    teacherName = teacher.name,
                                    note = noteText.trim(),
                                    supervisor = "مستر مصطفى فرغلي"
                                )
                            )
                            noteText = ""
                            showAddLogDialog = false
                        }
                    }
                ) {
                    Text("حفظ")
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
fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    isClickable: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .then(if (isClickable && onClick != null) Modifier.clickable { onClick() } else Modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = EduBlue,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isClickable) FontWeight.Bold else FontWeight.Medium,
                color = if (isClickable) EduBlue else MaterialTheme.colorScheme.onSurface
            )
        )
    }
}
