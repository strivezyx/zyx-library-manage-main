-- Test data seed for performance testing
-- Safe to run multiple times (uses INSERT IGNORE and notes='perf' tag)

SET @reader_count = 200;
SET @book_count = 1000;

-- Ensure categories exist
INSERT IGNORE INTO r_category (name, amount, lend_period, effect_period, notes)
VALUES ('压测读者', 10, 30, '2030-12-31', 'perf');

INSERT IGNORE INTO b_category (name, keywords, notes)
VALUES ('压测图书', 'perf', 'perf');

-- Build a 0..999 number set without CTEs (MySQL 5.7+)
-- n = a + b*10 + c*100
-- Readers
INSERT INTO reader
  (name, category_id, sex, w_address, h_address, phone, email, create_time, notes)
SELECT
  CONCAT('压测读者', seq.n),
  (SELECT id FROM r_category WHERE name='压测读者' LIMIT 1),
  1,
  '压测工作地址',
  '压测家庭地址',
  CONCAT('139', LPAD(seq.n, 8, '0')),
  CONCAT('perf', seq.n, '@test.com'),
  NOW(),
  'perf'
FROM (
  SELECT (a.n + b.n * 10 + c.n * 100 + 1) AS n
  FROM
    (SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
     UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) a
  CROSS JOIN
    (SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
     UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) b
  CROSS JOIN
    (SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
     UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) c
) seq
WHERE seq.n <= @reader_count;

-- Books
INSERT INTO book
  (name, category_id, author, press, publish_date, price, page_number, keywords, create_time, status, notes)
SELECT
  CONCAT('压测书', seq.n),
  (SELECT id FROM b_category WHERE name='压测图书' LIMIT 1),
  '压测作者',
  '压测出版社',
  '2024-01-01',
  9.99,
  200,
  'perf',
  NOW(),
  0,
  'perf'
FROM (
  SELECT (a.n + b.n * 10 + c.n * 100 + 1) AS n
  FROM
    (SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
     UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) a
  CROSS JOIN
    (SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
     UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) b
  CROSS JOIN
    (SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
     UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) c
) seq
WHERE seq.n <= @book_count;

-- Cleanup helpers (uncomment when needed)
-- DELETE FROM lend_return WHERE notes='jmeter' OR notes='perf';
-- DELETE FROM book WHERE notes='perf';
-- DELETE FROM reader WHERE notes='perf';
-- DELETE FROM b_category WHERE notes='perf';
-- DELETE FROM r_category WHERE notes='perf';
