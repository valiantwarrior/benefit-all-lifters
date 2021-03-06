package kr.valor.bal.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kr.valor.bal.data.local.AppDatabase
import kr.valor.bal.data.local.user.UserDao
import kr.valor.bal.data.local.workout.WorkoutDao
import kr.valor.bal.data.local.youtube.VideoDao
import kr.valor.bal.utilities.DATABASE_NAME
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            DATABASE_NAME
        )
            .build()
    }

    @Provides
    fun provideWorkoutDao(appDatabase: AppDatabase): WorkoutDao {
        return appDatabase.workoutDao()
    }

    @Provides
    fun provideVideoDao(appDatabase: AppDatabase): VideoDao {
        return appDatabase.videoDao()
    }

    @Provides
    fun provideUserDao(appDatabase: AppDatabase): UserDao {
        return appDatabase.userDao()
    }

    //    @Singleton
//    @Provides
//    fun provideAppDatabase(@ApplicationContext appContext: Context, provider: Provider<WorkoutDao>): AppDatabase {
//        return Room.databaseBuilder(
//            appContext,
//            AppDatabase::class.java,
//            DATABASE_NAME
//        )
//            .addCallback(object : RoomDatabase.Callback() {
//                override fun onCreate(db: SupportSQLiteDatabase) {
//                    super.onCreate(db)
//                    CoroutineScope(Dispatchers.IO).launch {
//                        randomGenerator().forEach {
//                            provider.get().insertWorkoutOverview(it)
//                        }
//                    }
//                }
//            })
//            .build()
//    }
}
