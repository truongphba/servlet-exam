package myanotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) // thời điểm sử dụng.
@Target(ElementType.FIELD) // phạm vi sử dụng của annotation, dành cho class, hay field...
public @interface Validate {
    String requiredMessage() default "This field can't be empty";
    boolean required() default false;
    boolean isEmail() default false;
    String message() default "Something is wrong with this field";
}
