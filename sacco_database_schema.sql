-- =============================================================================
-- SACCO MOBILE APPLICATION DATABASE SCHEMA (SQLite / Room)
-- Course: STU 3102 - Mobile Application Development
-- =============================================================================

CREATE TABLE IF NOT EXISTS `users` (
    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `email` TEXT UNIQUE NOT NULL,
    `password_hash` TEXT NOT NULL,
    `salt` TEXT NOT NULL,
    `role` TEXT NOT NULL, -- 'MEMBER' or 'ADMIN'
    `created_at` TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS `members` (
    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `user_id` INTEGER NOT NULL,
    `member_number` TEXT UNIQUE NOT NULL,
    `full_name` TEXT NOT NULL,
    `national_id` TEXT UNIQUE NOT NULL,
    `phone_number` TEXT NOT NULL,
    `gender` TEXT NOT NULL,
    `date_of_birth` TEXT NOT NULL,
    `address` TEXT NOT NULL,
    `profile_image_path` TEXT,
    `status` TEXT NOT NULL, -- 'PENDING', 'APPROVED', 'REJECTED'
    FOREIGN KEY(`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS `admins` (
    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `user_id` INTEGER NOT NULL,
    `full_name` TEXT NOT NULL,
    `department` TEXT NOT NULL,
    FOREIGN KEY(`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS `sacco_products` (
    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `product_name` TEXT NOT NULL,
    `interest_rate` REAL NOT NULL,
    `min_amount` REAL NOT NULL,
    `max_amount` REAL NOT NULL,
    `max_duration_months` INTEGER NOT NULL,
    `calculation_type` TEXT NOT NULL -- 'FLAT' or 'REDUCING_BALANCE'
);

CREATE TABLE IF NOT EXISTS `savings` (
    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `member_id` INTEGER UNIQUE NOT NULL,
    `balance` REAL NOT NULL,
    `total_contributions` REAL NOT NULL,
    `last_deposit_date` TEXT NOT NULL,
    FOREIGN KEY(`member_id`) REFERENCES `members`(`id`) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS `loans` (
    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `member_id` INTEGER NOT NULL,
    `product_id` INTEGER NOT NULL,
    `loan_number` TEXT UNIQUE NOT NULL,
    `requested_amount` REAL NOT NULL,
    `interest_rate` REAL NOT NULL,
    `total_repayment` REAL NOT NULL,
    `monthly_installment` REAL NOT NULL,
    `duration_months` INTEGER NOT NULL,
    `purpose` TEXT NOT NULL,
    `repayment_frequency` TEXT NOT NULL,
    `amount_repaid` REAL NOT NULL,
    `outstanding_balance` REAL NOT NULL,
    `status` TEXT NOT NULL, -- 'PENDING', 'APPROVED', 'REJECTED', 'ACTIVE', 'FULLY_PAID'
    `applied_date` TEXT NOT NULL,
    `approved_date` TEXT,
    FOREIGN KEY(`member_id`) REFERENCES `members`(`id`) ON DELETE CASCADE,
    FOREIGN KEY(`product_id`) REFERENCES `sacco_products`(`id`) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS `loan_repayments` (
    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `loan_id` INTEGER NOT NULL,
    `member_id` INTEGER NOT NULL,
    `amount_paid` REAL NOT NULL,
    `payment_date` TEXT NOT NULL,
    `payment_method` TEXT NOT NULL,
    `reference_code` TEXT NOT NULL,
    FOREIGN KEY(`loan_id`) REFERENCES `loans`(`id`) ON DELETE CASCADE,
    FOREIGN KEY(`member_id`) REFERENCES `members`(`id`) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS `transactions` (
    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `transaction_code` TEXT UNIQUE NOT NULL,
    `member_id` INTEGER NOT NULL,
    `type` TEXT NOT NULL, -- 'DEPOSIT', 'WITHDRAWAL', 'LOAN_DISBURSEMENT', 'LOAN_REPAYMENT', 'SHARE_CONTRIBUTION', 'TRANSFER'
    `amount` REAL NOT NULL,
    `date` TEXT NOT NULL,
    `time` TEXT NOT NULL,
    `description` TEXT NOT NULL,
    `status` TEXT NOT NULL,
    FOREIGN KEY(`member_id`) REFERENCES `members`(`id`) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS `shares` (
    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `member_id` INTEGER UNIQUE NOT NULL,
    `total_shares` INTEGER NOT NULL,
    `share_value` REAL NOT NULL,
    `total_contribution` REAL NOT NULL,
    `min_required_shares` INTEGER NOT NULL,
    `status` TEXT NOT NULL, -- 'COMPLIANT' or 'BELOW_MINIMUM'
    FOREIGN KEY(`member_id`) REFERENCES `members`(`id`) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS `notifications` (
    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `member_id` INTEGER NOT NULL,
    `title` TEXT NOT NULL,
    `message` TEXT NOT NULL,
    `timestamp` TEXT NOT NULL,
    `is_read` INTEGER NOT NULL, -- 0 for false, 1 for true
    FOREIGN KEY(`member_id`) REFERENCES `members`(`id`) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS `announcements` (
    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `title` TEXT NOT NULL,
    `content` TEXT NOT NULL,
    `date_posted` TEXT NOT NULL,
    `posted_by` TEXT NOT NULL
);

-- INDEXES
CREATE INDEX IF NOT EXISTS `index_members_user_id` ON `members` (`user_id`);
CREATE INDEX IF NOT EXISTS `index_admins_user_id` ON `admins` (`user_id`);
CREATE INDEX IF NOT EXISTS `index_savings_member_id` ON `savings` (`member_id`);
CREATE INDEX IF NOT EXISTS `index_loans_member_id` ON `loans` (`member_id`);
CREATE INDEX IF NOT EXISTS `index_loans_product_id` ON `loans` (`product_id`);
CREATE INDEX IF NOT EXISTS `index_loan_repayments_loan_id` ON `loan_repayments` (`loan_id`);
CREATE INDEX IF NOT EXISTS `index_loan_repayments_member_id` ON `loan_repayments` (`member_id`);
CREATE INDEX IF NOT EXISTS `index_transactions_member_id` ON `transactions` (`member_id`);
CREATE INDEX IF NOT EXISTS `index_shares_member_id` ON `shares` (`member_id`);
CREATE INDEX IF NOT EXISTS `index_notifications_member_id` ON `notifications` (`member_id`);
