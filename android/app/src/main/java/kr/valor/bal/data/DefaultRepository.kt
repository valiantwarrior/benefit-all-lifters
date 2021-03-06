package kr.valor.bal.data

import androidx.lifecycle.*
import kotlinx.coroutines.*
import kr.valor.bal.data.local.user.UserDao
import kr.valor.bal.data.local.user.UserPersonalRecording
import kr.valor.bal.data.local.workout.WorkoutDao
import kr.valor.bal.data.local.workout.WorkoutSchedule
import kr.valor.bal.data.local.workout.entities.WorkoutDetail
import kr.valor.bal.data.local.workout.entities.WorkoutOverview
import kr.valor.bal.data.local.workout.entities.WorkoutSet
import kr.valor.bal.data.local.youtube.VideoDao
import kr.valor.bal.data.remote.YoutubeApiService
import kr.valor.bal.data.remote.asDatabaseModel
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultRepository @Inject constructor(
    private val workoutDao: WorkoutDao,
    private val videoDao: VideoDao,
    private val userDao: UserDao,
    private val service: YoutubeApiService) {

    private val _workoutOverviewCached = MutableLiveData<WorkoutOverview>()

    private var _workoutScheduleCached = MutableLiveData<WorkoutSchedule>()
    val workoutScheduleCached: LiveData<WorkoutSchedule>
        get() = _workoutScheduleCached

    val workoutOverview: LiveData<WorkoutOverview?> =
        workoutDao.getWorkoutOverviewByDateLiveData(LocalDate.now())

    val workoutSchedules: LiveData<List<WorkoutSchedule>> =
        workoutDao.getAllWorkoutSchedule()

    val youtubeVideos = videoDao.getVideos()

    val userInfo = userDao.getUserInfo()

    val userPrRecords = userDao.getAllUserPersonalRecord()

    suspend fun refreshVideos() {
        withContext(Dispatchers.IO) {
            val videoList = service.requestVideo()
            videoDao.insertAll(videoList.asDatabaseModel())
        }
    }

    fun getWorkoutOverviewOfToday(init: ((WorkoutOverview) -> Unit)?) = liveData {
        val currentDate = LocalDate.now()
        val currentWorkoutOverview = workoutDao.getWorkoutOverviewByDate(currentDate)
            ?: run {
                createWorkoutOverviewIfNotExist()
            }
        init?.invoke(currentWorkoutOverview)
        emit(currentWorkoutOverview)
    }

    fun getWorkoutScheduleByWorkoutOverviewId(id: Long): LiveData<WorkoutSchedule> {
        return workoutDao.getWorkoutSchedule(id)
    }

    fun getWorkoutOverviewCachedById(overviewId: Long) = liveData<WorkoutOverview> {
        val targetWorkoutOverview = workoutDao.getNoneNullWorkoutOverviewById(overviewId)
        _workoutOverviewCached.value = targetWorkoutOverview
        emit(workoutDao.getNoneNullWorkoutOverviewById(overviewId))
    }

    fun getWorkoutScheduleCachedByWorkoutOverviewId(overviewId: Long) = liveData<WorkoutSchedule> {
        val targetWorkoutSchedule = workoutDao.getNoneNullWorkoutSchedule(overviewId)
        _workoutScheduleCached.value = targetWorkoutSchedule
        emitSource(workoutDao.getWorkoutSchedule(overviewId))
    }

    suspend fun isChanged(overviewId: Long, coroutineScope: CoroutineScope): Boolean {
        val workoutScheduleAsync = coroutineScope.async {
            workoutDao.getNoneNullWorkoutSchedule(overviewId)
        }
        val workoutScheduleAfterEdit = workoutScheduleAsync.await()
        return workoutScheduleAfterEdit != workoutScheduleCached.value
    }

    suspend fun restore(overviewId: Long, coroutineScope: CoroutineScope): Boolean {
        val job = coroutineScope.launch {
            if (workoutDao.getNoneNullWorkoutOverviewById(overviewId) != _workoutOverviewCached.value) {
                workoutDao.update(_workoutOverviewCached.value!!)
            }

            val originalWorkoutSchedule = _workoutScheduleCached.value!!
            val originalWorkoutDetails = originalWorkoutSchedule.workoutDetails.map { it.workoutDetail }
            val originalWorkoutSets = originalWorkoutSchedule.workoutDetails.flatMap {
                it.workoutSets
            }

            val modifiedWorkoutSchedule = workoutDao.getNoneNullWorkoutSchedule(overviewId)
            val modifiedWorkoutDetails = modifiedWorkoutSchedule.workoutDetails.map { it.workoutDetail }
            val modifiedWorkoutSets = modifiedWorkoutSchedule.workoutDetails.flatMap { it.workoutSets }

            modifiedWorkoutSets.forEach {
                workoutDao.delete(it)
            }
            modifiedWorkoutDetails.forEach {
                workoutDao.delete(it)
            }

            originalWorkoutDetails.forEach {
                workoutDao.insert(it)
            }

            originalWorkoutSets.forEach {
                workoutDao.insert(it)
            }
        }
        job.join()

        return true
    }

    suspend fun getWorkoutNameByWorkoutSetId(setId: Long): String {
        return workoutDao.getWorkoutNameByWorkoutSetId(setId)
    }

    fun getWorkoutSetById(id: Long): LiveData<WorkoutSet> {
        return workoutDao.getWorkoutSet(id)
    }

    suspend fun insertWorkoutSet(workoutSet: WorkoutSet): Long {
        return workoutDao.insert(workoutSet)
    }

    suspend fun updateWorkoutSet(workoutSet: WorkoutSet) {
        workoutDao.update(workoutSet)
    }

    suspend fun removeWorkoutSet(detailId: Long) {
        workoutDao.deleteWorkoutSetAssociatedWithWorkoutDetail(detailId)
    }

    suspend fun getLatestWorkoutSetInfo(workoutSet: WorkoutSet): WorkoutSet? {
        return workoutDao.getLatestWorkoutSetByWorkoutDetailId(workoutSet.containerId)
    }

    suspend fun addWorkoutDetail(workoutDetail: WorkoutDetail) {
        workoutDao.insert(workoutDetail)
    }

    suspend fun dropWorkoutDetail(workoutDetail: WorkoutDetail) {
        workoutDao.delete(workoutDetail)
    }

    suspend fun updateWorkoutOverview(workoutOverview: WorkoutOverview) {
        workoutDao.update(workoutOverview)
    }

    suspend fun getPrInfoOfThisWorkout(workoutName: String): UserPersonalRecording {
        return userDao.getPersonalRecordOfThisWorkout(workoutName)
    }

    suspend fun updateUserPersonalRecording(pr: UserPersonalRecording) {
        userDao.updateUserPrRecording(pr)
    }

    private suspend fun createWorkoutOverviewIfNotExist(): WorkoutOverview {
        val newWorkoutOverview = WorkoutOverview()
        workoutDao.insert(newWorkoutOverview)
        return workoutDao.getLatestWorkoutOverview()
    }
}