/*
 * Copyright 2023 RealYusufIsmail.
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 
package io.github.yusufsdiscordbot.mystigurdian.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabaseHandler;
import io.github.yusufsdiscordbot.mystigurdian.db.tables.records.ReloadAuditRecord;
import org.jooq.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
