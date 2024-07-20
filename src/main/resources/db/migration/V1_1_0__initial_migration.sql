-- Account Table
CREATE TABLE IF NOT EXISTS accounts (
                                        id VARCHAR(255) PRIMARY KEY NOT NULL,
                                        food_balance DOUBLE PRECISION NOT NULL,
                                        cash_balance DOUBLE PRECISION NOT NULL,
                                        meal_balance DOUBLE PRECISION NOT NULL,
                                        created_at TIMESTAMP NOT NULL,
                                        updated_at TIMESTAMP NOT NULL
);

-- Transaction Table
CREATE TABLE IF NOT EXISTS transactions (
                                            id VARCHAR(255) PRIMARY KEY NOT NULL,
                                            account_id VARCHAR(255) NOT NULL,
                                            amount DOUBLE PRECISION NOT NULL,
                                            mcc CHAR(4) NOT NULL,
                                            merchant VARCHAR(255) NOT NULL,
                                            created_at TIMESTAMP NOT NULL,
                                            updated_at TIMESTAMP NOT NULL,
                                            CONSTRAINT fk_transaction_account FOREIGN KEY (account_id) REFERENCES accounts (id)
);