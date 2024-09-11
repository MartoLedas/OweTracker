-- Table: users
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    password VARCHAR NOT NULL
);

-- Table: friends
CREATE TABLE friends (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    friend_id INTEGER NOT NULL,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_friend FOREIGN KEY (friend_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Table: groups
CREATE TABLE groups (
    id SERIAL PRIMARY KEY,
    title VARCHAR NOT NULL,
    created_by INTEGER NOT NULL,
    status VARCHAR NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_created_by FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE
);

-- Table: group_memberships
CREATE TABLE group_memberships (
    id SERIAL PRIMARY KEY,
    group_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    CONSTRAINT fk_group FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE CASCADE,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Table: expenses
CREATE TABLE expenses (
    id SERIAL PRIMARY KEY,
    title VARCHAR NOT NULL,
    description VARCHAR,
    amount DOUBLE PRECISION NOT NULL,
    paid_by INTEGER NOT NULL,
    group_id INTEGER,
    status VARCHAR NOT NULL,
    type VARCHAR NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_paid_by FOREIGN KEY (paid_by) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_group_id FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE SET NULL
);

-- Table: transactions
CREATE TABLE transactions (
    id SERIAL PRIMARY KEY,
    amount DOUBLE PRECISION NOT NULL,
    from_user_id INTEGER NOT NULL,
    to_user_id INTEGER NOT NULL,
    expense_id INTEGER NOT NULL,
    status VARCHAR NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_debt BOOLEAN NOT NULL, -- true: debt, false: paying debt (transaction)
    CONSTRAINT fk_from_user FOREIGN KEY (from_user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_to_user FOREIGN KEY (to_user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_expense FOREIGN KEY (expense_id) REFERENCES expenses(id) ON DELETE CASCADE
);
