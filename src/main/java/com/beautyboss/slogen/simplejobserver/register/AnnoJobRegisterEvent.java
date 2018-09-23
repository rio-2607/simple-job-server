package com.beautyboss.slogen.simplejobserver.register;

import com.beautyboss.slogen.simplejobserver.base.ClusterJob;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

/**
 * Author : Slogen
 * Date   : 2017/12/11
 */

public class AnnoJobRegisterEvent extends ApplicationEvent{

    @Getter
    @Setter
    private ClusterJob clusterJob;
    public AnnoJobRegisterEvent(Object source) {
        super(source);
    }
}
