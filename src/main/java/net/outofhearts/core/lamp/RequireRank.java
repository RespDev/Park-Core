package net.outofhearts.core.lamp;

import net.outofhearts.core.player.Rank;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface RequireRank {
    Rank value();
}