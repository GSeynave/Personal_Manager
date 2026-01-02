# Auto-Categorization

## Overview
Allows users to define rules that automatically categorize imported transactions, reducing manual work and ensuring consistency. Rules support flexible matching patterns with actions that set category, subcategory, and custom labels.

## Key Features
- **Rule creation** - Users define IF/THEN rules with field matching (CONTAINS, STARTS_WITH, etc.)
- **Multi-condition support** - Chain conditions with AND/OR operators for complex patterns
- **Automatic application** - Rules evaluated when transactions imported, no manual triggering needed
- **Priority system** - Users control rule evaluation order, first match wins
- **Rule management** - Edit, disable, reorder, or delete rules without losing history

## Architecture Decisions

### Rule Storage Format
**Choice:** Store rules as JSON in PostgreSQL JSONB column
**Alternatives Considered:** 
- Separate conditions table (more normalized)
**Reasoning:** Balance of flexibility and query performance. Easy tenant isolation, simple versioning, PostgreSQL JSONB provides indexing and query capabilities.
**Tradeoffs Accepted:** Less efficient for complex queries across rules. Schema validation happens at application layer instead of database constraints.

### Evaluation Timing
**Choice:** Synchronous evaluation during transaction creation
**Alternatives Considered:**
- Async background job
- Real-time streaming with Kafka
**Reasoning:** Immediate feedback to user, simple implementation, rule evaluation is fast (<50ms for typical rule sets)
**Tradeoffs Accepted:** Blocks transaction creation if rules slow. Acceptable for MVP, can move to async if needed.

### Conflict Resolution
**Choice:** First matching rule wins, user controls priority order
**Alternatives Considered:**
- Apply all matching rules (rejected - creates confusion)
- Most specific rule wins (rejected - hard to define "specific")
- User chooses per-rule (rejected - too complex)
**Reasoning:** Simple mental model, predictable behavior, users have full control via ordering
**Tradeoffs Accepted:** Users must manually order rules, no automatic "smart" prioritization

## Technical Approach
- Event-driven: Listens to TransactionImportedEvent
- REST API for CRUD operations on rules
- PostgreSQL JSONB for rule storage with GIN indexes
- Custom expression evaluator (no external rule engine) 
- Tenant isolation via tenant_id foreign key on rules table

## Business Context
Manual transaction categorization is tedious and error-prone. Auto-categorization saves users 5-10 minutes per import session and ensures consistent categorization over time. Critical for users tracking budgets across months.

## Future Considerations
- [ ] Machine learning suggestions based on past categorizations
- [ ] Rule templates/marketplace (community-shared rules)
- [ ] Bulk re-categorization when rules change
- [ ] Rule analytics (how often each rule matches)

## Resources

See : [auto-categorization-rules.md]
