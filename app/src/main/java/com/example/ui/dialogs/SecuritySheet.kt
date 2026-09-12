package com.example.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ZoomBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityBottomSheet(
    onDismiss: () -> Unit
) {
    var isMeetingLocked by remember { mutableStateOf(false) }
    var isWaitingRoomEnabled by remember { mutableStateOf(true) }
    var allowShareScreen by remember { mutableStateOf(true) }
    var allowChat by remember { mutableStateOf(true) }
    var allowRename by remember { mutableStateOf(true) }
    var allowUnmute by remember { mutableStateOf(true) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "Security",
                    tint = ZoomBlue,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Security",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            SecurityToggleRow(
                title = "Lock Meeting",
                subtitle = "No new participants can join",
                icon = Icons.Default.Lock,
                checked = isMeetingLocked,
                onCheckedChange = { isMeetingLocked = it }
            )

            SecurityToggleRow(
                title = "Enable Waiting Room",
                subtitle = "Admit participants individually",
                icon = Icons.Default.MeetingRoom,
                checked = isWaitingRoomEnabled,
                onCheckedChange = { isWaitingRoomEnabled = it }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

            Text(
                text = "ALLOW PARTICIPANTS TO:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            SecurityCheckRow(title = "Share Screen", checked = allowShareScreen, onCheckedChange = { allowShareScreen = it })
            SecurityCheckRow(title = "Chat with Everyone", checked = allowChat, onCheckedChange = { allowChat = it })
            SecurityCheckRow(title = "Rename Themselves", checked = allowRename, onCheckedChange = { allowRename = it })
            SecurityCheckRow(title = "Unmute Themselves", checked = allowUnmute, onCheckedChange = { allowUnmute = it })

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SecurityToggleRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Text(text = subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun SecurityCheckRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(text = title, fontSize = 14.sp)
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
    }
}
