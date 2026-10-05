INSERT INTO bookings (user_id, service_name, amount, booking_time, reservation_time, status) VALUES
('user1', 'Deluxe Room - Beach Resort', 200.00, DATEADD('DAY', -10, NOW()), DATEADD('DAY', 10, NOW()), 'ACTIVE'),
('user2', 'City Tour Package', 80.00, DATEADD('DAY', -2, NOW()), DATEADD('DAY', 3, NOW()), 'ACTIVE'),
('user3', 'Airport Shuttle', 40.00, DATEADD('HOUR', -5, NOW()), DATEADD('HOUR', 10, NOW()), 'ACTIVE');
