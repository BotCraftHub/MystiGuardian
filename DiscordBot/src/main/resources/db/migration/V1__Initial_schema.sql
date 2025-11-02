-- Initial schema for MystiGuardian
-- This migration creates all the base tables for the bot

-- Reload Audit Table
CREATE TABLE IF NOT EXISTS reload_audit (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(256) NOT NULL,
    reason VARCHAR(256) NOT NULL,
    time TIMESTAMP NOT NULL
);

-- Warns Table
CREATE TABLE IF NOT EXISTS warns (
    id BIGSERIAL PRIMARY KEY,
    guild_id VARCHAR(256) NOT NULL,
    user_id VARCHAR(256) NOT NULL,
    reason VARCHAR(256) NOT NULL,
    time TIMESTAMP NOT NULL,
    UNIQUE (guild_id, user_id, id)
);

-- Amount of Warns Table
CREATE TABLE IF NOT EXISTS amount_of_warns (
    id BIGSERIAL PRIMARY KEY,
    guild_id VARCHAR(256) NOT NULL,
    user_id VARCHAR(256) NOT NULL,
    amount_of_warns INTEGER NOT NULL,
    UNIQUE (guild_id, user_id, id)
);

-- Time Out Table
CREATE TABLE IF NOT EXISTS time_out (
    id BIGSERIAL PRIMARY KEY,
    guild_id VARCHAR(256) NOT NULL,
    user_id VARCHAR(256) NOT NULL,
    reason VARCHAR(256) NOT NULL,
    duration TIMESTAMP NOT NULL,
    time TIMESTAMP NOT NULL,
    UNIQUE (guild_id, user_id, id)
);

-- Amount of Time Outs Table
CREATE TABLE IF NOT EXISTS amount_of_time_outs (
    id BIGSERIAL PRIMARY KEY,
    guild_id VARCHAR(256) NOT NULL,
    user_id VARCHAR(256) NOT NULL,
    amount_of_time_outs INTEGER NOT NULL,
    UNIQUE (guild_id, user_id, id)
);

-- Kick Table
CREATE TABLE IF NOT EXISTS kick (
    id BIGSERIAL PRIMARY KEY,
    guild_id VARCHAR(256) NOT NULL,
    user_id VARCHAR(256) NOT NULL,
    reason VARCHAR(256) NOT NULL,
    time TIMESTAMP NOT NULL,
    UNIQUE (guild_id, user_id, id)
);

-- Amount of Kicks Table
CREATE TABLE IF NOT EXISTS amount_of_kicks (
    id BIGSERIAL PRIMARY KEY,
    guild_id VARCHAR(256) NOT NULL,
    user_id VARCHAR(256) NOT NULL,
    amount_of_kicks INTEGER NOT NULL,
    UNIQUE (guild_id, user_id, id)
);

-- Ban Table
CREATE TABLE IF NOT EXISTS ban (
    id BIGSERIAL PRIMARY KEY,
    guild_id VARCHAR(256) NOT NULL,
    user_id VARCHAR(256) NOT NULL,
    reason VARCHAR(256) NOT NULL,
    time TIMESTAMP NOT NULL,
    UNIQUE (guild_id, user_id, id)
);

-- Amount of Bans Table
CREATE TABLE IF NOT EXISTS amount_of_bans (
    id BIGSERIAL PRIMARY KEY,
    guild_id VARCHAR(256) NOT NULL,
    user_id VARCHAR(256) NOT NULL,
    amount_of_bans INTEGER NOT NULL,
    UNIQUE (guild_id, user_id, id)
);

-- Soft Ban Table
CREATE TABLE IF NOT EXISTS soft_ban (
    id BIGSERIAL PRIMARY KEY,
    guild_id VARCHAR(256) NOT NULL,
    user_id VARCHAR(256) NOT NULL,
    reason VARCHAR(256) NOT NULL,
    days INTEGER NOT NULL,
    time TIMESTAMP NOT NULL
);

-- OAuth Table
CREATE TABLE IF NOT EXISTS oauth (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(256) NOT NULL,
    access_token VARCHAR(256) NOT NULL,
    refresh_token VARCHAR(256) NOT NULL,
    expires_in VARCHAR(256) NOT NULL,
    user_json VARCHAR(1000) NOT NULL
);

-- Audit Channel Table
CREATE TABLE IF NOT EXISTS audit_channel (
    guild_id VARCHAR(256) PRIMARY KEY,
    channel_id VARCHAR(256) NOT NULL
);

