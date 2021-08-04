-- 导出  表 db_insurance_info.yb_institution_info 结构
CREATE TABLE IF NOT EXISTS `yb_institution_info` (
  `number` varchar(20) NOT NULL COMMENT '机构编号',
  `institution_name` varchar(100) DEFAULT NULL COMMENT '机构名称',
  `org_institution_code` varchar(128) NOT NULL COMMENT '统一社会信用代码',
  `legal_name` varchar(32) DEFAULT NULL COMMENT '机构法人姓名',
  `legal_card_type` varchar(16) DEFAULT 'IDCard' COMMENT '机构法人证件类型 Other-其他，IDCard身份证号码，Passport-中国护照，HMPass-港澳居民来往内地通行证，MTP-台胞证,默认为IDCard',
  `legal_id_card` varchar(64) DEFAULT NULL COMMENT '机构法人证件号',
  `legal_phone` varchar(11) DEFAULT NULL COMMENT '机构法人手机号',
  `contact_name` varchar(32) DEFAULT NULL COMMENT '经办人姓名',
  `contact_card_type` varchar(16) DEFAULT 'IDCard' COMMENT '经办人证件类型 Other-其他，IDCard身份证号码，Passport-中国护照，HMPass-港澳居民来往内地通行证，MTP-台胞证,默认为IDCard',
  `contact_id_card` varchar(64) DEFAULT NULL COMMENT '经办人证件号',
  `contact_phone` varchar(11) DEFAULT NULL COMMENT '经办人手机号',
  `account_id` varchar(255) DEFAULT NULL COMMENT '天印系统经办人用户标识',
  `organize_id` varchar(255) DEFAULT NULL COMMENT '天印系统机构标记',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='定点机构信息';

-- 导出  表 db_insurance_info.yb_flow_info 结构
CREATE TABLE IF NOT EXISTS `yb_flow_info` (
  `flow_id` int(11) NOT NULL AUTO_INCREMENT,
  `sign_flow_id` varchar(20) NOT NULL COMMENT '天印系统的签署流程id',
  `subject` varchar(200) NOT NULL COMMENT '流程名称',
  `flow_status` int(2) DEFAULT NULL COMMENT '流程状态（0草稿，1 签署中，2完成，3 撤销，4终止，5过 期，6删除，7拒 签，8作废，9已归 档，10预盖章）',
  `number` varchar(20) DEFAULT NULL COMMENT '机构编号',
  `unique_id` varchar(20) DEFAULT NULL COMMENT '内部用户唯一标识',
  `account_type` int(2) DEFAULT NULL COMMENT '用户类型（1:内部,2外部）',
  `sign_status` char(2) DEFAULT NULL COMMENT '签署状态（0待签 署，1签署中，2 完成，3中止/失败）',
  `initiator` varchar(200) NOT NULL COMMENT '发起人',
  `initiator_time` timestamp NULL DEFAULT NULL COMMENT '发起时间',
  `handle_time` timestamp NULL DEFAULT NULL COMMENT '最近处理时间',
  `signers` varchar(500) NOT NULL COMMENT '签署方',
  `copy_viewers` varchar(500) NOT NULL COMMENT '抄送人',
  `flow_type` varchar(20) DEFAULT NULL COMMENT 'Common-普通签署， Cancellation-作废签署',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`flow_id`)
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8 COMMENT='签署流程记录';
