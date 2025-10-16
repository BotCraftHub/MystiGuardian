# Multi-Guild/Channel Configuration Guide

## Overview
The system now supports sending job notifications to multiple Discord channels across different guilds.

## Configuration Format

### Option 1: Single Guild/Channel (Backwards Compatible)
Your current config will continue to work:

```json
"daConfig": {
    "discordChannelId": "1054379920542937159",
    "guildId": "938122131949097052",
    "spreadsheetId": "1W7WJQerSH_Fdxq5BfiYdDz70cC1N4uWTDQjWgp-eUW8"
}
```

### Option 2: Multiple Guilds/Channels (New Format)
To send notifications to multiple channels:

```json
"daConfig": {
    "guildChannels": [
        {
            "guildId": "938122131949097052",
            "discordChannelId": "1054379920542937159"
        },
        {
            "guildId": "1045714326956290078",
            "discordChannelId": "1278095420161265675"
        },
        {
            "guildId": "ANOTHER_GUILD_ID",
            "discordChannelId": "ANOTHER_CHANNEL_ID"
        }
    ],
    "spreadsheetId": "1W7WJQerSH_Fdxq5BfiYdDz70cC1N4uWTDQjWgp-eUW8"
}
```

## How It Works

1. **Backwards Compatible**: If you keep your current config, it will work exactly as before
2. **Multiple Channels**: Use the `guildChannels` array to specify multiple guild/channel pairs
3. **Same Spreadsheet**: All jobs are still saved to the same spreadsheet regardless of how many channels you notify

## Example Use Cases

### Use Case 1: Same bot in multiple servers
If your bot is in multiple Discord servers and you want to send job notifications to all of them:

```json
"guildChannels": [
    {
        "guildId": "SERVER_1_ID",
        "discordChannelId": "CHANNEL_IN_SERVER_1"
    },
    {
        "guildId": "SERVER_2_ID",
        "discordChannelId": "CHANNEL_IN_SERVER_2"
    }
]
```

### Use Case 2: Multiple channels in the same server
If you want to send notifications to multiple channels in the same server:

```json
"guildChannels": [
    {
        "guildId": "938122131949097052",
        "discordChannelId": "1054379920542937159"
    },
    {
        "guildId": "938122131949097052",
        "discordChannelId": "ANOTHER_CHANNEL_IN_SAME_SERVER"
    }
]
```

## Important Notes

- All job IDs are still deduplicated globally (one spreadsheet)
- Jobs are sent to ALL configured channels
- There's a 1-second delay between sending to different channels to avoid rate limiting
- If a guild or channel is not found, it's skipped with a warning in the logs

## Migration

**You don't need to change anything!** Your current config will continue to work. Only update if you want to add more channels.

