package kr.valor.bal.utilities.binding

import android.content.Context
import kr.valor.bal.R
import kr.valor.bal.data.local.workout.UserPersonalRecording

object UserPrBindingParameterCreator {

    fun getWorkoutName(item: UserPersonalRecording?, context: Context): String {
        return item?.workoutName ?: context.resources.getStringArray(R.array.exercise_list)[0]
    }

    fun get1rmMaximum(item: UserPersonalRecording?, context: Context): String {
        val weights = item?.maximum1rm?.toInt() ?: 20
        return context.resources.getString(R.string.weights_text, weights)
    }


}