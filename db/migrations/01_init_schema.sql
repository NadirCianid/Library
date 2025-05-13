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
    is_available BOOLEAN   DEFAULT TRUE,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Создание таблицы избранных книг пользователей
CREATE TABLE IF NOT EXISTS user_favorites
(
    user_id INTEGER REFERENCES users (id),
    book_id INTEGER REFERENCES books (id),
    is_read BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (user_id, book_id)
);

-- Создание индексов для улучшения производительности
CREATE INDEX IF NOT EXISTS idx_books_title ON books (title);
CREATE INDEX IF NOT EXISTS idx_books_author ON books (author);
CREATE INDEX IF NOT EXISTS idx_books_genre ON books (genre);


-- Начальные данные для таблицы books
INSERT INTO books (title, author, year, genre, url, is_available) VALUES
                                                                      ('1984', 'George Orwell', 1949, 'Dystopian', 'https://www.planetebook.com/free-ebooks/1984.pdf', true),
                                                                      ('The Idiot', 'Fyodor Dostoevsky', 1869, 'Fiction', 'https://www.planetebook.com/free-ebooks/the-idiot.pdf', true),
                                                                      ('To Kill a Mockingbird', 'Harper Lee', 1960, 'Fiction', 'https://giove.isti.cnr.it/demo/eread/libri/angry/mockingbird.pdf', true),
                                                                      ('Brave New World', 'Aldous Huxley', 1932, 'Dystopian', 'https://gutenberg.ca/ebooks/huxleya-bravenewworld/huxleya-bravenewworld-00-e.html', true)
ON CONFLICT (title, author) DO NOTHING;

-- Начальные данные для таблицы users
INSERT INTO users (username, password, is_admin) VALUES
    ('admin', 'admin123', true)
ON CONFLICT (username) DO NOTHING;