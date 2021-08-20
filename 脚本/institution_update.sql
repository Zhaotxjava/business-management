ALTER TABLE `yb_institution_info`
	ADD COLUMN `legal_account_id` VARCHAR(255) NULL DEFAULT NULL COMMENT '天印系统法人用户标识' AFTER `account_id`;