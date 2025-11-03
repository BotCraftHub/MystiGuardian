-- Add stored_files table for file management feature
-- This allows users to upload and store files (tips, documents, etc.) in Discord

CREATE TABLE IF NOT EXISTS stored_files (
    id BIGSERIAL PRIMARY KEY,
    guild_id VARCHAR(256) NOT NULL,
    file_name VARCHAR(256) NOT NULL,
    file_type VARCHAR(256) NOT NULL,
    description VARCHAR(512),
    file_url VARCHAR(512) NOT NULL,
    uploaded_by VARCHAR(256) NOT NULL,
    uploaded_at TIMESTAMP NOT NULL,
    UNIQUE (guild_id, file_name)
);

-- Create an index for faster lookups by guild_id
CREATE INDEX idx_stored_files_guild_id ON stored_files(guild_id);

-- Create an index for faster lookups by file_name
CREATE INDEX idx_stored_files_file_name ON stored_files(file_name);

