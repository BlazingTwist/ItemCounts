package blazingtwist.itemcounts.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to annotate default constructors which will be invoked by auto-config (cloth config)
 * solely used to disable 'unused'-warnings
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.CONSTRUCTOR)
public @interface AutoConfigConstructor {
}
