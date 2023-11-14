package io.github.yusufsdiscordbot.mystigurdian.database;

import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabaseHandler;
import io.github.yusufsdiscordbot.mystigurdian.db.tables.records.ReloadAuditRecord;
import org.jooq.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ReloadAuditTest {

    @Mock
    private Result<ReloadAuditRecord> mockResult;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldSetReloadAuditRecord() {
        String userId = "testUser";
        String reason = "testReason";

        MystiGuardianDatabaseHandler.ReloadAudit.setReloadAuditRecord(userId, reason);
    }

    @Test
    public void shouldGetReloadAuditRecords() {
        when(MystiGuardianDatabaseHandler.ReloadAudit.getReloadAuditRecords()).thenReturn(mockResult);

        Result<ReloadAuditRecord> result = MystiGuardianDatabaseHandler.ReloadAudit.getReloadAuditRecords();

        assertEquals(mockResult, result);
    }
}