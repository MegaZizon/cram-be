-- H2 데이터베이스용 알림 시스템 더미데이터
-- 파일 위치: src/main/resources/notification/data.sql

-- 1. 기존 사용자 데이터 (알림 테스트용)
INSERT INTO `사용자` (`id`, `소셜_로그인_종류`, `소셜_고유_식별_id`, `소셜_계정_이메일`, `사용자_이름`, `생년월일`, `이미지_URL`, `전화번호`, `서비스_권한`, `생성일시`, `수정일시`, `삭제_여부`) VALUES
                                                                                                                                              (1, 'GOOGLE', 'google_12345', 'test1@example.com', '김철수', '1990-01-01', 'https://example.com/profile1.jpg', '010-1234-5678', 'USER', '2024-01-01 10:00:00', '2024-01-01 10:00:00', false),
                                                                                                                                              (2, 'KAKAO', 'kakao_67890', 'test2@example.com', '이영희', '1992-03-15', 'https://example.com/profile2.jpg', '010-9876-5432', 'USER', '2024-01-02 11:00:00', '2024-01-02 11:00:00', false),
                                                                                                                                              (3, 'GOOGLE', 'google_11111', 'admin@example.com', '관리자', '1985-05-20', 'https://example.com/admin.jpg', '010-1111-2222', 'ADMIN', '2024-01-01 09:00:00', '2024-01-01 09:00:00', false);

-- 2. 기본 스터디 그룹 데이터 (알림 테스트용)
INSERT INTO `스터디_그룹` (`id`, `그룹장_id`, `스터디명`, `설명`, `모집_인원`, `공개_유형`, `안내_사항`, `수정_일시`, `생성일시`, `초대_링크`, `썸네일`) VALUES
                                                                                                                    (1, 1, '알고리즘 스터디', '알고리즘 문제를 함께 풀어보는 스터디입니다.', 10, 'public', '매주 화요일 저녁 7시에 모입니다.', '2024-01-01', '2024-01-01', 'https://cram.com/invite/abc123', 'https://example.com/algorithm_thumb.jpg'),
                                                                                                                    (2, 2, 'Spring Boot 스터디', 'Spring Boot를 학습하는 스터디 그룹입니다.', 8, 'public', '초보자 환영!', '2024-01-02', '2024-01-02', 'https://cram.com/invite/def456', 'https://example.com/spring_thumb.jpg'),
                                                                                                                    (3, 1, '개인 프로젝트', '개인 프로젝트 진행용 그룹', 5, 'private', '개인용', '2024-01-03', '2024-01-03', 'https://cram.com/invite/ghi789', null);

-- 3. 알림 더미데이터 (H2 문법 적용)
INSERT INTO `알림` (`id`, `사용자_id`, `발신자_ID`, `제목`, `알림_내용`, `대상_ID`, `대상_타입`, `읽음_여부`, `생성일시`, `알림_종류`) VALUES

-- 사용자 1의 알림들 (최근 2주 내)
(1, 1, 2, '그룹 가입 신청', '''알고리즘 스터디'' 그룹의 가입 신청이 승인되었습니다.', 1, 'group', false, DATEADD('DAY', -1, CURRENT_TIMESTAMP), 'group'),
(2, 1, 3, '새로운 댓글', '당신의 게시글 ''이진 탐색 알고리즘 정리''에 새로운 댓글이 달렸습니다.', 1, 'post', false, DATEADD('DAY', -2, CURRENT_TIMESTAMP), 'comment'),
(3, 1, 2, '좋아요 알림', '홍길동님이 당신의 게시글을 좋아합니다.', 1, 'post', true, DATEADD('DAY', -3, CURRENT_TIMESTAMP), 'like'),
(4, 1, null, '시스템 공지', '서버 정기 점검이 예정되어 있습니다.', null, 'system', false, DATEADD('DAY', -5, CURRENT_TIMESTAMP), 'system'),
(5, 1, 3, '멘션 알림', '@김철수님이 댓글에서 당신을 언급했습니다.', 2, 'comment', false, DATEADD('DAY', -7, CURRENT_TIMESTAMP), 'mention'),
(6, 1, 2, '그룹 활동', '알고리즘 스터디에 새로운 멤버가 가입했습니다.', 1, 'group', true, DATEADD('DAY', -10, CURRENT_TIMESTAMP), 'group'),

-- 사용자 2의 알림들
(7, 2, 1, '그룹 초대', '''Spring Boot 스터디'' 그룹에 초대되었습니다.', 2, 'group', false, DATEADD('DAY', -1, CURRENT_TIMESTAMP), 'group'),
(8, 2, 3, '새로운 댓글', '당신의 게시글에 새로운 댓글이 달렸습니다.', 2, 'post', true, DATEADD('DAY', -4, CURRENT_TIMESTAMP), 'comment'),
(9, 2, null, '문의 답변', '고객센터 문의에 대한 답변이 등록되었습니다.', 1, 'inquiry', false, DATEADD('DAY', -6, CURRENT_TIMESTAMP), 'inquiry_reply'),
(10, 2, 1, '좋아요 알림', '김철수님이 당신의 댓글을 좋아합니다.', 3, 'comment', false, DATEADD('DAY', -8, CURRENT_TIMESTAMP), 'like'),
(11, 2, 3, '멘션 알림', '@이영희님이 게시글에서 당신을 언급했습니다.', 4, 'post', false, DATEADD('DAY', -12, CURRENT_TIMESTAMP), 'mention'),

-- 사용자 3의 알림들
(12, 3, 1, '그룹 가입 승인', '''개인 프로젝트'' 그룹 가입이 승인되었습니다.', 3, 'group', true, DATEADD('DAY', -2, CURRENT_TIMESTAMP), 'group'),
(13, 3, 2, '새로운 게시글', '관심 있는 그룹에 새로운 게시글이 올라왔습니다.', 3, 'post', false, DATEADD('DAY', -3, CURRENT_TIMESTAMP), 'group'),
(14, 3, null, '시스템 알림', '프로필 정보를 업데이트해주세요.', null, 'system', false, DATEADD('DAY', -9, CURRENT_TIMESTAMP), 'system'),
(15, 3, 1, '댓글 좋아요', '김철수님이 당신의 댓글을 좋아합니다.', 5, 'comment', true, DATEADD('DAY', -11, CURRENT_TIMESTAMP), 'like'),

-- 오래된 알림들 (2주 이전 - API에서 필터링되어야 함)
(16, 1, 2, '오래된 알림', '이 알림은 2주 이전 것입니다.', 1, 'post', true, DATEADD('DAY', -20, CURRENT_TIMESTAMP), 'comment'),
(17, 2, 1, '오래된 그룹 알림', '오래된 그룹 관련 알림입니다.', 1, 'group', false, DATEADD('DAY', -25, CURRENT_TIMESTAMP), 'group'),

-- 추가 테스트용 안읽은 알림들
(18, 1, 3, '최신 알림 1', '방금 전에 온 알림입니다.', 2, 'post', false, DATEADD('HOUR', -2, CURRENT_TIMESTAMP), 'comment'),
(19, 1, 2, '최신 알림 2', '오늘 오전에 온 알림입니다.', 1, 'group', false, DATEADD('HOUR', -6, CURRENT_TIMESTAMP), 'group'),
(20, 2, 3, '최신 댓글', '새로운 댓글이 달렸습니다.', 6, 'post', false, DATEADD('HOUR', -1, CURRENT_TIMESTAMP), 'comment');

-- 4. 그룹 알림 상세 정보
INSERT INTO `그룹_알림` (`Key`, `알림_id`, `그룹_id`, `알림_분류`) VALUES
                                                           (1, 1, 1, 'ACCEPT'),    -- 그룹 가입 승인
                                                           (2, 6, 1, 'COMMENT'),   -- 그룹 내 새 멤버
                                                           (3, 7, 2, 'ACCEPT'),    -- 그룹 초대 승인
                                                           (4, 12, 3, 'ACCEPT'),   -- 그룹 가입 승인
                                                           (5, 13, 2, 'COMMENT'),  -- 그룹 내 새 게시글
                                                           (6, 17, 1, 'FAIL'),     -- 그룹 가입 거절 (오래된 알림)
                                                           (7, 19, 1, 'SUMMARY');  -- 그룹 요약

-- 5. 일반 알림 상세 정보
INSERT INTO `일반_알림` (`id`, `알림_id`, `알림_분류`) VALUES
    (1, 9, 'INQUIRY_REPLY');  -- 문의 답변

-- 6. 테스트용 추가 데이터 - 게시글 (알림과 연결용)
INSERT INTO `게시판_카테고리` (`id`, `스터디_그룹_id`, `게시판_유형`, `게시판_이름`) VALUES
                                                                   (1, 1, '학습', '알고리즘 문제 풀이'),
                                                                   (2, 2, '소통', '자유게시판');

INSERT INTO `게시글` (`id`, `게시판_카테고리_id`, `사용자_id`, `제목`, `내용`, `작성일시`, `삭제여부`, `조회수`) VALUES
                                                                                         (1, 1, 1, '이진 탐색 알고리즘 정리', '이진 탐색에 대해 정리해보았습니다.', DATEADD('DAY', -3, CURRENT_TIMESTAMP), false, 25),
                                                                                         (2, 1, 2, '퀵소트 구현 질문', '퀵소트를 구현하다가 막혔습니다.', DATEADD('DAY', -2, CURRENT_TIMESTAMP), false, 18),
                                                                                         (3, 2, 2, 'Spring Boot 시작하기', 'Spring Boot 프로젝트 생성부터 실행까지', DATEADD('DAY', -1, CURRENT_TIMESTAMP), false, 42);

-- 7. 알림 시스템 테스트를 위한 추가 설정
-- H2에서 자동 증가 ID 설정 (필요한 경우)
-- ALTER SEQUENCE IF EXISTS SYSTEM_SEQUENCE_알림 RESTART WITH 21;
-- ALTER SEQUENCE IF EXISTS SYSTEM_SEQUENCE_그룹_알림 RESTART WITH 8;
-- ALTER SEQUENCE IF EXISTS SYSTEM_SEQUENCE_일반_알림 RESTART WITH 2;