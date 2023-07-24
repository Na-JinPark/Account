package com.example.account.service;

import com.example.account.dto.UseBalance;
import com.example.account.exception.AccountException;
import com.example.account.type.ErrorCode;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LockAopAspectTest {
    @Mock
    private LockService lockService;

    @Mock
    private ProceedingJoinPoint proceddingJoinPoint;

    @InjectMocks
    private LockAopAspect lockAopAspect;

    @Test
    void lockAndunlock_evenIfThrow() throws Throwable {
        //given
        ArgumentCaptor<String> lockArgumentcaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> unlockArgumentcaptor = ArgumentCaptor.forClass(String.class);
        UseBalance.Request request = new UseBalance.Request(123L, "54321", 1000L);
        given(proceddingJoinPoint.proceed())
                .willThrow(new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));
        //when
        assertThrows(AccountException.class, () ->
                lockAopAspect.aroundMethod(proceddingJoinPoint, request));
        //then
        verify(lockService, times(1)).lock(lockArgumentcaptor.capture());
        verify(lockService, times(1)).lock(unlockArgumentcaptor.capture());
        assertEquals("54321", lockArgumentcaptor.getValue());
        assertEquals("54321", unlockArgumentcaptor.getValue());
    }

    @Test
    void lockAndunlock() throws Throwable {
        //given
        ArgumentCaptor<String> lockArgumentcaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> unlockArgumentcaptor = ArgumentCaptor.forClass(String.class);
        UseBalance.Request request = new UseBalance.Request(123L, "1234", 1000L);
        //when
        lockAopAspect.aroundMethod(proceddingJoinPoint, request);
        //then
        verify(lockService, times(1)).lock(lockArgumentcaptor.capture());
        verify(lockService, times(1)).lock(unlockArgumentcaptor.capture());
        assertEquals("1234", lockArgumentcaptor.getValue());
        assertEquals("1234", unlockArgumentcaptor.getValue());
    }
}