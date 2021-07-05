CREATE TABLE `yb_institution_info` (
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
  `updateTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='定点机构信息';

CREATE TABLE `yb_flow_info` (
  `flow_id` int(11) NOT NULL AUTO_INCREMENT,
  `sign_flow_id` varchar(20) NOT NULL COMMENT '天印系统的签署流程id',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`flow_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='签署流程记录';