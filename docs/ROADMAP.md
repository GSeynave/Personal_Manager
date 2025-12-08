# Product Roadmap

> **Vision:** Create a spiritual growth companion that helps users elevate themselves through intentional tracking, meaningful gamification, and supportive social connections.

## Guiding Principles

1. **One Domain at a Time** - Focus deeply, ship completely
2. **Core Loop First** - Prove the magic (track → gamify → share) before expanding
3. **Spiritual over Mechanical** - Features that foster growth, not just checkboxes
4. **Social = Pride, Not Competition** - Celebrate together, lift each other up

---

## Release Strategy

### Version Numbering
- **Major (X.0.0)** - New core domain or architectural change
- **Minor (0.X.0)** - New features within existing domains
- **Patch (0.0.X)** - Bug fixes, small improvements

### Release Cadence
- **MVP:** Single big release (0.1.0)
- **Post-MVP:** 2-week sprint cycles
- **Feature releases:** When ready (no forced deadlines)

---

## Phase 0: Foundation 
**Goal:** Set up project infrastructure

### Completed
- [x] Project structure (multi-module Spring Boot)
- [x] Database setup (PostgreSQL)
- [x] Authentication system (JWT)
- [x] Core module (User entity, shared utilities)
- [ ] CI/CD pipeline (semantic versioning, auto-release)
- [x] Frontend scaffolding (Vue 3 + shadcn-vue)
- [x] Documentation templates

---

## Phase 1: MVP - The Core Loop 🎯 (Current Focus)

**Timeline:** [06-12-2025] → [?]
**Goal:** Prove the value proposition with minimal but complete experience

### Success Criteria
- [ ] Users can track daily tasks and habits
- [ ] Users feel "lighter" after unloading their brain
- [ ] Users are motivated by gamification (not just tolerating it)
- [ ] Users want to share achievements with friends
- [ ] 10 beta users use it daily for 2+ weeks

### Modules to Build

#### 1. Todo Module (Productivity & Focus) 🚧
**Status:** In Progress (70% complete)
- [x] Task CRUD operations
- [x] Task grouping
- [ ] Task priority (simple)
- [ ] XP rewards on completion
- [ ] Basic UI (functional, not perfect)

#### 2. Habits Module (Health & Wellness Foundation) 📋
**Status:** Next up
- [ ] Habit creation (name, frequency, XP reward)
- [ ] Daily check-in interface
- [ ] Streak calculation (consecutive days)
- [ ] Simple habit list view
- [ ] XP integration

#### 3. Gamification Module (Cross-Cutting) 📋
**Status:** After habits
- [ ] GameProfile entity (XP, level, title)
- [ ] XP award system (listen to TaskCompleted, HabitCompleted events)
- [ ] Level calculation (exponential curve)
- [ ] Title system (Freshman → Apprentice → Journeyman → Master)
- [ ] Basic badge system (5-10 badges)
- [ ] Streak tracking (cross-domain)

#### 4. Social Module (Basic) 📋
**Status:** Final MVP piece
- [ ] Friend system (add, accept, remove)
- [ ] Achievement sharing (manual, opt-in)
- [ ] Pacts v1 (shared habit tracking with one friend)
- [ ] Simple profile view (see friend's level, badges)
- [ ] No feed/timeline (future)

### MVP Features Summary

| Feature | Module | Priority | Status |
|---------|--------|----------|--------|
| Create/complete tasks | Todo | P0 | ✅ Done |
| Task groups | Todo | P0 | ✅ Done |
| Create/track habits | Habits | P0 | 📋 Todo |
| Streak counting | Habits | P0 | 📋 Todo |
| XP on completion | Game | P0 | 📋 Todo |
| Level progression | Game | P0 | 📋 Todo |
| Title unlocking | Game | P0 | 📋 Todo |
| Basic badges | Game | P1 | 📋 Todo |
| Add friends | Social | P1 | 📋 Todo |
| Share achievement | Social | P1 | 📋 Todo |
| Habit pacts | Social | P1 | 📋 Todo |

### Out of Scope for MVP
- ❌ AI features (future)
- ❌ Finance, Health, Learning modules (future domains)
- ❌ Analytics/insights (future)
- ❌ Social feed (too complex for MVP)
- ❌ Group challenges (start with 1:1 pacts)
- ❌ Leaderboards (may create unhealthy competition)
- ❌ Mobile app (web-first)

### MVP UI/UX Focus
- **Design system:** shadcn-vue + Tailwind + colored outline icons
- **Aesthetic:** Zen minimalism (soft colors, breathing room)
- **Language:** Spiritual ("You chose growth today" not "Task completed")
- **Micro-interactions:** Satisfying but not overwhelming
- **Mobile-friendly:** Responsive web, not native app

### MVP Launch Checklist
- [ ] All P0 features complete and tested
- [ ] Basic error handling (no crashes)
- [ ] User authentication works reliably
- [ ] Data persists correctly
- [ ] 5 internal testers validate for 1 week
- [ ] Documentation: user guide, API docs
- [ ] Landing page explaining the vision
- [ ] Privacy policy & terms (basic)
- [ ] Discord community set up
- [ ] Feedback mechanism in app
- [ ] Analytics setup (basic usage metrics)

---

## Phase 2: Polish & Expand (Post-MVP)

**Timeline:** [After MVP] → [+2 months]
**Goal:** Refine MVP based on feedback, add depth to existing modules

### Based on User Feedback
**Prioritize based on what users actually want/need**

### Potential Enhancements

#### Todo Module v2
- [ ] Pomodoro timer integration
- [ ] Task priorities (AI-suggested or manual)
- [ ] Due dates with reminders
- [ ] Projects (multi-task grouping)
- [ ] Subtasks
- [ ] Recurring tasks

#### Habits Module v2
- [ ] Flexible frequencies (every 2 days, 3x per week, etc.)
- [ ] Habit categories (health, productivity, learning)
- [ ] Time-of-day tracking (morning, evening)
- [ ] Habit notes/journal entries
- [ ] Habit templates (popular habits to adopt)

#### Gamification v2
- [ ] More badges (20-30 total)
- [ ] Cross-domain achievements ("Productive Healthy Human")
- [ ] Domain-specific titles (not just overall level)
- [ ] Seasonal events (Summer of Growth, etc.)
- [ ] XP multipliers for streaks
- [ ] Weekly recap with progress

#### Social v2
- [ ] Friend activity feed (opt-in)
- [ ] Group pacts (3+ friends)
- [ ] Challenge templates (30-day habit challenge)
- [ ] Private groups (family, close friends)
- [ ] Encouragement messages between friends
- [ ] "Check on friend" nudges

#### Infrastructure
- [ ] Email notifications (opt-in)
- [ ] Push notifications (web push)
- [ ] Export data (JSON, CSV)
- [ ] Dark mode
- [ ] Accessibility improvements (WCAG AA)
- [ ] Performance optimizations
- [ ] Better error messages

---

## Phase 3: New Domains (Major Expansion)

**Timeline:** [+3-6 months]
**Goal:** Add new core domains based on validated demand

### Priority Order (Tentative)

#### 1. Finance Module 💰
**Why first:** High impact, clear value, data-driven insights
- Accounting (expenses, income)
- Subscription tracking
- Debt management
- Budget planning
- XP integration (save money = XP)

#### 2. Health Module 🏥
**Why second:** Natural extension of habits, high engagement
- Diet/meal logging
- Sleep tracking
- Fitness activities
- Health metrics (weight, etc.)
- Integration with wearables (future)

#### 3. Learning Module 📚
**Why third:** Completes the "elevation" story
- Course tracking
- Skill development paths
- Reading lists
- Learning goals
- Integration with learning platforms

#### 4. POI Module 📍
**Why later:** Nice-to-have, not core to growth
- Location tracking
- Favorite places
- Vector DB for recommendations
- Social sharing of places

#### 5. Energy Module ⚡
**Why later:** Niche use case
- Utility consumption tracking
- Cost optimization
- Environmental impact

---

## Phase 4: Intelligence Layer (AI & Analytics)

**Timeline:** [+6-12 months]
**Goal:** Add smart features that provide insights and predictions

### AI Features
- [ ] Task priority suggestions
- [ ] Habit recommendations based on user patterns
- [ ] Personalized goal setting
- [ ] Natural language task entry ("remind me to call mom tomorrow")
- [ ] Smart scheduling (best time for habits)
- [ ] Conversational interface (chat with your data)

### Analytics Features
- [ ] Cross-domain insights ("productivity drops when sleep < 6h")
- [ ] Pattern detection (weekly/monthly trends)
- [ ] Goal progress tracking with forecasts
- [ ] Weekly/monthly reports (automated)
- [ ] Comparative analytics (you vs past you, not vs others)
- [ ] Data visualizations (charts, heatmaps)

---

## Phase 5: Platform Maturity (Long-term)

**Timeline:** [12+ months]
**Goal:** Scale, polish, potential monetization

### Potential Features
- [ ] Native mobile apps (iOS, Android)
- [ ] Team/organization features (beyond personal use)
- [ ] Public API for third-party integrations
- [ ] Zapier/IFTTT integrations
- [ ] Browser extension
- [ ] Desktop apps (Electron)
- [ ] Webhooks for custom automations
- [ ] Premium features (advanced analytics, AI, etc.)
- [ ] White-label for coaches/therapists

### Infrastructure Scaling
- [ ] Microservices extraction (if needed)
- [ ] Multi-region deployment
- [ ] Advanced caching (Redis)
- [ ] CDN for static assets
- [ ] Database sharding (if needed)
- [ ] Real-time features (WebSockets)

---

## Feature Request Backlog

### High Demand (from users/beta testers)
- [ ] [Feature name] - [Who requested] - [Why valuable]
- [ ] [Feature name] - [Who requested] - [Why valuable]

### Nice to Have
- [ ] [Feature name] - [Impact: Low/Medium/High]
- [ ] [Feature name] - [Impact: Low/Medium/High]

### Under Consideration
- [ ] [Feature name] - [Needs validation]
- [ ] [Feature name] - [Needs validation]

### Rejected (and why)
- ❌ [Feature name] - [Reason for rejection]
- ❌ [Feature name] - [Reason for rejection]

---

## Technical Debt & Refactoring

### Known Issues
- [ ] [Technical issue] - [Impact] - [Priority]
- [ ] [Technical issue] - [Impact] - [Priority]

### Planned Refactors
- [ ] [Refactor description] - [When to do it]
- [ ] [Refactor description] - [When to do it]

---

## Metrics & Success Indicators

### MVP Success Metrics
- **Activation:** 80% of signups create at least 1 task or habit
- **Retention:** 40% of users return day 2, 20% return day 7
- **Engagement:** Average 2 completions (task/habit) per active day
- **Sentiment:** 8/10 average rating from beta testers
- **Referral:** 30% of users invite at least 1 friend

### Growth Metrics (Post-MVP)
- Monthly Active Users (MAU)
- Daily Active Users (DAU)
- DAU/MAU ratio (stickiness)
- Average session length
- Tasks/habits completed per user per week
- XP earned per user per week
- Social engagement (pacts created, achievements shared)

### Business Metrics (Future)
- Conversion rate (free → premium, if applicable)
- Churn rate
- Customer Lifetime Value (LTV)
- Net Promoter Score (NPS)

---

## Community & Go-to-Market

### MVP Launch Plan
1. **Closed Beta** (friends & family, 10 users, 2 weeks)
2. **Open Beta** (Discord invite, 50 users, 1 month)
3. **Soft Launch** (Product Hunt, Reddit, small audience)
4. **Public v1.0** (Marketing push, press outreach)

### Marketing Channels
- [ ] Product Hunt launch
- [ ] Reddit (r/productivity, r/selfimprovement)
- [ ] Twitter/X (personal growth community)
- [ ] Discord community
- [ ] Blog (personal growth stories)
- [ ] YouTube (demo videos, user stories)
- [ ] Email newsletter

### Partnership Opportunities
- [ ] Productivity coaches
- [ ] Habit-building communities
- [ ] Mental health advocates
- [ ] Fitness influencers (for health module)

---

## Decision Log

### Why start with Todo + Habits (not Finance or Health)?
**Decision:** MVP focuses on Productivity + Habits
**Rationale:** 
- Universal use case (everyone has tasks/habits)
- Quickest to build and validate
- Foundation for gamification
- Easy to explain value proposition
**Date:** [Date]

### Why Pacts before Social Feed?
**Decision:** Build 1:1 pacts before complex social features
**Rationale:**
- Simpler to build
- More intimate, less pressure
- Validates social layer without building full feed
- Can expand to groups later
**Date:** [Date]

### Why no AI in MVP?
**Decision:** Defer AI features to Phase 4
**Rationale:**
- Need user data first (cold start problem)
- Core loop doesn't depend on AI
- Significant complexity/cost
- Can add later without disrupting UX
**Date:** [Date]

---

## Next Actions

### Short term (This Week)
- [ ] Finish todo module UI polish
- [ ] Start habits module (entity, repository, service)
- [ ] Design gamification data model
- [ ] Set up Discord community

### Mid term
- [ ] Complete habits module
- [ ] Build gamification module
- [ ] Start social module (friends, basic profiles)
- [ ] Internal testing with 3-5 people

### Long term
- [ ] Launch MVP to closed beta (10 users)
- [ ] Gather feedback, iterate
- [ ] Open beta (50 users)
- [ ] Prepare for public launch

---

**Last Updated:** [06-12-2025]
**Current Phase:** Phase 1 - MVP
**Current Focus:** Todo Module Polish + Habits Module Build
**Next Milestone:** Complete MVP Core Loop ([Target Date])
