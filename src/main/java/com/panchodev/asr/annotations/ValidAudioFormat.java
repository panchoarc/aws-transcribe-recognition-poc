package com.panchodev.asr.annotations;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AudioFormatValidator.class)
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAudioFormat {

    String message() default "Audio Format not supported";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
