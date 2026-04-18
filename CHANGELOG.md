# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2024-04-18

### Added
- Initial release of Clipboard Rule Manager
- AccessibilityService for monitoring clipboard changes
- Rule-based URL cleaning with regex patterns
- Rule management UI with Material Design 3
- History tracking for clipboard modifications
- Anti-loop prevention mechanism
- Dark/Light theme support
- Rules persistence using Room Database
- Settings storage using DataStore
- Add, edit, enable/disable, and delete rules
- Regex pattern validation
- Clipboard history viewing (last 100 entries)
- Clear history functionality
- Multiple preset rules for common websites

### Features
- Background service that runs continuously
- No internet connection required
- Complete offline operation
- Local data storage only
- Zero tracking/analytics

### Technical
- Built with Kotlin and Jetpack Compose
- Clean Architecture pattern
- Coroutines for async operations
- MVVM pattern for UI
- Repository pattern for data access

---

## [Planned for Future Releases]

### [1.1.0] - Planned
- [ ] Import/export rules as JSON
- [ ] Pre-configured rule templates
- [ ] Regex pattern editor with live preview
- [ ] Keyboard shortcuts support
- [ ] Notification when rule applied
- [ ] Statistics dashboard
- [ ] Multiple backup/restore options

### [1.2.0] - Planned
- [ ] Advanced pattern conditions (URL scheme, domain validation)
- [ ] Rule scheduling (enable/disable at specific times)
- [ ] Whitelist/blacklist specific apps
- [ ] Custom variables in replacement strings
- [ ] Undo last modification

### [2.0.0] - Planned
- [ ] Cloud sync (optional)
- [ ] Collaborative rule sharing
- [ ] Rule marketplace
- [ ] Plugin system
- [ ] Browser extension integration

---

## Versioning

This project follows [Semantic Versioning](https://semver.org/):
- **MAJOR** (X.0.0): Breaking changes
- **MINOR** (0.X.0): New features, backward compatible
- **PATCH** (0.0.X): Bug fixes

---

## How to Report Issues

Found a bug? Have a feature request? Please open an issue on [GitHub Issues](https://github.com/yourusername/clipboard-rules/issues).

When reporting bugs, please include:
- Android version and device model
- App version (Help > About)
- Steps to reproduce
- Expected vs actual behavior
- Screenshots if applicable
- Regex pattern (if rule-related)
