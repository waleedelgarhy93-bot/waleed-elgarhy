package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.*
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditIncidentScreen(
    initialSchoolName: String? = null,
    existingIncident: SecurityIncident? = null,
    schoolsList: List<School>,
    onSaveIncident: (SecurityIncident) -> Unit,
    onBackClick: () -> Unit
) {
    var title by remember { mutableStateOf(existingIncident?.title ?: "") }
    var selectedType by remember { mutableStateOf(existingIncident?.incidentType ?: IncidentType.STUDENT_STUDENT) }
    var description by remember { mutableStateOf(existingIncident?.description ?: "") }
    var selectedSchool by remember { mutableStateOf(existingIncident?.schoolName ?: initialSchoolName ?: (schoolsList.firstOrNull()?.name ?: "مدرسة سمالوط الثانوية بنين")) }
    var partiesInvolved by remember { mutableStateOf(existingIncident?.partiesInvolved ?: "") }
    var severity by remember { mutableStateOf(existingIncident?.severity ?: SeverityLevel.MEDIUM) }
    var actionTaken by remember { mutableStateOf(existingIncident?.actionTaken ?: "") }
    var status by remember { mutableStateOf(existingIncident?.status ?: IncidentStatus.IN_PROGRESS) }
    var imageUriString by remember { mutableStateOf(existingIncident?.imageUri) }

    val currentDate = remember { SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date()) }
    val currentTime = remember { SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()) }

    var expandedSchoolDropdown by remember { mutableStateOf(false) }
    var expandedTypeDropdown by remember { mutableStateOf(false) }

    // Photo Picker (Zero-permission Android Photo Picker)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            imageUriString = uri.toString()
        }
    }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (existingIncident == null) "تسجيل واقعة أمنية جديدة" else "تعديل بيانات الواقعة",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Error Message
            if (errorMessage != null) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // Title Field
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("عنوان الواقعة / الموضوع *") },
                placeholder = { Text("مثال: مشادة بين طلاب أثناء الفسحة") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("incident_title_input"),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Incident Type Selector
            ExposedDropdownMenuBox(
                expanded = expandedTypeDropdown,
                onExpandedChange = { expandedTypeDropdown = !expandedTypeDropdown }
            ) {
                OutlinedTextField(
                    value = selectedType.displayNameAr,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("نوع الواقعة / التصنيف *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTypeDropdown) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .testTag("incident_type_select"),
                    shape = RoundedCornerShape(12.dp)
                )

                ExposedDropdownMenu(
                    expanded = expandedTypeDropdown,
                    onDismissRequest = { expandedTypeDropdown = false }
                ) {
                    IncidentType.values().forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.displayNameAr) },
                            onClick = {
                                selectedType = type
                                expandedTypeDropdown = false
                            }
                        )
                    }
                }
            }

            // School Selector
            ExposedDropdownMenuBox(
                expanded = expandedSchoolDropdown,
                onExpandedChange = { expandedSchoolDropdown = !expandedSchoolDropdown }
            ) {
                OutlinedTextField(
                    value = selectedSchool,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("المدرسة المعنية *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSchoolDropdown) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .testTag("incident_school_select"),
                    shape = RoundedCornerShape(12.dp)
                )

                ExposedDropdownMenu(
                    expanded = expandedSchoolDropdown,
                    onDismissRequest = { expandedSchoolDropdown = false }
                ) {
                    schoolsList.forEach { school ->
                        DropdownMenuItem(
                            text = { Text(school.name) },
                            onClick = {
                                selectedSchool = school.name
                                expandedSchoolDropdown = false
                            }
                        )
                    }
                }
            }

            // Parties Involved
            OutlinedTextField(
                value = partiesInvolved,
                onValueChange = { partiesInvolved = it },
                label = { Text("الأطراف المشاركة بالواقعة") },
                placeholder = { Text("مثال: الطالب يوسف x الطالب عمر، أو ولي أمر الطالب") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("incident_parties_input"),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Severity Level Segmented Row
            Column {
                Text(
                    text = "درجة الخطورة:",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SeverityLevel.values().forEach { level ->
                        val isSelected = severity == level
                        val levelColor = when (level) {
                            SeverityLevel.LOW -> SeverityLow
                            SeverityLevel.MEDIUM -> SeverityMedium
                            SeverityLevel.HIGH -> SeverityHigh
                            SeverityLevel.CRITICAL -> SeverityCritical
                        }
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { severity = level },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) levelColor else levelColor.copy(alpha = 0.12f),
                            border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, levelColor.copy(alpha = 0.4f))
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(vertical = 10.dp)
                            ) {
                                Text(
                                    text = level.displayNameAr,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSelected) Color.White else levelColor
                                )
                            }
                        }
                    }
                }
            }

            // Incident Status Segmented Row
            Column {
                Text(
                    text = "حالة الواقعة:",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IncidentStatus.values().forEach { st ->
                        val isSelected = status == st
                        val statusColor = when (st) {
                            IncidentStatus.IN_PROGRESS -> StatusInProgress
                            IncidentStatus.RESOLVED -> StatusResolved
                            IncidentStatus.CLOSED -> StatusClosed
                        }
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { status = st },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) statusColor else statusColor.copy(alpha = 0.12f)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(vertical = 10.dp)
                            ) {
                                Text(
                                    text = st.displayNameAr,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSelected) Color.White else statusColor
                                )
                            }
                        }
                    }
                }
            }

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("تفاصيل ووصف الواقعة *") },
                placeholder = { Text("سرد ما تم رصده من تفاصيل وأسباب وملابسات...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("incident_description_input"),
                minLines = 3,
                shape = RoundedCornerShape(12.dp)
            )

            // Action Taken
            OutlinedTextField(
                value = actionTaken,
                onValueChange = { actionTaken = it },
                label = { Text("الإجراء المتخذ من إدارة الأمن والمدرسة") },
                placeholder = { Text("مثال: تم استدعاء ولي الأمر، توقيع إقرار، إخطار الإدارة...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("incident_action_input"),
                minLines = 2,
                shape = RoundedCornerShape(12.dp)
            )

            // Photo Attachment Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "إرفاق صور المعاينة / المستندات",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        OutlinedButton(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("اختيار صورة", style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    if (imageUriString != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(10.dp))
                        ) {
                            AsyncImage(
                                model = imageUriString,
                                contentDescription = "الصورة المرفقة",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            IconButton(
                                onClick = { imageUriString = null },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(6.dp)
                                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "حذف الصورة", tint = Color.White)
                            }
                        }
                    }
                }
            }

            // Supervisor Info Notice
            Surface(
                color = EduBlueLight,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Verified, contentDescription = null, tint = EduBlue)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "التسجيل معتمد بمعرفة مسؤول أمن الإدارة: مستر مصطفى فرغلي",
                        style = MaterialTheme.typography.bodySmall,
                        color = EduBlueDark
                    )
                }
            }

            // Save Button
            Button(
                onClick = {
                    if (title.isBlank()) {
                        errorMessage = "يرجى إدخال عنوان الواقعة"
                        return@Button
                    }
                    if (description.isBlank()) {
                        errorMessage = "يرجى كتابة تفاصيل ووصف الواقعة"
                        return@Button
                    }
                    errorMessage = null

                    val incident = SecurityIncident(
                        id = existingIncident?.id ?: 0,
                        title = title.trim(),
                        incidentType = selectedType,
                        description = description.trim(),
                        schoolName = selectedSchool,
                        partiesInvolved = partiesInvolved.trim(),
                        severity = severity,
                        actionTaken = actionTaken.trim(),
                        status = status,
                        dateString = existingIncident?.dateString ?: currentDate,
                        timeString = existingIncident?.timeString ?: currentTime,
                        supervisor = "مستر مصطفى فرغلي",
                        imageUri = imageUriString
                    )
                    onSaveIncident(incident)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_incident_button"),
                colors = ButtonDefaults.buttonColors(containerColor = EduGreen),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (existingIncident == null) "حفظ وتسجيل الواقعة رسمياً" else "تحديث بيانات الواقعة",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
