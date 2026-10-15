package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.IncidentStatus
import com.example.data.model.SecurityIncident
import com.example.data.model.SeverityLevel
import com.example.ui.theme.*
import com.example.util.WhatsAppHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidentDetailScreen(
    incident: SecurityIncident?,
    onUpdateStatus: (SecurityIncident, IncidentStatus) -> Unit,
    onDeleteIncident: (SecurityIncident) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var showDeleteConfirm by remember { mutableStateOf(false) }

    if (incident == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val severityColor = when (incident.severity) {
        SeverityLevel.CRITICAL -> SeverityCritical
        SeverityLevel.HIGH -> SeverityHigh
        SeverityLevel.MEDIUM -> SeverityMedium
        SeverityLevel.LOW -> SeverityLow
    }

    val statusColor = when (incident.status) {
        IncidentStatus.IN_PROGRESS -> StatusInProgress
        IncidentStatus.RESOLVED -> StatusResolved
        IncidentStatus.CLOSED -> StatusClosed
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "تفاصيل الواقعة الأمنية",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "رجوع")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val text = """
                                🚨 *إخطار أمني - إدارة سمالوط التعليمية*
                                • الواقعة: ${incident.title}
                                • المدرسة: ${incident.schoolName}
                                • النوع: ${incident.incidentType.displayNameAr}
                                • الخطورة: ${incident.severity.displayNameAr}
                                • الأطراف: ${incident.partiesInvolved}
                                • الإجراء المتخذ: ${incident.actionTaken}
                                • التاريخ: ${incident.dateString} ${incident.timeString}
                                • مسؤول الأمن: ${incident.supervisor}
                            """.trimIndent()
                            WhatsAppHelper.shareTextToWhatsApp(context, text)
                        }
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "مشاركة للواتساب", tint = Color.White)
                    }
                    IconButton(onClick = { showDeleteConfirm = true }) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "حذف الواقعة", tint = Color.White)
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Badges Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = severityColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "درجة الخطورة: ${incident.severity.displayNameAr}",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = severityColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }

                Surface(
                    color = statusColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "الحالة: ${incident.status.displayNameAr}",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            // Title & Core Data Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = incident.title,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    InfoRow(icon = Icons.Default.School, label = "المدرسة", value = incident.schoolName)
                    InfoRow(icon = Icons.Default.Category, label = "تصنيف الواقعة", value = incident.incidentType.displayNameAr)
                    if (incident.partiesInvolved.isNotBlank()) {
                        InfoRow(icon = Icons.Default.Groups, label = "الأطراف المشاركة", value = incident.partiesInvolved)
                    }
                    InfoRow(icon = Icons.Default.CalendarToday, label = "التاريخ والوقت", value = "${incident.dateString} الساعة ${incident.timeString}")
                    InfoRow(icon = Icons.Default.VerifiedUser, label = "مسؤول الأمن المتابع", value = incident.supervisor)
                }
            }

            // Description Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "وصف وملابسات الواقعة",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = EduBlue,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = incident.description,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp
                    )
                }
            }

            // Action Taken Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "الإجراء المتخذ من إدارة الأمن",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = EduGreen,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = incident.actionTaken.ifBlank { "قيد التحقيق والمتابعة الأمنية" },
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp
                    )
                }
            }

            // Image Preview (if present)
            if (!incident.imageUri.isNullOrBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "صورة المعاينة والمستندات",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                        ) {
                            AsyncImage(
                                model = incident.imageUri,
                                contentDescription = "معاينة الواقعة",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }

            // Status Change Actions
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "تحديث حالة الواقعة:",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onUpdateStatus(incident, IncidentStatus.RESOLVED) },
                            colors = ButtonDefaults.buttonColors(containerColor = EduGreen),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            enabled = incident.status != IncidentStatus.RESOLVED
                        ) {
                            Text("تم الحل ✅", style = MaterialTheme.typography.labelSmall)
                        }

                        Button(
                            onClick = { onUpdateStatus(incident, IncidentStatus.IN_PROGRESS) },
                            colors = ButtonDefaults.buttonColors(containerColor = EduBlue),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            enabled = incident.status != IncidentStatus.IN_PROGRESS
                        ) {
                            Text("جاري ⏳", style = MaterialTheme.typography.labelSmall)
                        }

                        Button(
                            onClick = { onUpdateStatus(incident, IncidentStatus.CLOSED) },
                            colors = ButtonDefaults.buttonColors(containerColor = StatusClosed),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            enabled = incident.status != IncidentStatus.CLOSED
                        ) {
                            Text("إغلاق 📁", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }

            // Share to WhatsApp Button
            Button(
                onClick = {
                    val text = """
                        🚨 *إدارة أمن الإدارة التعليمية بسمالوط*
                        📋 *إخطار واقعة:* ${incident.title}
                        🏫 *المدرسة:* ${incident.schoolName}
                        ⚠️ *الخطورة:* ${incident.severity.displayNameAr} | *الحالة:* ${incident.status.displayNameAr}
                        👥 *الأطراف:* ${incident.partiesInvolved}
                        📝 *الوصف:* ${incident.description}
                        ⚖️ *الإجراء:* ${incident.actionTaken}
                        📅 *التاريخ:* ${incident.dateString} ${incident.timeString}
                        ✍️ *مسؤول الأمن:* ${incident.supervisor}
                    """.trimIndent()
                    WhatsAppHelper.shareTextToWhatsApp(context, text)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("incident_detail_whatsapp_share"),
                colors = ButtonDefaults.buttonColors(containerColor = EduGreen),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Chat, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("مشاركة تفاصيل الواقعة عبر واتساب 🟢", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("تأكيد حذف الواقعة") },
            text = { Text("هل أنت متأكد من رغبتك في حذف واقعة: '${incident.title}'؟") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        onDeleteIncident(incident)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("حذف نهائياً")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}
