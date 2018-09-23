package com.beautyboss.slogen.simplejobserver.mapper;

import org.apache.ibatis.annotations.Param;

/**
 * Author : Slogen
 * Date   : 2017/12/13
 */
public interface SimpleJobLockMapper {

    void insertJobLock(@Param("jobName") String jobName,
                       @Param("jobHash") int jobHash,
                       @Param("status") int status,
                       @Param("host") String host);

    int updateStatus(@Param("jobHash") int jobHash,
                     @Param("newStatus") int newStatus,
                     @Param("oldStatus") int oldStatus);

    Integer getJobLockStatus(@Param("jobHash") int jobHash);

    String getJobLockHost(@Param("jobHash") int jobHash);
}
