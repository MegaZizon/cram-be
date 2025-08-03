
DELETE FROM group_member;
DELETE FROM study_group;
DELETE FROM user_entity;


INSERT INTO user_entity (provider, provider_id, email, name, username, birth_date, profile_image, phone, role, created_at, updated_at, deleted_at)
SELECT 'google', 'google-user-1', 'user1@example.com', '유저1', 'naver user1', '1990-01-01', 'https://example.com/profile1.jpg', '01012340001', 'ADMIN', CURRENT_DATE, CURRENT_DATE, NULL
    WHERE NOT EXISTS (
    SELECT 1 FROM user_entity WHERE provider_id = 'google-user-1' OR username = 'naver user1'
);

INSERT INTO user_entity (provider, provider_id, email, name, username, birth_date, profile_image, phone, role, created_at, updated_at, deleted_at)
SELECT 'google', 'google-user-2', 'user2@example.com', '유저2', 'naver user2', '1991-02-02', 'https://example.com/profile2.jpg', '01012340002', 'USER', CURRENT_DATE, CURRENT_DATE, NULL
    WHERE NOT EXISTS (
    SELECT 1 FROM user_entity WHERE provider_id = 'google-user-2' OR username = 'naver user2'
);

INSERT INTO user_entity (provider, provider_id, email, name, username, birth_date, profile_image, phone, role, created_at, updated_at, deleted_at)
SELECT 'google', 'google-user-3', 'user3@example.com', '유저3', 'naver user3', '1992-03-03', 'https://example.com/profile3.jpg', '01012340003', 'USER', CURRENT_DATE, CURRENT_DATE, NULL
    WHERE NOT EXISTS (
    SELECT 1 FROM user_entity WHERE provider_id = 'google-user-3' OR username = 'naver user3'
);

INSERT INTO user_entity (provider, provider_id, email, name, username, birth_date, profile_image, phone, role, created_at, updated_at, deleted_at)
SELECT 'google', 'google-user-4', 'user4@example.com', '유저4', 'naver user4', '1993-04-04', 'https://example.com/profile4.jpg', '01012340004', 'USER', CURRENT_DATE, CURRENT_DATE, NULL
    WHERE NOT EXISTS (
    SELECT 1 FROM user_entity WHERE provider_id = 'google-user-4' OR username = 'naver user4'
);

INSERT INTO user_entity (provider, provider_id, email, name, username, birth_date, profile_image, phone, role, created_at, updated_at, deleted_at)
SELECT 'google', 'google-user-5', 'user5@example.com', '유저5', 'naver user5', '1994-05-05', 'https://example.com/profile5.jpg', '01012340005', 'USER', CURRENT_DATE, CURRENT_DATE, NULL
    WHERE NOT EXISTS (
    SELECT 1 FROM user_entity WHERE provider_id = 'google-user-5' OR username = 'naver user5'
);

INSERT INTO user_entity (provider, provider_id, email, name, username, birth_date, profile_image, phone, role, created_at, updated_at, deleted_at)
SELECT 'google', 'google-user-6', 'user6@example.com', '유저6', 'naver user6', '1995-06-06', 'https://example.com/profile6.jpg', '01012340006', 'USER', CURRENT_DATE, CURRENT_DATE, NULL
    WHERE NOT EXISTS (
    SELECT 1 FROM user_entity WHERE provider_id = 'google-user-6' OR username = 'naver user6'
);

INSERT INTO user_entity (provider, provider_id, email, name, username, birth_date, profile_image, phone, role, created_at, updated_at, deleted_at)
SELECT 'google', 'google-user-7', 'user7@example.com', '유저7', 'naver user7', '1996-07-07', 'https://example.com/profile7.jpg', '01012340007', 'USER', CURRENT_DATE, CURRENT_DATE, NULL
    WHERE NOT EXISTS (
    SELECT 1 FROM user_entity WHERE provider_id = 'google-user-7' OR username = 'naver user7'
);

INSERT INTO user_entity (provider, provider_id, email, name, username, birth_date, profile_image, phone, role, created_at, updated_at, deleted_at)
SELECT 'google', 'google-user-8', 'user8@example.com', '유저8', 'naver user8', '1997-08-08', 'https://example.com/profile8.jpg', '01012340008', 'USER', CURRENT_DATE, CURRENT_DATE, NULL
    WHERE NOT EXISTS (
    SELECT 1 FROM user_entity WHERE provider_id = 'google-user-8' OR username = 'naver user8'
);

INSERT INTO user_entity (provider, provider_id, email, name, username, birth_date, profile_image, phone, role, created_at, updated_at, deleted_at)
SELECT 'google', 'google-user-9', 'user9@example.com', '유저9', 'naver user9', '1998-09-09', 'https://example.com/profile9.jpg', '01012340009', 'USER', CURRENT_DATE, CURRENT_DATE, NULL
    WHERE NOT EXISTS (
    SELECT 1 FROM user_entity WHERE provider_id = 'google-user-9' OR username = 'naver user9'
);

INSERT INTO user_entity (provider, provider_id, email, name, username, birth_date, profile_image, phone, role, created_at, updated_at, deleted_at)
SELECT 'google', 'google-user-10', 'user10@example.com', '유저10', 'naver user10', '1999-10-10', 'https://example.com/profile10.jpg', '01012340010', 'USER', CURRENT_DATE, CURRENT_DATE, NULL
    WHERE NOT EXISTS (
    SELECT 1 FROM user_entity WHERE provider_id = 'google-user-10' OR username = 'naver user10'
);
INSERT INTO study_group (id, name, description, member_limit, access_type, notice, updated_at, created_at, link, thumbnail_url, leader_id)
VALUES
    (1, 'Group 1', 'Description 1', 10, 'PUBLIC', 'Notice 1', NOW(), NOW(), 'http://link1.com', 'http://thumb1.com', NULL),
    (2, 'Group 2', 'Description 2', 10, 'PUBLIC', 'Notice 2', NOW(), NOW(), 'http://link2.com', 'http://thumb2.com', NULL),
    (3, 'Group 3', 'Description 3', 10, 'PUBLIC', 'Notice 3', NOW(), NOW(), 'http://link3.com', 'http://thumb3.com', NULL);

INSERT INTO group_member (user_id, study_group_id, role, nickname, greeting, is_active, kicked, joined_at)
VALUES
    (1, 1, 'MEMBER', 'user1', 'Hi, I am user1', true, false, NOW()),
    (2, 1, 'MEMBER', 'user2', 'Hi, I am user2', true, false, NOW()),
    (3, 1, 'MEMBER', 'user3', 'Hi, I am user3', true, false, NOW()),
    (4, 1, 'MEMBER', 'user4', 'Hi, I am user4', true, false, NOW()),
    (5, 1, 'MEMBER', 'user5', 'Hi, I am user5', true, false, NOW());

-- study_group_id = 2, user_id = 4~8
INSERT INTO group_member (user_id, study_group_id, role, nickname, greeting, is_active, kicked, joined_at)
VALUES
    (4, 2, 'MEMBER', 'user4', 'Hi, I am user4', true, false, NOW()),
    (5, 2, 'MEMBER', 'user5', 'Hi, I am user5', true, false, NOW()),
    (6, 2, 'MEMBER', 'user6', 'Hi, I am user6', true, false, NOW()),
    (7, 2, 'MEMBER', 'user7', 'Hi, I am user7', true, false, NOW()),
    (8, 2, 'MEMBER', 'user8', 'Hi, I am user8', true, false, NOW());

-- user_id 9 → group 1, 2, 3에 모두 가입
INSERT INTO group_member (user_id, study_group_id, role, nickname, greeting, is_active, kicked, joined_at)
VALUES
    (9, 1, 'MEMBER', 'user9', 'Hi, I am user9', true, false, NOW()),
    (9, 2, 'MEMBER', 'user9', 'Hi, I am user9', true, false, NOW()),
    (9, 3, 'MEMBER', 'user9', 'Hi, I am user9', true, false, NOW());

UPDATE study_group SET leader_id = 1 WHERE id = 1;
UPDATE study_group SET leader_id = 5 WHERE id = 2;
UPDATE study_group SET leader_id = 9 WHERE id = 3;