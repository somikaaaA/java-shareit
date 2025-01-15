CREATE TABLE IF NOT EXISTS users (
  id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
  name VARCHAR(255),
  email VARCHAR(512) NOT NULL,
  CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);
CREATE TABLE IF NOT EXISTS requests (
  id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
  description TEXT NOT NULL,
           CHECK (LENGTH(description) <= 1000),
  requester INTEGER REFERENCES users(id) ON DELETE CASCADE,
  created TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS items (
  id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
  name VARCHAR(255) NOT NULL,
  description TEXT NOT NULL,
           CHECK (LENGTH(description) <= 1000),
  available BOOLEAN DEFAULT TRUE NOT NULL,
  owner INTEGER REFERENCES users(id) ON DELETE CASCADE,
  request INTEGER REFERENCES requests(id) ON DELETE SET NULL
);
CREATE TABLE IF NOT EXISTS bookings (
  id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
  start_date TIMESTAMP WITHOUT TIME ZONE,
  end_date TIMESTAMP WITHOUT TIME ZONE,
  item INTEGER REFERENCES items(id) ON DELETE CASCADE,
  booker INTEGER REFERENCES users(id) ON DELETE CASCADE,
  status VARCHAR(20) NOT NULL
);
CREATE TABLE IF NOT EXISTS comments (
  id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
  text TEXT NOT NULL,
                  CHECK (LENGTH(description) <= 1000),
  item INTEGER REFERENCES items(id) ON DELETE CASCADE,
  author INTEGER REFERENCES users(id) ON DELETE CASCADE,
  created TIMESTAMP WITHOUT TIME ZONE
);




