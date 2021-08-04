package com.hfi.insurance.config;

import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * @Author ChenZX
 * @Date 2021/8/4 10:38
 * @Description:
 */
@Slf4j
public class CacheRemovalListener implements RemovalListener<String,Object> {
    @Override
    public void onRemoval(@Nullable String o, @Nullable Object o2, @NonNull RemovalCause removalCause) {
        String tips = String.format("key=%s,value=%s,reason=%s", o, o2, removalCause);
        log.info("缓存日志：{}",tips);
    }
}
