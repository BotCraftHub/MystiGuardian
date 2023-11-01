package io.github.yusufsdiscordbot.mystiguardian.database;

import io.github.yusufsdiscordbot.mystiguardian.MystiGuardian;
import io.github.yusufsdiscordbot.mystigurdian.db.tables.records.ReloadAuditRecord;
import org.jooq.Result;

import static io.github.yusufsdiscordbot.mystigurdian.db.Tables.RELOAD_AUDIT;

public class MystiGuardianDatabaseHandler {

    public static class ReloadAudit {
        public static void setReloadAuditRecord(String userId, String reason) {
            MystiGuardian.getContext().insertInto(RELOAD_AUDIT, RELOAD_AUDIT.USER_ID, RELOAD_AUDIT.REASON)
                    .values(userId, reason)
                    .execute();
        }
    }
}
