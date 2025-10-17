CREATE TABLE IF NOT EXISTS wellness_resources (
                                                  resource_id SERIAL PRIMARY KEY,
                                                  title VARCHAR(100) NOT NULL,
    description TEXT,
    category VARCHAR(50),
    url VARCHAR(255)
    );

INSERT INTO wellness_resources (title, description, category, url)
VALUES
    ('Mindfulness Basics', 'A beginner-friendly guide to mindfulness practices.', 'mindfulness', 'https://example.com/mindfulness'),
    ('Online Counseling', 'Access licensed therapists remotely for personal sessions.', 'counseling', 'https://example.com/counseling');