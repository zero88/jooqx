package io.zero88.jooqx.provider;

import org.jetbrains.annotations.NonNls;

public interface HasDSLProvider {

    @NonNls JooqDSLProvider dslProvider();

}
