/*
 * This file is generated by jOOQ.
 */
package io.github.yusufsdiscordbot.mystiguardian.db.tables.pojos;


import java.io.Serializable;
import java.time.OffsetDateTime;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Warns implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String reason;
    private final String userId;
    private final String guildId;
    private final Long id;
    private final OffsetDateTime time;

    public Warns(Warns value) {
        this.reason = value.reason;
        this.userId = value.userId;
        this.guildId = value.guildId;
        this.id = value.id;
        this.time = value.time;
    }

    public Warns(
        String reason,
        String userId,
        String guildId,
        Long id,
        OffsetDateTime time
    ) {
        this.reason = reason;
        this.userId = userId;
        this.guildId = guildId;
        this.id = id;
        this.time = time;
    }

    /**
     * Getter for <code>public.warns.reason</code>.
     */
    public String getReason() {
        return this.reason;
    }

    /**
     * Getter for <code>public.warns.user_id</code>.
     */
    public String getUserId() {
        return this.userId;
    }

    /**
     * Getter for <code>public.warns.guild_id</code>.
     */
    public String getGuildId() {
        return this.guildId;
    }

    /**
     * Getter for <code>public.warns.id</code>.
     */
    public Long getId() {
        return this.id;
    }

    /**
     * Getter for <code>public.warns.time</code>.
     */
    public OffsetDateTime getTime() {
        return this.time;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Warns other = (Warns) obj;
        if (this.reason == null) {
            if (other.reason != null)
                return false;
        }
        else if (!this.reason.equals(other.reason))
            return false;
        if (this.userId == null) {
            if (other.userId != null)
                return false;
        }
        else if (!this.userId.equals(other.userId))
            return false;
        if (this.guildId == null) {
            if (other.guildId != null)
                return false;
        }
        else if (!this.guildId.equals(other.guildId))
            return false;
        if (this.id == null) {
            if (other.id != null)
                return false;
        }
        else if (!this.id.equals(other.id))
            return false;
        if (this.time == null) {
            if (other.time != null)
                return false;
        }
        else if (!this.time.equals(other.time))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.reason == null) ? 0 : this.reason.hashCode());
        result = prime * result + ((this.userId == null) ? 0 : this.userId.hashCode());
        result = prime * result + ((this.guildId == null) ? 0 : this.guildId.hashCode());
        result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
        result = prime * result + ((this.time == null) ? 0 : this.time.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Warns (");

        sb.append(reason);
        sb.append(", ").append(userId);
        sb.append(", ").append(guildId);
        sb.append(", ").append(id);
        sb.append(", ").append(time);

        sb.append(")");
        return sb.toString();
    }
}