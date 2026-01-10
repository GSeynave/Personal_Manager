-- =====================================================
-- Dev Test Data Seed Script
-- Matches mock tokens: dev-user1, dev-user2, dev-user3
-- =====================================================

-- Insert test tenants
INSERT INTO tenants (id, created_at, updated_at) 
VALUES 
  (1, EXTRACT(EPOCH FROM NOW())::BIGINT, EXTRACT(EPOCH FROM NOW())::BIGINT),
  (2, EXTRACT(EPOCH FROM NOW())::BIGINT, EXTRACT(EPOCH FROM NOW())::BIGINT)
ON CONFLICT (id) DO NOTHING;

-- Insert test users (matching mock token UIDs)
INSERT INTO users (id, firebase_uid, email, user_tag, role, tenant_id, created_at, updated_at)
VALUES 
  -- Tenant 1 users (shared tenant scenario)
  (1, 'user1', 'user1@test.com', 'testuser1', 'ROLE_USER', 1, EXTRACT(EPOCH FROM NOW())::BIGINT, EXTRACT(EPOCH FROM NOW())::BIGINT),
  (2, 'user2', 'user2@test.com', 'testuser2', 'ROLE_USER', 1, EXTRACT(EPOCH FROM NOW())::BIGINT, EXTRACT(EPOCH FROM NOW())::BIGINT),
  -- Tenant 2 user (different tenant scenario)
  (3, 'user3', 'user3@test.com', 'testuser3', 'ROLE_USER', 2, EXTRACT(EPOCH FROM NOW())::BIGINT, EXTRACT(EPOCH FROM NOW())::BIGINT)
ON CONFLICT (firebase_uid) DO NOTHING;

-- Insert test wallets
INSERT INTO accounting_wallets (id, name, description, owner_id, tenant_id, created_at, updated_at)
VALUES 
  -- User1's wallets
  (1, 'User1 Personal Wallet', 'Main wallet for user1', 1, 1, EXTRACT(EPOCH FROM NOW())::BIGINT, EXTRACT(EPOCH FROM NOW())::BIGINT),
  (2, 'User1 Secret Wallet', 'Private wallet not shared with user2', 1, 1, EXTRACT(EPOCH FROM NOW())::BIGINT, EXTRACT(EPOCH FROM NOW())::BIGINT),
  -- User2's wallet
  (3, 'User2 Personal Wallet', 'Main wallet for user2', 2, 1, EXTRACT(EPOCH FROM NOW())::BIGINT, EXTRACT(EPOCH FROM NOW())::BIGINT),
  -- User3's wallet (different tenant)
  (4, 'User3 Personal Wallet', 'Main wallet for user3', 3, 2, EXTRACT(EPOCH FROM NOW())::BIGINT, EXTRACT(EPOCH FROM NOW())::BIGINT)
ON CONFLICT (id) DO NOTHING;

-- Insert wallet permissions (cross-tenant sharing examples)
INSERT INTO accounting_wallet_permissions (id, user_id, wallet_id, role_permission, created_at, updated_at)
VALUES 
  -- User2 has READ access to User1's personal wallet (same tenant sharing)
  (1, 2, 1, 'READ', EXTRACT(EPOCH FROM NOW())::BIGINT, EXTRACT(EPOCH FROM NOW())::BIGINT),
  -- User2 is EXCLUDED from User1's secret wallet
  (2, 2, 2, 'NONE', EXTRACT(EPOCH FROM NOW())::BIGINT, EXTRACT(EPOCH FROM NOW())::BIGINT),
  -- User3 has WRITE access to User1's personal wallet (cross-tenant sharing)
  (3, 3, 1, 'WRITE', EXTRACT(EPOCH FROM NOW())::BIGINT, EXTRACT(EPOCH FROM NOW())::BIGINT)
ON CONFLICT (id) DO NOTHING;

-- Reset sequences to avoid ID conflicts
SELECT setval('tenants_id_seq', (SELECT MAX(id) FROM tenants));
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('accounting_wallets_id_seq', (SELECT MAX(id) FROM accounting_wallets));
SELECT setval('accounting_wallet_permissions_id_seq', (SELECT MAX(id) FROM accounting_wallet_permissions));

-- Display summary
DO $$
BEGIN
  RAISE NOTICE '========================================';
  RAISE NOTICE 'Dev Test Data Seeded Successfully';
  RAISE NOTICE '========================================';
  RAISE NOTICE 'Tenants: %', (SELECT COUNT(*) FROM tenants);
  RAISE NOTICE 'Users: %', (SELECT COUNT(*) FROM users);
  RAISE NOTICE 'Wallets: %', (SELECT COUNT(*) FROM accounting_wallets);
  RAISE NOTICE 'Permissions: %', (SELECT COUNT(*) FROM accounting_wallet_permissions);
  RAISE NOTICE '========================================';
  RAISE NOTICE 'Test Tokens:';
  RAISE NOTICE '  Bearer dev-user1 (tenant 1)';
  RAISE NOTICE '  Bearer dev-user2 (tenant 1)';
  RAISE NOTICE '  Bearer dev-user3 (tenant 2)';
  RAISE NOTICE '========================================';
END $$;
