package com.beautyboss.slogen.simplejobserver.register;

/**
 * Author : Slogen
 * Date   : 2017/12/11
 */
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface SimpleQuartzJob {

    String name();

    String group() default "DEFAULT";

    String cronExp();

    String type() default "single";
}
