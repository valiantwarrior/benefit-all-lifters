package kr.valor.bal.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kr.valor.bal.utilities.localDateFormatter
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.util.*


@Entity(tableName = "workout_overview")
data class WorkoutOverview(
    @PrimaryKey
    @ColumnInfo(name = "overview_id")
    val overviewId: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "date")
    var date: LocalDate = LocalDate.now(),

    @ColumnInfo(name = "start_time_milli")
    val startTimeMilli: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "end_time_milli")
    var endTimeMilli: Long = startTimeMilli
)
