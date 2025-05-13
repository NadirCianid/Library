
DROP TABLE IF EXISTS user_favorites;
DROP TABLE IF EXISTS books_reviews;
DROP TABLE IF EXISTS book_availability;
DROP TABLE IF EXISTS users CASCADE ;
DROP TABLE IF EXISTS books CASCADE;


-- Создание таблицы пользователей
CREATE TABLE IF NOT EXISTS users
(
    id         SERIAL PRIMARY KEY,
    username   VARCHAR(50) UNIQUE NOT NULL,
    password   VARCHAR(100)       NOT NULL,
    is_admin   BOOLEAN   DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Создание таблицы книг
CREATE TABLE IF NOT EXISTS books
(
    id           SERIAL PRIMARY KEY,
    title        VARCHAR(255) NOT NULL,
    author       VARCHAR(255) NOT NULL,
    year         INTEGER      NOT NULL,
    genre        VARCHAR(100),
    url          VARCHAR(512),
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT book_unique UNIQUE (title, author)
);

-- Создание таблицы избранных книг пользователей
CREATE TABLE IF NOT EXISTS user_favorites
(
    user_id INTEGER REFERENCES users (id),
    book_id INTEGER REFERENCES books (id),
    status  VARCHAR(100) DEFAULT 'PLAN',
    PRIMARY KEY (user_id, book_id)
);

-- Отзывы пользователей
CREATE TABLE IF NOT EXISTS books_reviews
(
    user_id   INTEGER REFERENCES users (id) ON DELETE CASCADE,
    book_id   INTEGER REFERENCES books (id) ON DELETE CASCADE,
    is_read   BOOLEAN NOT NULL DEFAULT FALSE,
    review_date TIMESTAMP,
    rating    SMALLINT CHECK (rating BETWEEN 1 AND 5),
    review     TEXT,
    PRIMARY KEY (user_id, book_id)
);

-- Доступность книг
CREATE TABLE IF NOT EXISTS book_availability
(
    book_id      INTEGER PRIMARY KEY REFERENCES books (id) ON DELETE CASCADE,
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    last_updated TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    updated_by   INTEGER REFERENCES users (id)
);

-- Создание индексов для улучшения производительности
CREATE INDEX IF NOT EXISTS idx_books_title ON books (title);
CREATE INDEX IF NOT EXISTS idx_books_author ON books (author);
CREATE INDEX IF NOT EXISTS idx_books_genre ON books (genre);
CREATE INDEX IF NOT EXISTS idx_availability ON book_availability (is_available);
CREATE INDEX IF NOT EXISTS idx_read_status ON books_reviews (is_read);


-- Начальные данные для таблицы users (6 пользователей)
INSERT INTO users (username, password, is_admin)
VALUES ('admin', 'admin123', true),
       ('john_doe', 'password123', false),
       ('jane_smith', 'qwerty456', false),
       ('mike_johnson', 'secure789', false),
       ('sarah_williams', 'pass1234', false),
       ('alex_brown', 'brownie22', false)
ON CONFLICT (username) DO NOTHING;

-- Начальные данные для таблицы books (6 книг)
INSERT INTO books (title, author, year, genre, url)
VALUES ('1984', 'George Orwell', 1949, 'Dystopian', 'https://www.planetebook.com/free-ebooks/1984.pdf'),
       ('The Idiot', 'Fyodor Dostoevsky', 1869, 'Fiction', 'https://www.planetebook.com/free-ebooks/the-idiot.pdf'),
       ('To Kill a Mockingbird', 'Harper Lee', 1960, 'Fiction', 'https://giove.isti.cnr.it/demo/eread/libri/angry/mockingbird.pdf'),
       ('Brave New World', 'Aldous Huxley', 1932, 'Dystopian', 'https://gutenberg.ca/ebooks/huxleya-bravenewworld/huxleya-bravenewworld-00-e.html'),
       ('Pride and Prejudice', 'Jane Austen', 1813, 'Romance', 'https://www.gutenberg.org/files/1342/1342-h/1342-h.htm'),
       ('The Great Gatsby', 'F. Scott Fitzgerald', 1925, 'Classic', 'https://www.gutenberg.org/files/64317/64317-h/64317-h.htm')
ON CONFLICT (title, author) DO NOTHING;

-- Начальные данные для таблицы book_availability (6 записей)
INSERT INTO book_availability (book_id, is_available, updated_by)
VALUES (1, true, 1),
       (2, false, 1),
       (3, true, 1),
       (4, true, 1),
       (5, false, 1),
       (6, true, 1)
ON CONFLICT (book_id) DO NOTHING;

-- Начальные данные для таблицы user_favorites (6 записей)
INSERT INTO user_favorites (user_id, book_id, status)
VALUES (2, 1, 'PLAN'),
       (2, 3, 'READING'),
       (3, 2, 'DROPPED'),
       (4, 4, 'COMPLETE'),
       (5, 5, 'PLAN'),
       (6, 6, 'READING')
ON CONFLICT (user_id, book_id) DO NOTHING;

-- Начальные данные для таблицы read_books (6 записей)
INSERT INTO books_reviews (user_id, book_id, is_read, review_date, rating, review)
VALUES (2, 1, true, '2025-01-15', 5, 'Отличная книга, рекомендую!'),
       (3, 2, true, '2025-02-20', 4, 'Сложный, но интересный роман'),
       (4, 3, false, '2025-03-20', 5, 'Планирую прочитать'),
       (5, 4, true, '2025-03-10', 3, 'Интересная антиутопия'),
       (6, 5, true, '2025-04-05', 5, 'Любимая книга с детства'),
       (2, 6, true, '2025-05-12', 4, 'Классика американской литературы')
ON CONFLICT (user_id, book_id) DO NOTHING;