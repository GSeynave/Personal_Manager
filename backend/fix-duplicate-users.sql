-- Fix duplicate users issue
-- Step 1: Check for duplicates
SELECT firebase_uid, COUNT(*) as count
FROM users
GROUP BY firebase_uid
HAVING COUNT(*) > 1;

-- Step 2: Delete duplicate users (keeps the oldest one)
DELETE FROM users
WHERE id NOT IN (
    SELECT MIN(id)
    FROM users
    GROUP BY firebase_uid
);

-- Step 3: Add unique constraint to prevent future duplicates
ALTER TABLE users
ADD CONSTRAINT uk_users_firebase_uid UNIQUE (firebase_uid);

-- Verify the fix
SELECT firebase_uid, COUNT(*) as count
FROM users
GROUP BY firebase_uid
HAVING COUNT(*) > 1;
