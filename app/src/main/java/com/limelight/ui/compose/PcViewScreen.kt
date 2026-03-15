package com.limelight.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.limelight.PcView
import com.limelight.R
import com.limelight.grid.PcGridAdapter
import com.limelight.nvstream.http.ComputerDetails

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PcViewScreen(
    adapter: PcGridAdapter,
    onSettingsClick: () -> Unit,
    onHelpClick: () -> Unit,
    onAddAppClick: () -> Unit
) {
    // A dark/slate theme inspired by Shadcn UI
    MaterialTheme(
        colorScheme = darkColorScheme(
            background = Color(0xFF09090B),
            surface = Color(0xFF18181B),
            onBackground = Color(0xFFFAFAFA),
            onSurface = Color(0xFFFAFAFA),
            primary = Color(0xFFFAFAFA),
            onPrimary = Color(0xFF09090B),
            surfaceVariant = Color(0xFF27272A),
            onSurfaceVariant = Color(0xFFA1A1AA)
        )
    ) {
        var itemCount by remember { mutableStateOf(adapter.count) }
        
        // This is a minimal hack to refresh compose when adapter changes.
        // In a real migration we'd use flow/livedata for state completely, but this bridges the legacy adapter safely.
        DisposableEffect(adapter) {
            val dataSetObserver = object : android.database.DataSetObserver() {
                override fun onChanged() {
                    itemCount = adapter.count
                }
            }
            adapter.registerDataSetObserver(dataSetObserver)
            onDispose {
                adapter.unregisterDataSetObserver(dataSetObserver)
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Prettylight", fontWeight = FontWeight.Bold) },
                    actions = {
                        IconButton(onClick = onSettingsClick) {
                            Icon(painterResource(id = R.drawable.ic_settings), contentDescription = "Settings", tint = MaterialTheme.colorScheme.onSurface)
                        }
                        IconButton(onClick = onHelpClick) {
                            Icon(painterResource(id = R.drawable.ic_help), contentDescription = "Help", tint = MaterialTheme.colorScheme.onSurface)
                        }
                        IconButton(onClick = onAddAppClick) {
                            Icon(painterResource(id = R.drawable.ic_add), contentDescription = "Add PC", tint = MaterialTheme.colorScheme.onSurface)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                if (itemCount == 0) {
                    EmptyState()
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 160.dp),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(itemCount) { index ->
                            val computerObj = adapter.getItem(index) as PcView.ComputerObject
                            // Use adapter's View method directly for simplicity, but that returns an XML View.
                            // Better: We redraw the grid item directly in Compose mapping exactly what pc_grid_item does
                            PcGridItem(
                                computerDetails = computerObj.details,
                                onClick = {
                                    // Normally we would invoke the ListView's OnItemClickListener.
                                    // For this bridge, we can just trigger it through the ListView reference if we had it,
                                    // but we can ask the bridge or Activity to handle it.
                                },
                                onLongClick = {
                                    // Trigger Context menu
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyState() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Searching for PCs on your local network...",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 16.sp
        )
    }
}

@Composable
fun PcGridItem(
    computerDetails: ComputerDetails,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
        // Note: Long press and context menus in Compose require slightly different handling
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(125.dp),
                contentAlignment = Alignment.Center
            ) {
                // We'd load the box art or default PC icon here based on computerDetails
                // A complete port would use Coil to load images, or fetch from adapter cleanly.
                Icon(
                    painter = painterResource(id = R.drawable.ic_pc_offline), // Mock icon
                    contentDescription = null,
                    modifier = Modifier.size(100.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = computerDetails.name,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp
            )
        }
    }
}
