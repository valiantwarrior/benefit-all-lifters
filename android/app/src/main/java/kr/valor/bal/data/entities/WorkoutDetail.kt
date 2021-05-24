package kr.valor.bal.data.entities

import androidx.room.*
import kr.valor.bal.data.WorkoutSet
import java.util.*

@Entity(
    tableName = "workout_detail",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutOverview::class,
            parentColumns = ["overview_id"],
            childColumns = ["container_id"],
            onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("container_id")]
)
data class WorkoutDetail(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "detail_id")
    val detailId: Long = 0L,

    @ColumnInfo(name = "container_id")
    val containerId: Long,

    @ColumnInfo(name = "workout_name")
    val workoutName: String,

    @ColumnInfo(name = "sets_detail")
    val setsDetail: MutableList<WorkoutSet> = mutableListOf()
)



