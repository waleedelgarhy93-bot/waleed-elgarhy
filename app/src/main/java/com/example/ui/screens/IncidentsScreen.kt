package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.IncidentStatus
import com.example.data.model.SecurityIncident
import com.example.data.model.SeverityLevel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidentsScreen(
    incidents: List<SecurityIncident>,
    searchQuery: String,
    selectedSeverity: SeverityLevel?,
    selectedStatus: IncidentStatus?,
    onSearchChange: (String) -> Unit,
    onSeverityFilter: (SeverityLevel?) -> Unit,
    onStatusFilter: (IncidentStatus?) -> Unit,
    onIncidentClick: (Long) -> Unit,
    onAddIncidentClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "سجل الوقائع والأحداث الأمنية",
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddIncidentClick,
                containerColor = EduGreen,
                contentColor = Color.White,
                modifier = Modifier.testTag("fab_add_incident_list")
            ) {
                Icon(Icons.Default.Add, contentDescription = "إضافة واقعة")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .testTag("incident_search_field"),
                placeholder = { Text("بحث بعنوان الواقعة، المدرسة، أو الأطراف...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = EduBlue) },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = "مسح")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )

            // Severity Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedSeverity == null,
                    onClick = { onSeverityFilter(null) },
                    label = { Text("كل الخطورة") }
                )
                FilterChip(
                    selected = selectedSeverity == SeverityLevel.CRITICAL,
                    onClick = { onSeverityFilter(SeverityLevel.CRITICAL) },
                    label = { Text("خطير جداً 🚨") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SeverityCritical.copy(alpha = 0.2f),
                        selectedLabelColor = SeverityCritical
                    )
                )
                FilterChip(
                    selected = selectedSeverity == SeverityLevel.HIGH,
                    onClick = { onSeverityFilter(SeverityLevel.HIGH) },
                    label = { Text("عاجل 🔴") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SeverityHigh.copy(alpha = 0.2f),
                        selectedLabelColor = SeverityHigh
                    )
                )
                FilterChip(
                    selected = selectedSeverity == SeverityLevel.MEDIUM,
                    onClick = { onSeverityFilter(SeverityLevel.MEDIUM) },
                    label = { Text("متوسط 🟡") }
                )
                FilterChip(
                    selected = selectedSeverity == SeverityLevel.LOW,
                    onClick = { onSeverityFilter(SeverityLevel.LOW) },
                    label = { Text("عادي 🟢") }
                )
            }

            // Status Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedStatus == null,
                    onClick = { onStatusFilter(null) },
                    label = { Text("كل الحالات") }
                )
                FilterChip(
                    selected = selectedStatus == IncidentStatus.IN_PROGRESS,
                    onClick = { onStatusFilter(IncidentStatus.IN_PROGRESS) },
                    label = { Text("جاري المتابعة ⏳") }
                )
                FilterChip(
                    selected = selectedStatus == IncidentStatus.RESOLVED,
                    onClick = { onStatusFilter(IncidentStatus.RESOLVED) },
                    label = { Text("تم الحل ✅") }
                )
                FilterChip(
                    selected = selectedStatus == IncidentStatus.CLOSED,
                    onClick = { onStatusFilter(IncidentStatus.CLOSED) },
                    label = { Text("مغلق 📁") }
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Incidents List
            if (incidents.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CheckCircleOutline,
                            contentDescription = null,
                            tint = EduGreen,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "لا توجد وقائع مسجلة مطابقة للبحث",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "الحالة الأمنية مستقرة تماماً",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(incidents, key = { it.id }) { incident ->
                        IncidentColorCard(
                            incident = incident,
                            onClick = { onIncidentClick(incident.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun IncidentColorCard(
    incident: SecurityIncident,
    onClick: () -> Unit
) {
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

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(
                width = if (incident.severity == SeverityLevel.CRITICAL || incident.severity == SeverityLevel.HIGH) 1.5.dp else 0.5.dp,
                color = severityColor.copy(alpha = 0.6f),
                shape = RoundedCornerShape(16.dp)
            )
            .testTag("incident_card_${incident.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Severity Badge + Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = severityColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(severityColor, RoundedCornerShape(4.dp))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "الخطورة: ${incident.severity.displayNameAr}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = severityColor
                        )
                    }
                }

                Surface(
                    color = statusColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = incident.status.displayNameAr,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title
            Text(
                text = incident.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            // School & Type
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = EduBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = incident.schoolName,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Category,
                        contentDescription = null,
                        tint = EduGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = incident.incidentType.displayNameAr,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (incident.partiesInvolved.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PeopleOutline,
                        contentDescription = null,
                        tint = EduGoldDark,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "الأطراف: ${incident.partiesInvolved}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }

            Divider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outlineVariant)

            // Date & Supervisor
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${incident.dateString} - ${incident.timeString}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = incident.supervisor,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = EduBlueDark
                )
            }
        }
    }
}
