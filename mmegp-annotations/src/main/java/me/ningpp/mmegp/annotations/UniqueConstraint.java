package me.ningpp.mmegp.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Inherited
@Target({ })
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueConstraint {

    String[] columns();

}
