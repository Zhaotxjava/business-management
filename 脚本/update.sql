ALTER TABLE `yb_flow_info`
	ADD COLUMN `file_key` VARCHAR(200) NOT NULL AFTER `flow_type`;
