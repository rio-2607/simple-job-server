package com.beautyboss.slogen.simplejobserver.mapper;

import com.beautyboss.slogen.simplejobserver.data.SimpleJobLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Author : Slogen
 * Date   : 2017/12/13
 */
public interface SimpleJobLogMapper {

    /**
     * 添加JobLog的jobName和startedTime
     *
     * @param simpleJobLog
     */
    Integer addOneJobLog(@Param("simpleJobLog") SimpleJobLog simpleJobLog);

    /**
     * 根据JobLog的id修改finishedTime
     *
     * @param simpleJobLog
     * @return
     */
    int updateOneJobLogFinishedTime(@Param("simpleJobLog") SimpleJobLog simpleJobLog);

    /**
     * 更新一个job的失败信息
     * @param simpleJobLog
     * @return
     */
    int updateOneJobLogExceptionMessage(@Param("simpleJobLog") SimpleJobLog simpleJobLog);

    List<SimpleJobLog> getJobLogByNameAndTime(@Param("jobName") String jobName,
                                              @Param("startedTime") String startedTime,
                                              @Param("finishedTime") String finishedTime);
}
