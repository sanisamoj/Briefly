package com.sanisamoj.utils.schedule

import com.sanisamoj.utils.schedule.models.JobIdentification
import com.sanisamoj.utils.schedule.models.StartRoutineData
import org.quartz.*
import org.quartz.impl.StdSchedulerFactory

class ScheduleRoutine {
    var scheduler: Scheduler = StdSchedulerFactory().scheduler
        private set
    
    companion object {
        var jobsList: MutableList<JobIdentification> = mutableListOf()
            private set
    }

    inline fun <reified T : Job> startRoutine(startRoutineData: StartRoutineData): JobKey {
        scheduler.start()

        val job = JobBuilder.newJob(T::class.java)
            .withIdentity(startRoutineData.name, startRoutineData.group.name)
            .build()

        val triggerBuilder = TriggerBuilder.newTrigger()
            .withIdentity(startRoutineData.name, startRoutineData.group.name)
            .startNow()

        val scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
            .withIntervalInMilliseconds(startRoutineData.interval)

        if (startRoutineData.repeatForever) {
            scheduleBuilder.repeatForever()
        }

        val trigger = triggerBuilder.withSchedule(scheduleBuilder).build()

        scheduler.scheduleJob(job, trigger)
        val jobIdentification = JobIdentification(job.key, startRoutineData.description)
        jobsList.add(jobIdentification)
        return job.key
    }

    fun stopRoutine(jobKey: JobKey) {
        scheduler.deleteJob(jobKey)
    }
}