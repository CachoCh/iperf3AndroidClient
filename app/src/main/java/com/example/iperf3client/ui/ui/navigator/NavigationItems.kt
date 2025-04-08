package com.example.iperf3client.ui.ui.navigator

import androidx.compose.ui.graphics.vector.ImageVector
import com.example.iperf3client.IperfScreen

class NavigationItems (
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val badgeCount: Int? = null,
    val screen: IperfScreen
)