INSERT INTO caju_credit_card_authorizer.users (id, name, created_at, updated_at)
VALUES ('1', 'John Doe', NOW(), NOW());

INSERT INTO caju_credit_card_authorizer.accounts (id, user_id, food_balance, cash_balance, meal_balance, created_at, updated_at)
VALUES ('1', '1', 100, 50, 80, NOW(), NOW());