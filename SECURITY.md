# Security Policy

## 🔒 License Protection

MystiGuardian is licensed under the **Apache License 2.0**. This means:

- ✅ **You MUST** include the original copyright notice
- ✅ **You MUST** include a copy of the Apache License 2.0
- ✅ **You MUST** state significant changes made to the code
- ✅ **You MUST** retain all copyright, patent, trademark, and attribution notices
- ❌ **You CANNOT** use the project's trademarks without permission
- ❌ **You CANNOT** claim the code as your own work

**Any use of this code without proper attribution is a violation of copyright law.**

## ⚖️ Copyright Notice

```
Copyright 2025 RealYusufIsmail.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## 🛡️ Reporting Security Vulnerabilities

We take security seriously. If you discover a security vulnerability, please follow responsible disclosure:

### **DO NOT** open a public GitHub issue for security vulnerabilities!

Instead:

1. **Email**: Send details to the project maintainer (contact information can be found in the GitHub profile)
2. **Include**:
   - Description of the vulnerability
   - Steps to reproduce
   - Potential impact
   - Suggested fix (if any)
3. **Wait**: Allow up to 90 days for a response before public disclosure

### Scope

Security issues include but are not limited to:

- Authentication/Authorization bypasses
- SQL injection vulnerabilities
- Remote code execution
- Sensitive data exposure
- API token/credential leaks
- Discord bot permission escalation

## 🔐 Sensitive Data Protection

This repository **NEVER** contains:

- ❌ Discord bot tokens (`config.json` is gitignored)
- ❌ Database credentials (`config.json` is gitignored)
- ❌ Google Service Account credentials (`service-account.json` is gitignored)
- ❌ API keys or OAuth secrets
- ❌ Private keys or certificates

**If you find such data in the repository, it's a critical security issue - report immediately!**

## 🔍 Code Integrity

### Digital Signatures
All releases are tagged and signed with GPG. Verify releases before use:

```bash
git tag -v v0.0.9
```

### Checksum Verification
Verify JAR file integrity using checksums provided with each release.

### Supply Chain Security
- All dependencies are managed through Gradle Version Catalog
- Dependencies are sourced from Maven Central (trusted source)
- Regular dependency updates and security audits

## 🚨 Known Security Considerations

### Rate Limiting
The bot implements rate limiting for:
- Discord API calls (respects Discord rate limits)
- Web scraping operations (500ms delay between requests)
- Command execution (per-user cooldowns)

### Data Storage
- Apprenticeship data is stored in Google Sheets (read-only access for most users)
- Discord IDs and configuration are stored in PostgreSQL
- No personal user data (messages, DMs) is logged or stored

### Bot Permissions
The bot requires specific Discord permissions - always follow the principle of least privilege:
- Only grant permissions necessary for operation
- Review permission scopes before inviting to your server
- Use role-based access control for admin commands

## 🔄 Security Update Policy

| Version | Status | Security Updates |
|---------|--------|------------------|
| 0.0.9+  | ✅ Supported | Yes |
| < 0.0.9 | ❌ Unsupported | No |

We recommend always using the latest stable release.

## 📝 Security Best Practices for Users

### Hosting
1. ✅ Use environment variables for sensitive configuration (not `config.json` in production)
2. ✅ Enable firewall rules to restrict database access
3. ✅ Use HTTPS for all web endpoints (OAuth module)
4. ✅ Regularly update dependencies and the bot itself
5. ✅ Monitor logs for suspicious activity

### Database Security
1. ✅ Use strong PostgreSQL passwords
2. ✅ Restrict database access to localhost or VPN
3. ✅ Enable SSL/TLS for database connections in production
4. ✅ Regular backups with encryption
5. ✅ Rotate credentials periodically

### Discord Security
1. ✅ Never share your bot token
2. ✅ Regenerate token if compromised
3. ✅ Use Discord's OAuth2 flow (not user tokens)
4. ✅ Set up 2FA on your Discord account
5. ✅ Regularly audit bot permissions

## 🏆 Security Acknowledgments

We appreciate security researchers who help keep MystiGuardian secure. Responsible disclosures will be acknowledged in release notes (with permission).

---

**Last Updated**: November 2, 2025

