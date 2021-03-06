package kr.valor.bal.data.remote

import kr.valor.bal.data.local.youtube.DatabaseVideo

data class YoutubeVideoContainer(val items: List<YoutubeVideo>)

data class YoutubeVideo(
    val kind: String,
    val etag: String,
    val id: YoutubeVideoId,
    val snippet: YoutubeVideoSnippet
)

data class YoutubeVideoId(
    val kind: String,
    val videoId: String
)

data class YoutubeVideoSnippet(
    val publishedAt: String,
    val channelId: String,
    val title: String,
    val description: String,
    val thumbnails: YoutubeVideoThumbnail,
    val channelTitle: String,
    val liveBroadcastContent: String,
    val publishTime: String
)

data class YoutubeVideoThumbnail(
    val default: ThumbnailInfo,
    val medium: ThumbnailInfo,
    val high: ThumbnailInfo
)

data class ThumbnailInfo(
    val url: String,
    val width: Int,
    val height: Int
)

fun YoutubeVideoContainer.asDatabaseModel(): List<DatabaseVideo> {
    return items.map {
        DatabaseVideo(
            id = it.id.videoId,
            thumbnailUrl = it.snippet.thumbnails.high.url,
            thumbnailWidth = it.snippet.thumbnails.high.width,
            thumbnailHeight = it.snippet.thumbnails.high.width,
            title = it.snippet.title,
            channelTitle = it.snippet.channelTitle
        )
    }
}