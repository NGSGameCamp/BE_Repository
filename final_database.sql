-- =====================================================
-- 최종 게임 데이터베이스 생성 SQL
-- DELIMITER 없이 순수 INSERT 문으로만 구성
-- =====================================================

SET NAMES utf8mb4;
SET CHARACTER_SET_CLIENT = utf8mb4;
SET CHARACTER_SET_CONNECTION = utf8mb4;
SET CHARACTER_SET_RESULTS = utf8mb4;

USE ngs_db;

-- =====================================================
-- PART 0: 기존 데이터 삭제 (FK 순서 고려)
-- =====================================================

SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE review;
TRUNCATE TABLE linked_tag;
TRUNCATE TABLE single_game_discount;
TRUNCATE TABLE game;
TRUNCATE TABLE game_tag;
TRUNCATE TABLE users;
TRUNCATE TABLE user_role;
TRUNCATE TABLE user_status;

SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================
-- PART 1: 기초 데이터 (user_role, user_status)
-- =====================================================

INSERT IGNORE INTO user_role (id, role, description) VALUES
(1, 'ROLE_USER', '일반 사용자'),
(2, 'ROLE_PUBLISHER', '게임 배급사'),
(3, 'ROLE_ADMIN', '관리자');

INSERT IGNORE INTO user_status (id, name, description) VALUES
(1, 'ACTIVE', '활성 상태'),
(2, 'INACTIVE', '비활성 상태'),
(3, 'BANNED', '정지 상태');

-- =====================================================
-- PART 2: 게임 태그 (18개, STRING enum)
-- =====================================================

INSERT IGNORE INTO game_tag (tag_type) VALUES
('ACTION'), ('RPG'), ('STRATEGY'), ('SIMULATION'),
('SPORTS'), ('RACING'), ('PUZZLE'), ('ADVENTURE'),
('SHOOTER'), ('FIGHTING'), ('PLATFORMER'), ('HORROR'),
('INDIE'), ('CASUAL'), ('MMORPG'), ('SURVIVAL'),
('SANDBOX'), ('EDUCATIONAL');

-- 중복 제거
DELETE gt1 FROM game_tag gt1
INNER JOIN game_tag gt2
WHERE gt1.tag_type = gt2.tag_type AND gt1.id < gt2.id;

-- 태그 ID 변수
SET @tag_action = (SELECT id FROM game_tag WHERE tag_type = 'ACTION' LIMIT 1);
SET @tag_rpg = (SELECT id FROM game_tag WHERE tag_type = 'RPG' LIMIT 1);
SET @tag_strategy = (SELECT id FROM game_tag WHERE tag_type = 'STRATEGY' LIMIT 1);
SET @tag_simulation = (SELECT id FROM game_tag WHERE tag_type = 'SIMULATION' LIMIT 1);
SET @tag_sports = (SELECT id FROM game_tag WHERE tag_type = 'SPORTS' LIMIT 1);
SET @tag_racing = (SELECT id FROM game_tag WHERE tag_type = 'RACING' LIMIT 1);
SET @tag_puzzle = (SELECT id FROM game_tag WHERE tag_type = 'PUZZLE' LIMIT 1);
SET @tag_adventure = (SELECT id FROM game_tag WHERE tag_type = 'ADVENTURE' LIMIT 1);
SET @tag_shooter = (SELECT id FROM game_tag WHERE tag_type = 'SHOOTER' LIMIT 1);
SET @tag_fighting = (SELECT id FROM game_tag WHERE tag_type = 'FIGHTING' LIMIT 1);
SET @tag_platformer = (SELECT id FROM game_tag WHERE tag_type = 'PLATFORMER' LIMIT 1);
SET @tag_horror = (SELECT id FROM game_tag WHERE tag_type = 'HORROR' LIMIT 1);
SET @tag_indie = (SELECT id FROM game_tag WHERE tag_type = 'INDIE' LIMIT 1);
SET @tag_casual = (SELECT id FROM game_tag WHERE tag_type = 'CASUAL' LIMIT 1);
SET @tag_mmorpg = (SELECT id FROM game_tag WHERE tag_type = 'MMORPG' LIMIT 1);
SET @tag_survival = (SELECT id FROM game_tag WHERE tag_type = 'SURVIVAL' LIMIT 1);
SET @tag_sandbox = (SELECT id FROM game_tag WHERE tag_type = 'SANDBOX' LIMIT 1);
SET @tag_educational = (SELECT id FROM game_tag WHERE tag_type = 'EDUCATIONAL' LIMIT 1);

-- =====================================================
-- PART 3: Publisher 사용자 (30명)
-- =====================================================

-- 실제 게임사 10개
INSERT INTO users (email, pwd, name, nickname, created_at, role_id, status_id)
SELECT email_val, 'publisher_pwd', name_val, nickname_val, NOW(), 2, 1
FROM (
    SELECT 'fromsoftware@pub.com' AS email_val, 'FromSoftware' AS name_val, 'FromSoft' AS nickname_val UNION ALL
    SELECT 'cdpr@pub.com', 'CD Projekt RED', 'CDPR' UNION ALL
    SELECT 'rockstar@pub.com', 'Rockstar Games', 'Rockstar' UNION ALL
    SELECT 'supergiant@pub.com', 'Supergiant Games', 'Supergiant' UNION ALL
    SELECT 'teamcherry@pub.com', 'Team Cherry', 'TeamCherry' UNION ALL
    SELECT 'concernedape@pub.com', 'ConcernedApe', 'ConcernedApe' UNION ALL
    SELECT 'capcom@pub.com', 'Capcom', 'Capcom' UNION ALL
    SELECT 'larian@pub.com', 'Larian Studios', 'Larian' UNION ALL
    SELECT 'valve@pub.com', 'Valve Corporation', 'Valve' UNION ALL
    SELECT 'nintendo@pub.com', 'Nintendo', 'Nintendo'
) AS publishers
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = email_val);

-- 가상 Publisher 20개
INSERT INTO users (email, pwd, name, nickname, created_at, role_id, status_id)
SELECT
    CONCAT('publisher', n, '@game.com'),
    'publisher_pwd',
    CONCAT('Game Studio ', n),
    CONCAT('Studio', n),
    NOW(),
    2,
    1
FROM (
    SELECT 1 AS n UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL
    SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL SELECT 10 UNION ALL
    SELECT 11 UNION ALL SELECT 12 UNION ALL SELECT 13 UNION ALL SELECT 14 UNION ALL SELECT 15 UNION ALL
    SELECT 16 UNION ALL SELECT 17 UNION ALL SELECT 18 UNION ALL SELECT 19 UNION ALL SELECT 20
) AS numbers
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = CONCAT('publisher', n, '@game.com'));

-- Publisher ID 조회
SET @pub_fromsoftware = (SELECT id FROM users WHERE email = 'fromsoftware@pub.com' LIMIT 1);
SET @pub_cdpr = (SELECT id FROM users WHERE email = 'cdpr@pub.com' LIMIT 1);
SET @pub_rockstar = (SELECT id FROM users WHERE email = 'rockstar@pub.com' LIMIT 1);
SET @pub_supergiant = (SELECT id FROM users WHERE email = 'supergiant@pub.com' LIMIT 1);
SET @pub_teamcherry = (SELECT id FROM users WHERE email = 'teamcherry@pub.com' LIMIT 1);
SET @pub_concernedape = (SELECT id FROM users WHERE email = 'concernedape@pub.com' LIMIT 1);
SET @pub_capcom = (SELECT id FROM users WHERE email = 'capcom@pub.com' LIMIT 1);
SET @pub_larian = (SELECT id FROM users WHERE email = 'larian@pub.com' LIMIT 1);
SET @pub_valve = (SELECT id FROM users WHERE email = 'valve@pub.com' LIMIT 1);
SET @pub_nintendo = (SELECT id FROM users WHERE email = 'nintendo@pub.com' LIMIT 1);

-- =====================================================
-- PART 4: 실제 게임 50개 (Steam 이미지)
-- game_status = 0 (ACTIVE, ORDINAL)
-- =====================================================

INSERT INTO game (name, price, description, introduction, spec, thumbnail_url, game_status, created_at, updated_at, publisher_id) VALUES
-- FromSoftware
('Elden Ring', 69800, '프롬소프트웨어와 조지 R.R. 마틴이 함께 만든 액션 RPG. 광활한 오픈월드를 탐험하며 강력한 적들과 맞서 싸우세요.', '거대한 판타지 오픈월드 액션 RPG', 'OS: Windows 10 | CPU: i5-8400 | RAM: 12GB | GPU: GTX 1060 3GB', 'https://cdn.akamai.steamstatic.com/steam/apps/1245620/header.jpg', 0, NOW(), NOW(), @pub_fromsoftware),
('Dark Souls III', 44800, '다크 소울 시리즈의 완결편. 불의 시대의 종말을 배경으로 재의 심판관이 되어 어둠에 맞서 싸우세요.', '어둠이 세상을 집어삼키기 전', 'OS: Windows 7 64bit | CPU: i3-2100 | RAM: 8GB | GPU: GTX 750 Ti', 'https://cdn.akamai.steamstatic.com/steam/apps/374320/header.jpg', 0, NOW(), NOW(), @pub_fromsoftware),
('Sekiro: Shadows Die Twice', 64800, '센고쿠 시대 일본을 배경으로 한 사무라이 액션. 외팔 낭인 늑대가 되어 복수의 여정을 떠나세요.', '복수, 명예 회복, 그리고 죽음', 'OS: Windows 7 64bit | CPU: i3-2100 | RAM: 4GB | GPU: GTX 760', 'https://cdn.akamai.steamstatic.com/steam/apps/814380/header.jpg', 0, NOW(), NOW(), @pub_fromsoftware),
('Dark Souls Remastered', 39800, '전설의 시작, 다크 소울 리마스터. 죽음과 재생의 끝없는 순환.', '불멸의 저주를 끝낼 수 있는가', 'OS: Windows 7 64bit | CPU: i5-2500K | RAM: 8GB | GPU: GTX 970', 'https://cdn.akamai.steamstatic.com/steam/apps/570940/header.jpg', 0, NOW(), NOW(), @pub_fromsoftware),
('Dark Souls II', 39800, '다크 소울 2 완전판. 모든 DLC와 개선된 그래픽.', '저주받은 자의 끝없는 여정', 'OS: Windows 7 64bit | CPU: i3-2100 | RAM: 4GB | GPU: GTX 750', 'https://cdn.akamai.steamstatic.com/steam/apps/335300/header.jpg', 0, NOW(), NOW(), @pub_fromsoftware),

-- CD Projekt RED
('Cyberpunk 2077', 59800, '나이트 시티를 무대로 펼쳐지는 오픈월드 액션 어드벤처.', '다크한 미래 도시', 'OS: Windows 10 64bit | CPU: i7-6700 | RAM: 12GB | GPU: GTX 1060 6GB', 'https://cdn.akamai.steamstatic.com/steam/apps/1091500/header.jpg', 0, NOW(), NOW(), @pub_cdpr),
('The Witcher 3', 39800, '괴물 사냥꾼 게롤트가 되어 광활한 오픈월드를 모험하세요.', '게롤트의 마지막 모험', 'OS: Windows 7 64bit | CPU: i5-2500K | RAM: 6GB | GPU: GTX 660', 'https://cdn.akamai.steamstatic.com/steam/apps/292030/header.jpg', 0, NOW(), NOW(), @pub_cdpr),
('The Witcher 2', 19800, '위쳐 시리즈의 두 번째 작품.', '왕들의 암살자를 찾아서', 'OS: Windows XP | CPU: i5-750 | RAM: 4GB | GPU: GTX 260', 'https://cdn.akamai.steamstatic.com/steam/apps/20920/header.jpg', 0, NOW(), NOW(), @pub_cdpr),

-- Rockstar
('Red Dead Redemption 2', 65900, '1899년 미국을 배경으로 한 서부 액션 어드벤처.', '미국 서부의 종말', 'OS: Windows 10 64bit | CPU: i7-4770K | RAM: 12GB | GPU: GTX 1060', 'https://cdn.akamai.steamstatic.com/steam/apps/1174180/header.jpg', 0, NOW(), NOW(), @pub_rockstar),
('GTA V', 32900, 'LA를 모델로 한 로스 산토스의 범죄 액션.', '범죄의 도시', 'OS: Windows 7 64bit | CPU: i5-3470 | RAM: 8GB | GPU: GTX 660', 'https://cdn.akamai.steamstatic.com/steam/apps/271590/header.jpg', 0, NOW(), NOW(), @pub_rockstar),
('L.A. Noire', 19800, '1940년대 LA를 배경으로 한 범죄 수사.', '1940년대 LA의 어둠', 'OS: Windows 7 | CPU: i5-2300 | RAM: 8GB | GPU: GTX 560', 'https://cdn.akamai.steamstatic.com/steam/apps/110800/header.jpg', 0, NOW(), NOW(), @pub_rockstar),
('Max Payne 3', 19800, '스타일리시한 슈팅 액션.', '복수를 위한 총알의 춤', 'OS: Windows 7 | CPU: i5-430 | RAM: 4GB | GPU: GTX 480', 'https://cdn.akamai.steamstatic.com/steam/apps/204100/header.jpg', 0, NOW(), NOW(), @pub_rockstar),

-- Supergiant
('Hades', 27000, '그리스 신화 로그라이크 액션. 저승의 왕자가 탈출을 시도하세요.', '그리스 신화 로그라이크', 'OS: Windows 7 | CPU: Dual 2.4GHz | RAM: 4GB | GPU: GTX 950', 'https://cdn.akamai.steamstatic.com/steam/apps/1145360/header.jpg', 0, NOW(), NOW(), @pub_supergiant),
('Bastion', 16500, '세계의 파편을 모으는 액션 RPG.', '무너진 세계를 복원', 'OS: Windows XP | CPU: 1.7GHz | RAM: 2GB | GPU: X1950', 'https://cdn.akamai.steamstatic.com/steam/apps/107100/header.jpg', 0, NOW(), NOW(), @pub_supergiant),
('Transistor', 19800, '사이버펑크 액션 RPG.', '목소리를 빼앗긴 가수', 'OS: Windows 7 | CPU: 2GHz | RAM: 4GB | GPU: GTX 460', 'https://cdn.akamai.steamstatic.com/steam/apps/237930/header.jpg', 0, NOW(), NOW(), @pub_supergiant),
('Pyre', 22000, '스포츠와 RPG 결합.', '자유를 위한 의식', 'OS: Windows 7 | CPU: i3 | RAM: 4GB | GPU: GTX 650', 'https://cdn.akamai.steamstatic.com/steam/apps/462770/header.jpg', 0, NOW(), NOW(), @pub_supergiant),

-- Team Cherry
('Hollow Knight', 16500, '손으로 그린 2D 메트로배니아.', '고전 2D 액션', 'OS: Windows 7 | CPU: E5200 | RAM: 4GB | GPU: GTX 9800', 'https://cdn.akamai.steamstatic.com/steam/apps/367520/header.jpg', 0, NOW(), NOW(), @pub_teamcherry),

-- ConcernedApe
('Stardew Valley', 16500, '힐링 농장 시뮬레이션.', '평화로운 시골', 'OS: Windows Vista | CPU: 2GHz | RAM: 2GB | GPU: 256MB', 'https://cdn.akamai.steamstatic.com/steam/apps/413150/header.jpg', 0, NOW(), NOW(), @pub_concernedape),

-- Capcom
('Monster Hunter World', 39800, '거대 몬스터 사냥 액션.', '신대륙 사냥', 'OS: Windows 7 64bit | CPU: i5-4460 | RAM: 8GB | GPU: GTX 760', 'https://cdn.akamai.steamstatic.com/steam/apps/582010/header.jpg', 0, NOW(), NOW(), @pub_capcom),
('Resident Evil 2', 39800, 'RE 엔진 생존 호러.', '라쿤 시티', 'OS: Windows 7 64bit | CPU: i5-4460 | RAM: 8GB | GPU: GTX 760', 'https://cdn.akamai.steamstatic.com/steam/apps/883710/header.jpg', 0, NOW(), NOW(), @pub_capcom),
('Devil May Cry 5', 34800, '스타일리시 액션.', '악마 사냥', 'OS: Windows 7 64bit | CPU: i5-4460 | RAM: 8GB | GPU: GTX 760', 'https://cdn.akamai.steamstatic.com/steam/apps/601150/header.jpg', 0, NOW(), NOW(), @pub_capcom),
('Street Fighter 6', 59800, '격투 게임의 왕좌.', '격투의 새 시대', 'OS: Windows 10 64bit | CPU: i5-7500 | RAM: 8GB | GPU: GTX 1060', 'https://cdn.akamai.steamstatic.com/steam/apps/1364780/header.jpg', 0, NOW(), NOW(), @pub_capcom),
('Mega Man 11', 29800, '클래식 록맨.', '록맨의 귀환', 'OS: Windows 7 64bit | CPU: i3-4160 | RAM: 4GB | GPU: GTX 660', 'https://cdn.akamai.steamstatic.com/steam/apps/742300/header.jpg', 0, NOW(), NOW(), @pub_capcom),

-- Larian
('Baldurs Gate 3', 68900, 'D&D 5판 차세대 RPG.', 'D&D 세계로', 'OS: Windows 10 64bit | CPU: i5-4690 | RAM: 8GB | GPU: GTX 970', 'https://cdn.akamai.steamstatic.com/steam/apps/1086940/header.jpg', 0, NOW(), NOW(), @pub_larian),
('Divinity Original Sin 2', 45900, '전략 RPG.', '자유의 판타지', 'OS: Windows 7 64bit | CPU: i5 | RAM: 8GB | GPU: GTX 550', 'https://cdn.akamai.steamstatic.com/steam/apps/435150/header.jpg', 0, NOW(), NOW(), @pub_larian),
('Divinity Original Sin', 39800, '협동 전략 RPG.', '고전의 현대화', 'OS: Windows XP | CPU: i5-2400 | RAM: 4GB | GPU: GTX 550', 'https://cdn.akamai.steamstatic.com/steam/apps/230230/header.jpg', 0, NOW(), NOW(), @pub_larian),

-- Valve
('Portal 2', 10500, '포털 퍼즐 플랫포머.', '공간 연결 퍼즐', 'OS: Windows 7 | CPU: 3GHz P4 | RAM: 2GB | GPU: GTX 7600', 'https://cdn.akamai.steamstatic.com/steam/apps/620/header.jpg', 0, NOW(), NOW(), @pub_valve),
('Half-Life 2', 9800, 'FPS의 전설.', 'FPS 역사', 'OS: Windows XP | CPU: 1.7GHz | RAM: 512MB | GPU: DX9', 'https://cdn.akamai.steamstatic.com/steam/apps/220/header.jpg', 0, NOW(), NOW(), @pub_valve),
('Counter-Strike 2', 0, '전술 FPS.', '전술 슈팅', 'OS: Windows 10 64bit | CPU: i5-9600K | RAM: 8GB | GPU: GTX 1060', 'https://cdn.akamai.steamstatic.com/steam/apps/730/header.jpg', 0, NOW(), NOW(), @pub_valve),
('Dota 2', 0, 'MOBA의 정수.', 'MOBA 양대산맥', 'OS: Windows 7 | CPU: Dual Core | RAM: 4GB | GPU: DX11', 'https://cdn.akamai.steamstatic.com/steam/apps/570/header.jpg', 0, NOW(), NOW(), @pub_valve),
('Team Fortress 2', 0, '클래스 팀 슈팅.', '팀 슈팅 명작', 'OS: Windows 7 | CPU: 1.7GHz | RAM: 1GB | GPU: DX9', 'https://cdn.akamai.steamstatic.com/steam/apps/440/header.jpg', 0, NOW(), NOW(), @pub_valve),

-- 기타 인기 게임
('Terraria', 10500, '2D 샌드박스.', '무한 가능성', 'OS: Windows XP | CPU: 2GHz | RAM: 2.5GB | GPU: GTX 7600', 'https://cdn.akamai.steamstatic.com/steam/apps/105600/header.jpg', 0, NOW(), NOW(), @pub_valve),
('Celeste', 20500, '산을 오르는 플랫포머.', '내면과의 싸움', 'OS: Windows 7 | CPU: i3 M330 | RAM: 2GB | GPU: HD 4000', 'https://cdn.akamai.steamstatic.com/steam/apps/504230/header.jpg', 0, NOW(), NOW(), @pub_supergiant),
('Dead Cells', 26500, '로그라이크 액션.', '죽음을 딛고', 'OS: Windows 7 | CPU: i5+ | RAM: 2GB | GPU: GTX 950', 'https://cdn.akamai.steamstatic.com/steam/apps/588650/header.jpg', 0, NOW(), NOW(), @pub_teamcherry),
('Factorio', 35000, '자동화 공장 시뮬레이션.', '엔지니어의 꿈', 'OS: Windows 10 | CPU: Dual 3GHz | RAM: 4GB | GPU: GTX 260', 'https://cdn.akamai.steamstatic.com/steam/apps/427520/header.jpg', 0, NOW(), NOW(), @pub_larian),
('Cities Skylines', 30500, '도시 건설 시뮬레이션.', '꿈의 도시 설계', 'OS: Windows 7 64bit | CPU: i5-3470 | RAM: 6GB | GPU: GTX 660', 'https://cdn.akamai.steamstatic.com/steam/apps/255710/header.jpg', 0, NOW(), NOW(), @pub_rockstar),
('DOOM Eternal', 39800, '극한 FPS 액션.', '지옥 향한 복수', 'OS: Windows 7 64bit | CPU: i5-7600 | RAM: 8GB | GPU: GTX 1060', 'https://cdn.akamai.steamstatic.com/steam/apps/782330/header.jpg', 0, NOW(), NOW(), @pub_capcom),
('It Takes Two', 42000, '2인 협동 어드벤처.', '함께하는 모험', 'OS: Windows 8.1 64bit | CPU: i3-2100T | RAM: 8GB | GPU: GTX 660', 'https://cdn.akamai.steamstatic.com/steam/apps/1426210/header.jpg', 0, NOW(), NOW(), @pub_supergiant),
('Minecraft', 29900, '샌드박스 블록 게임.', '상상이 현실로', 'OS: Windows 7 | CPU: i3-3210 | RAM: 4GB | GPU: GTX 700', 'https://picsum.photos/460/215?random=minecraft', 0, NOW(), NOW(), @pub_nintendo),
('Valheim', 20500, '북유럽 생존 건설.', '바이킹 전설', 'OS: Windows 7 | CPU: 2.6GHz | RAM: 4GB | GPU: GTX 500', 'https://cdn.akamai.steamstatic.com/steam/apps/892970/header.jpg', 0, NOW(), NOW(), @pub_larian),
('Ori Blind Forest', 19800, '메트로배니아 플랫포머.', '빛을 되찾는 여정', 'OS: Windows 7 | CPU: i5 | RAM: 4GB | GPU: GTX 650', 'https://cdn.akamai.steamstatic.com/steam/apps/261570/header.jpg', 0, NOW(), NOW(), @pub_supergiant),
('Undertale', 10500, '전투 없는 RPG.', 'RPG의 혁명', 'OS: Windows XP | CPU: 2GHz | RAM: 2GB | GPU: 128MB', 'https://cdn.akamai.steamstatic.com/steam/apps/391540/header.jpg', 0, NOW(), NOW(), @pub_concernedape),
('Cuphead', 20500, '카툰 런앤건.', '악마와의 계약', 'OS: Windows 7 | CPU: i3 | RAM: 4GB | GPU: GTX 1050', 'https://cdn.akamai.steamstatic.com/steam/apps/268910/header.jpg', 0, NOW(), NOW(), @pub_teamcherry),
('Disco Elysium', 39800, '텍스트 RPG 걸작.', '생각하는 RPG', 'OS: Windows 7 64bit | CPU: i5-7500 | RAM: 8GB | GPU: GTX 1060', 'https://cdn.akamai.steamstatic.com/steam/apps/632470/header.jpg', 0, NOW(), NOW(), @pub_larian),
('Risk of Rain 2', 24000, '3D 로그라이크 슈팅.', '행성 생존', 'OS: Windows 7 | CPU: i3-6100 | RAM: 4GB | GPU: GTX 580', 'https://cdn.akamai.steamstatic.com/steam/apps/632360/header.jpg', 0, NOW(), NOW(), @pub_teamcherry),
('Slay the Spire', 25500, '덱빌딩 로그라이크.', '탑을 오르며 덱 완성', 'OS: Windows XP | CPU: 2GHz | RAM: 2GB | GPU: OpenGL 2', 'https://cdn.akamai.steamstatic.com/steam/apps/646570/header.jpg', 0, NOW(), NOW(), @pub_concernedape),
('Binding of Isaac', 16500, '다크 로그라이크.', '지하실 탈출', 'OS: Windows XP | CPU: 2.8GHz | RAM: 2GB | GPU: OpenGL 2', 'https://cdn.akamai.steamstatic.com/steam/apps/250900/header.jpg', 0, NOW(), NOW(), @pub_concernedape),
('Enter the Gungeon', 16500, '총알 지옥 로그라이크.', '총알의 던전', 'OS: Windows 7 | CPU: 2GHz | RAM: 2GB | GPU: GTX 8800', 'https://cdn.akamai.steamstatic.com/steam/apps/311690/header.jpg', 0, NOW(), NOW(), @pub_teamcherry);

-- =====================================================
-- PART 5: 리뷰 작성자 (200명)
-- =====================================================

INSERT INTO users (email, pwd, name, nickname, created_at, role_id, status_id)
SELECT
    CONCAT('reviewer', n, '@test.com'),
    'test_password',
    CONCAT('Reviewer ', n),
    CONCAT('Reviewer', n),
    NOW(),
    1,
    1
FROM (
    SELECT 1 AS n UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL
    SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL SELECT 10 UNION ALL
    SELECT 11 UNION ALL SELECT 12 UNION ALL SELECT 13 UNION ALL SELECT 14 UNION ALL SELECT 15 UNION ALL
    SELECT 16 UNION ALL SELECT 17 UNION ALL SELECT 18 UNION ALL SELECT 19 UNION ALL SELECT 20 UNION ALL
    SELECT 21 UNION ALL SELECT 22 UNION ALL SELECT 23 UNION ALL SELECT 24 UNION ALL SELECT 25 UNION ALL
    SELECT 26 UNION ALL SELECT 27 UNION ALL SELECT 28 UNION ALL SELECT 29 UNION ALL SELECT 30 UNION ALL
    SELECT 31 UNION ALL SELECT 32 UNION ALL SELECT 33 UNION ALL SELECT 34 UNION ALL SELECT 35 UNION ALL
    SELECT 36 UNION ALL SELECT 37 UNION ALL SELECT 38 UNION ALL SELECT 39 UNION ALL SELECT 40 UNION ALL
    SELECT 41 UNION ALL SELECT 42 UNION ALL SELECT 43 UNION ALL SELECT 44 UNION ALL SELECT 45 UNION ALL
    SELECT 46 UNION ALL SELECT 47 UNION ALL SELECT 48 UNION ALL SELECT 49 UNION ALL SELECT 50 UNION ALL
    SELECT 51 UNION ALL SELECT 52 UNION ALL SELECT 53 UNION ALL SELECT 54 UNION ALL SELECT 55 UNION ALL
    SELECT 56 UNION ALL SELECT 57 UNION ALL SELECT 58 UNION ALL SELECT 59 UNION ALL SELECT 60 UNION ALL
    SELECT 61 UNION ALL SELECT 62 UNION ALL SELECT 63 UNION ALL SELECT 64 UNION ALL SELECT 65 UNION ALL
    SELECT 66 UNION ALL SELECT 67 UNION ALL SELECT 68 UNION ALL SELECT 69 UNION ALL SELECT 70 UNION ALL
    SELECT 71 UNION ALL SELECT 72 UNION ALL SELECT 73 UNION ALL SELECT 74 UNION ALL SELECT 75 UNION ALL
    SELECT 76 UNION ALL SELECT 77 UNION ALL SELECT 78 UNION ALL SELECT 79 UNION ALL SELECT 80 UNION ALL
    SELECT 81 UNION ALL SELECT 82 UNION ALL SELECT 83 UNION ALL SELECT 84 UNION ALL SELECT 85 UNION ALL
    SELECT 86 UNION ALL SELECT 87 UNION ALL SELECT 88 UNION ALL SELECT 89 UNION ALL SELECT 90 UNION ALL
    SELECT 91 UNION ALL SELECT 92 UNION ALL SELECT 93 UNION ALL SELECT 94 UNION ALL SELECT 95 UNION ALL
    SELECT 96 UNION ALL SELECT 97 UNION ALL SELECT 98 UNION ALL SELECT 99 UNION ALL SELECT 100 UNION ALL
    SELECT 101 UNION ALL SELECT 102 UNION ALL SELECT 103 UNION ALL SELECT 104 UNION ALL SELECT 105 UNION ALL
    SELECT 106 UNION ALL SELECT 107 UNION ALL SELECT 108 UNION ALL SELECT 109 UNION ALL SELECT 110 UNION ALL
    SELECT 111 UNION ALL SELECT 112 UNION ALL SELECT 113 UNION ALL SELECT 114 UNION ALL SELECT 115 UNION ALL
    SELECT 116 UNION ALL SELECT 117 UNION ALL SELECT 118 UNION ALL SELECT 119 UNION ALL SELECT 120 UNION ALL
    SELECT 121 UNION ALL SELECT 122 UNION ALL SELECT 123 UNION ALL SELECT 124 UNION ALL SELECT 125 UNION ALL
    SELECT 126 UNION ALL SELECT 127 UNION ALL SELECT 128 UNION ALL SELECT 129 UNION ALL SELECT 130 UNION ALL
    SELECT 131 UNION ALL SELECT 132 UNION ALL SELECT 133 UNION ALL SELECT 134 UNION ALL SELECT 135 UNION ALL
    SELECT 136 UNION ALL SELECT 137 UNION ALL SELECT 138 UNION ALL SELECT 139 UNION ALL SELECT 140 UNION ALL
    SELECT 141 UNION ALL SELECT 142 UNION ALL SELECT 143 UNION ALL SELECT 144 UNION ALL SELECT 145 UNION ALL
    SELECT 146 UNION ALL SELECT 147 UNION ALL SELECT 148 UNION ALL SELECT 149 UNION ALL SELECT 150 UNION ALL
    SELECT 151 UNION ALL SELECT 152 UNION ALL SELECT 153 UNION ALL SELECT 154 UNION ALL SELECT 155 UNION ALL
    SELECT 156 UNION ALL SELECT 157 UNION ALL SELECT 158 UNION ALL SELECT 159 UNION ALL SELECT 160 UNION ALL
    SELECT 161 UNION ALL SELECT 162 UNION ALL SELECT 163 UNION ALL SELECT 164 UNION ALL SELECT 165 UNION ALL
    SELECT 166 UNION ALL SELECT 167 UNION ALL SELECT 168 UNION ALL SELECT 169 UNION ALL SELECT 170 UNION ALL
    SELECT 171 UNION ALL SELECT 172 UNION ALL SELECT 173 UNION ALL SELECT 174 UNION ALL SELECT 175 UNION ALL
    SELECT 176 UNION ALL SELECT 177 UNION ALL SELECT 178 UNION ALL SELECT 179 UNION ALL SELECT 180 UNION ALL
    SELECT 181 UNION ALL SELECT 182 UNION ALL SELECT 183 UNION ALL SELECT 184 UNION ALL SELECT 185 UNION ALL
    SELECT 186 UNION ALL SELECT 187 UNION ALL SELECT 188 UNION ALL SELECT 189 UNION ALL SELECT 190 UNION ALL
    SELECT 191 UNION ALL SELECT 192 UNION ALL SELECT 193 UNION ALL SELECT 194 UNION ALL SELECT 195 UNION ALL
    SELECT 196 UNION ALL SELECT 197 UNION ALL SELECT 198 UNION ALL SELECT 199 UNION ALL SELECT 200
) AS numbers
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = CONCAT('reviewer', n, '@test.com'));

-- =====================================================
-- PART 6: 게임-태그 연결 (복합키, linked_tag)
-- =====================================================

-- 각 게임에 랜덤 2-4개 태그 연결
INSERT IGNORE INTO linked_tag (game_id, tag_id)
SELECT g.id, (SELECT id FROM game_tag ORDER BY RAND() LIMIT 1)
FROM game g, (SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3) multiplier
WHERE g.game_status = 0;

-- 중복 제거 (복합키이므로 자동 처리되지만 확실히)
DELETE t1 FROM linked_tag t1
INNER JOIN linked_tag t2
WHERE t1.game_id = t2.game_id AND t1.tag_id = t2.tag_id AND t1.game_id < t2.game_id;

-- =====================================================
-- PART 7: 리뷰 데이터 생성 (50개 게임 대상)
-- =====================================================

SET @min_reviewer_id = (SELECT MIN(id) FROM users WHERE email LIKE 'reviewer%@test.com');
SET @max_reviewer_id = (SELECT MAX(id) FROM users WHERE email LIKE 'reviewer%@test.com');

-- 50개 실제 게임: 각 100-200개 리뷰, 평점 3.5-5.0
INSERT INTO review (user_id, game_id, content, score, is_deleted, created_at, updated_at)
SELECT
    @min_reviewer_id + MOD(combo.n * 17, (@max_reviewer_id - @min_reviewer_id + 1)) AS user_id,
    combo.game_id,
    CASE MOD(combo.n, 15)
        WHEN 0 THEN '정말 재미있는 게임입니다! 강력 추천합니다.'
        WHEN 1 THEN '최고의 게임이에요. 그래픽, 스토리 모두 완벽합니다.'
        WHEN 2 THEN '이 가격에 이런 퀄리티라니 믿기지 않네요.'
        WHEN 3 THEN '게임 역사에 길이 남을 명작입니다.'
        WHEN 4 THEN '완벽한 게임입니다. 모두에게 추천합니다.'
        WHEN 5 THEN '전반적으로 훌륭한 게임입니다.'
        WHEN 6 THEN '좋은 게임이에요. 시간 가는 줄 몰랐습니다.'
        WHEN 7 THEN '기대 이상이었습니다.'
        WHEN 8 THEN '재미있게 플레이했습니다.'
        WHEN 9 THEN '탄탄한 게임성에 만족합니다.'
        WHEN 10 THEN '할만한 게임입니다.'
        WHEN 11 THEN '괜찮은 게임이에요.'
        WHEN 12 THEN '나쁘지 않습니다.'
        WHEN 13 THEN '평범한 게임입니다.'
        ELSE '그저 그래요.'
    END AS content,
    -- 게임별 평점 분포 다양화 (평균 평점: 2.0 ~ 4.9)
    CASE
        -- 게임 ID % 10 = 0: 낮은 평점 (평균 ~2.0)
        WHEN MOD(combo.game_id, 10) = 0 THEN
            CASE WHEN MOD(combo.n, 10) < 7 THEN 2 WHEN MOD(combo.n, 10) < 9 THEN 1 ELSE 3 END
        -- 게임 ID % 10 = 1: 보통-낮음 평점 (평균 ~2.8)
        WHEN MOD(combo.game_id, 10) = 1 THEN
            CASE WHEN MOD(combo.n, 10) < 5 THEN 3 WHEN MOD(combo.n, 10) < 8 THEN 2 ELSE 4 END
        -- 게임 ID % 10 = 2: 보통 평점 (평균 ~3.5)
        WHEN MOD(combo.game_id, 10) = 2 THEN
            CASE WHEN MOD(combo.n, 10) < 5 THEN 4 WHEN MOD(combo.n, 10) < 8 THEN 3 ELSE 5 END
        -- 게임 ID % 10 = 3: 좋음 평점 (평균 ~4.0)
        WHEN MOD(combo.game_id, 10) = 3 THEN
            CASE WHEN MOD(combo.n, 10) < 3 THEN 3 WHEN MOD(combo.n, 10) < 7 THEN 4 ELSE 5 END
        -- 게임 ID % 10 = 4: 우수 평점 (평균 ~4.3)
        WHEN MOD(combo.game_id, 10) = 4 THEN
            CASE WHEN MOD(combo.n, 10) < 2 THEN 3 WHEN MOD(combo.n, 10) < 5 THEN 4 ELSE 5 END
        -- 게임 ID % 10 = 5: 매우 우수 평점 (평균 ~4.5)
        WHEN MOD(combo.game_id, 10) = 5 THEN
            CASE WHEN MOD(combo.n, 10) < 1 THEN 3 WHEN MOD(combo.n, 10) < 4 THEN 4 ELSE 5 END
        -- 게임 ID % 10 = 6: 훌륭함 평점 (평균 ~4.7)
        WHEN MOD(combo.game_id, 10) = 6 THEN
            CASE WHEN MOD(combo.n, 10) < 2 THEN 4 ELSE 5 END
        -- 게임 ID % 10 = 7: 최고 평점 (평균 ~4.9)
        WHEN MOD(combo.game_id, 10) = 7 THEN
            CASE WHEN MOD(combo.n, 10) < 1 THEN 4 ELSE 5 END
        -- 게임 ID % 10 = 8: 중간-높음 평점 (평균 ~3.8)
        WHEN MOD(combo.game_id, 10) = 8 THEN
            CASE WHEN MOD(combo.n, 10) < 4 THEN 4 WHEN MOD(combo.n, 10) < 7 THEN 3 ELSE 5 END
        -- 게임 ID % 10 = 9: 혼합 평점 (평균 ~3.2)
        ELSE
            CASE WHEN MOD(combo.n, 10) < 4 THEN 3 WHEN MOD(combo.n, 10) < 6 THEN 4 WHEN MOD(combo.n, 10) < 8 THEN 2 ELSE 5 END
    END AS score,
    false,
    DATE_SUB(NOW(), INTERVAL MOD(combo.n * 11, 730) DAY),
    DATE_SUB(NOW(), INTERVAL MOD(combo.n * 7, 730) DAY)
FROM (
    SELECT g.id AS game_id, r.n
    FROM (SELECT id FROM game WHERE game_status = 0) g
    CROSS JOIN (
        SELECT a.n + b.n * 10 + 1 AS n
        FROM (SELECT 0 AS n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) a
        CROSS JOIN (SELECT 0 AS n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL SELECT 10 UNION ALL SELECT 11 UNION ALL SELECT 12 UNION ALL SELECT 13 UNION ALL SELECT 14) b
        WHERE a.n + b.n * 10 + 1 <= 150
    ) r
) combo;

-- =====================================================
-- PART 9: 데이터 검증
-- =====================================================

SELECT '✅ 데이터 생성 완료' AS status;

SELECT
    '게임' AS category, COUNT(*) AS count FROM game WHERE game_status = 0
UNION ALL
SELECT '리뷰', COUNT(*) FROM review
UNION ALL
SELECT '태그', COUNT(*) FROM game_tag
UNION ALL
SELECT '게임-태그 연결', COUNT(*) FROM linked_tag
UNION ALL
SELECT 'Publisher', COUNT(*) FROM users WHERE role_id = 2
UNION ALL
SELECT '리뷰어', COUNT(*) FROM users WHERE email LIKE 'reviewer%@test.com';

-- 추천 게임 확인 (평점 3.5+, 리뷰 50+)
SELECT COUNT(*) AS recommended_games_count
FROM game g
WHERE g.game_status = 0
  AND (SELECT AVG(r.score) FROM review r WHERE r.game_id = g.id AND r.is_deleted = false) >= 3.5
  AND (SELECT COUNT(r.id) FROM review r WHERE r.game_id = g.id AND r.is_deleted = false) >= 50;

-- 상위 20개 추천 게임
SELECT
    g.id,
    g.name,
    g.price,
    COUNT(r.id) AS review_count,
    ROUND(AVG(r.score), 2) AS avg_score
FROM game g
LEFT JOIN review r ON g.id = r.game_id AND r.is_deleted = false
WHERE g.game_status = 0
GROUP BY g.id, g.name, g.price
HAVING avg_score >= 3.5 AND review_count >= 50
ORDER BY avg_score DESC, review_count DESC
LIMIT 20;

-- =====================================================
-- PART 8: 라이브러리 테스트 데이터
-- =====================================================

-- 테스트 유저 (reviewer1@test.com, user_id: 47)에게 구매 주문 생성
INSERT INTO orders (user_id, merchant_uid, status, created_at, updated_at)
VALUES (302, 'test_library_001', 'PURCHASED_CONFIRMED', NOW(), NOW());

SET @test_order_id = LAST_INSERT_ID();

-- 5개 게임 구매 (게임 ID: 1, 5, 10, 15, 20)
INSERT INTO order_details (order_id, game_id, price_snapshot, created_at, updated_at)
SELECT @test_order_id, g.id, g.price, NOW(), NOW()
FROM game g
WHERE g.id IN (1, 5, 10, 15, 20);

-- 추가 테스트 유저 (reviewer10@test.com, user_id: 56)에게 다른 게임 구매
INSERT INTO orders (user_id, merchant_uid, status, created_at, updated_at)
VALUES (56, 'test_library_002', 'PURCHASED_CONFIRMED', NOW(), NOW());

SET @test_order_id2 = LAST_INSERT_ID();

INSERT INTO order_details (order_id, game_id, price_snapshot, created_at, updated_at)
SELECT @test_order_id2, g.id, g.price, NOW(), NOW()
FROM game g
WHERE g.id IN (2, 7, 12, 18, 25, 30);

-- 검증: 라이브러리 데이터 확인
SELECT '✅ 라이브러리 테스트 데이터 생성 완료' AS status;

SELECT
    u.id AS user_id,
    u.email,
    COUNT(DISTINCT o.order_id) AS order_count,
    COUNT(od.id) AS game_count
FROM users u
LEFT JOIN orders o ON u.id = o.user_id
LEFT JOIN order_details od ON o.order_id = od.order_id
WHERE u.id IN (47, 56)
GROUP BY u.id, u.email;

select * from users;
-- =====================================================
-- 완료!
-- =====================================================
