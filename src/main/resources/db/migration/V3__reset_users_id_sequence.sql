-- Reset the serial sequence to match the highest ID
SELECT setval(pg_get_serial_sequence('users', 'id'), (SELECT coalesce(max(id), 1) FROM users));
