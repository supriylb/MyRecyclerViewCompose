package com.example.myrecyclerviewcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.myrecyclerviewcompose.ui.theme.MyRecyclerViewComposeTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyRecyclerViewComposeTheme {
                MainScreen()
            }
        }
    }
}

enum class ViewType {
    LIST,
    GRID,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val names = stringArrayResource(R.array.data_name)
    val descriptions = stringArrayResource(R.array.data_description)
    val photoUrls = stringArrayResource(R.array.data_photo_url)

    val heroes = remember(names, descriptions, photoUrls) {
        val count = minOf(names.size, descriptions.size, photoUrls.size)
        List(count) { index ->
            Hero(
                name = names[index],
                description = descriptions[index],
                photoUrl = photoUrls[index],
            )
        }
    }

    var viewType by rememberSaveable { mutableStateOf(ViewType.LIST) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val onHeroClick: (Hero) -> Unit = { hero ->
        scope.launch {
            snackbarHostState.showSnackbar(hero.name)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
                actions = {
                    ViewTypeActionButtons(
                        currentViewType = viewType,
                        onViewTypeChange = { viewType = it },
                    )
                },
            )
        },
    ) { innerPadding ->
        AnimatedContent(
            targetState = viewType,
            label = "ViewTypeAnimation",
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) { targetViewType ->
            when (targetViewType) {
                ViewType.LIST -> HeroList(
                    heroes = heroes,
                    onHeroClick = onHeroClick,
                )

                ViewType.GRID -> HeroGrid(
                    heroes = heroes,
                    onHeroClick = onHeroClick,
                )
            }
        }
    }
}

@Composable
fun HeroList(
    heroes: List<Hero>,
    modifier: Modifier = Modifier,
    onHeroClick: (Hero) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(
            items = heroes,
            key = { it.name },
        ) { hero ->
            HeroListItem(
                hero = hero,
                onClick = { onHeroClick(hero) },
            )
        }
    }
}

@Composable
fun HeroGrid(
    heroes: List<Hero>,
    modifier: Modifier = Modifier,
    onHeroClick: (Hero) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(
            items = heroes,
            key = { it.name },
        ) { hero ->
            HeroGridItem(
                hero = hero,
                onClick = { onHeroClick(hero) },
            )
        }
    }
}

@Composable
fun HeroListItem(
    hero: Hero,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            HeroImage(
                photoUrl = hero.photoUrl,
                contentDescription = hero.name,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape),
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = hero.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    text = hero.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
fun HeroGridItem(
    hero: Hero,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            HeroImage(
                photoUrl = hero.photoUrl,
                contentDescription = hero.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(8.dp)),
            )

            Text(
                text = hero.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )

            Text(
                text = hero.description,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun HeroImage(
    photoUrl: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val imageRequest = remember(photoUrl) {
        ImageRequest.Builder(context)
            .data(photoUrl)
            .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
            .crossfade(true)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_foreground)
            .build()
    }

    AsyncImage(
        model = imageRequest,
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = modifier,
    )
}

@Composable
private fun ViewTypeActionButtons(
    currentViewType: ViewType,
    onViewTypeChange: (ViewType) -> Unit,
) {
    val items = listOf(
        Triple(ViewType.LIST, Icons.AutoMirrored.Filled.List, R.string.list_view),
        Triple(ViewType.GRID, Icons.Default.GridView, R.string.grid_view),
    )

    items.forEach { (type, icon, contentDescRes) ->
        val isSelected = currentViewType == type

        IconButton(onClick = { onViewTypeChange(type) }) {
            Icon(
                imageVector = icon,
                contentDescription = stringResource(contentDescRes),
                tint = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
            )
        }
    }
}

private val SampleHero = Hero(
    name = "Ahmad Dahlan",
    description = "Salah seorang ulama dan khatib terkemuka di Masjid Besar Kasultanan Yogyakarta...",
    photoUrl = "",
)

@Preview(showBackground = true)
@Composable
fun HeroListItemPreview() {
    MyRecyclerViewComposeTheme {
        HeroListItem(
            hero = SampleHero,
            onClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HeroGridItemPreview() {
    MyRecyclerViewComposeTheme {
        HeroGridItem(
            hero = SampleHero,
            onClick = {},
        )
    }
}
