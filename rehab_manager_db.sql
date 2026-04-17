/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80041
 Source Host           : localhost:3306
 Source Schema         : rehab_manager_db

 Target Server Type    : MySQL
 Target Server Version : 80041
 File Encoding         : 65001

 Date: 16/04/2026 16:56:52
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for biz_match_record
-- ----------------------------
DROP TABLE IF EXISTS `biz_match_record`;
CREATE TABLE `biz_match_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `project_id` bigint NOT NULL COMMENT '关联的项目ID',
  `talent_id` bigint NOT NULL COMMENT '匹配到的人才ID',
  `match_score` int NOT NULL COMMENT '综合匹配度得分(如：92)',
  `status` tinyint NULL DEFAULT 0 COMMENT '处理状态：0-已推荐, 1-已发送邀请, 2-已拒绝, 3-已合作',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '撮合计算时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '智能撮合历史记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of biz_match_record
-- ----------------------------

-- ----------------------------
-- Table structure for biz_project
-- ----------------------------
DROP TABLE IF EXISTS `biz_project`;
CREATE TABLE `biz_project`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '项目主键ID',
  `project_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '项目名称(如：智能外骨骼研发)',
  `publisher` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '发布方(如：某某医院/某某高校)',
  `domain` tinyint NOT NULL COMMENT '所属领域：0-计算机科学, 1-康复医学, 2-深度交叉',
  `budget` decimal(10, 2) NULL DEFAULT NULL COMMENT '项目预算(万元)',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态：0-招募中, 1-进行中, 2-已结题',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '科研/企业项目表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of biz_project
-- ----------------------------
INSERT INTO `biz_project` VALUES (1, 'qadfa', 'sgs', 2, 50.00, 0, '2026-04-16 15:38:51');

-- ----------------------------
-- Table structure for biz_project_requirement
-- ----------------------------
DROP TABLE IF EXISTS `biz_project_requirement`;
CREATE TABLE `biz_project_requirement`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `project_id` bigint NOT NULL COMMENT '关联biz_project表ID',
  `dev_req` int NULL DEFAULT 0 COMMENT '编程开发要求门槛(0-100)',
  `algo_req` int NULL DEFAULT 0 COMMENT '算法设计要求门槛(0-100)',
  `clinical_req` int NULL DEFAULT 0 COMMENT '临床评估要求门槛(0-100)',
  `physio_req` int NULL DEFAULT 0 COMMENT '生理基础要求门槛(0-100)',
  `hardware_req` int NULL DEFAULT 0 COMMENT '硬件交互要求门槛(0-100)',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '具体需求文字描述',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '项目能力需求指标表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of biz_project_requirement
-- ----------------------------
INSERT INTO `biz_project_requirement` VALUES (1, 1, 80, 70, 50, 40, 60, '需要偏向计算机开发的交叉人才');

-- ----------------------------
-- Table structure for biz_talent_achievement
-- ----------------------------
DROP TABLE IF EXISTS `biz_talent_achievement`;
CREATE TABLE `biz_talent_achievement`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `talent_id` bigint NOT NULL COMMENT '人才档案ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '成果/论文/专利名称',
  `type` tinyint NOT NULL COMMENT '类型：0-SCI论文, 1-发明专利, 2-软著, 3-其他',
  `publish_date` date NULL DEFAULT NULL COMMENT '发表/授权日期',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '证明材料链接(备用)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '科研成果表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of biz_talent_achievement
-- ----------------------------

-- ----------------------------
-- Table structure for biz_talent_education
-- ----------------------------
DROP TABLE IF EXISTS `biz_talent_education`;
CREATE TABLE `biz_talent_education`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `talent_id` bigint NOT NULL COMMENT '人才档案ID',
  `school_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学校名称',
  `degree` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学位(如：学士、硕士、博士)',
  `major` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '专业名称',
  `start_date` date NULL DEFAULT NULL COMMENT '入学日期',
  `end_date` date NULL DEFAULT NULL COMMENT '毕业日期',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '人才教育经历表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of biz_talent_education
-- ----------------------------

-- ----------------------------
-- Table structure for biz_talent_experience
-- ----------------------------
DROP TABLE IF EXISTS `biz_talent_experience`;
CREATE TABLE `biz_talent_experience`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `talent_id` bigint NOT NULL COMMENT '人才档案ID',
  `organization` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '单位/课题组名称',
  `position` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '担任职务/角色',
  `description` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '工作内容详述',
  `start_date` date NULL DEFAULT NULL COMMENT '开始日期',
  `end_date` date NULL DEFAULT NULL COMMENT '结束日期',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '人才项目履历表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of biz_talent_experience
-- ----------------------------

-- ----------------------------
-- Table structure for sys_dict_data
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data`  (
  `dict_code` bigint NOT NULL AUTO_INCREMENT COMMENT '字典编码',
  `dict_sort` int NULL DEFAULT 0 COMMENT '字典排序',
  `dict_label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '字典标签(如：博士)',
  `dict_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '字典键值(如：doctor)',
  `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '字典类型(关联sys_dict_type)',
  PRIMARY KEY (`dict_code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '字典数据表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dict_data
-- ----------------------------

-- ----------------------------
-- Table structure for sys_dict_type
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type`  (
  `dict_id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典主键',
  `dict_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '字典名称(如：系统学历列表)',
  `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '字典类型(如：sys_degree)',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态（0正常 1停用）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`dict_id`) USING BTREE,
  UNIQUE INDEX `dict_type`(`dict_type` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '字典类型表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dict_type
-- ----------------------------

-- ----------------------------
-- Table structure for sys_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作人员',
  `operation` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作模块(如: 删除人才)',
  `method` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '请求方法名',
  `params` varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '请求参数',
  `time` bigint NOT NULL COMMENT '执行时长(毫秒)',
  `ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作者IP',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统操作日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_log
-- ----------------------------
INSERT INTO `sys_log` VALUES (1, 'admin', '删除人才档案', 'com.rehab.managerv2.controller.SysTalentProfileController.delete()', '2', 14, '127.0.0.1', '2026-04-16 13:42:48');

-- ----------------------------
-- Table structure for sys_notice
-- ----------------------------
DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice`  (
  `notice_id` bigint NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `notice_title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公告标题',
  `notice_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '公告内容(可存富文本HTML)',
  `receiver_id` bigint NULL DEFAULT NULL COMMENT '接收者ID(如果是发给个人的站内信)',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态：0-未读, 1-已读',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  PRIMARY KEY (`notice_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '通知公告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_notice
-- ----------------------------

-- ----------------------------
-- Table structure for sys_talent_profile
-- ----------------------------
DROP TABLE IF EXISTS `sys_talent_profile`;
CREATE TABLE `sys_talent_profile`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NULL DEFAULT NULL COMMENT '关联sys_user表的账号ID(暂留空)',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '人才真实姓名',
  `primary_domain` tinyint NOT NULL COMMENT '主攻领域：0-计算机科学, 1-康复医学, 2-深度交叉',
  `education_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '最高学历(如：本科, 硕士, 博士)',
  `research_direction` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '主要研究方向(如：脑机接口、运动数据分析)',
  `employment_status` tinyint NULL DEFAULT 0 COMMENT '就业状态：0-在校培养, 1-科研院所, 2-企业就职',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '建档时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '交叉人才基本档案表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_talent_profile
-- ----------------------------
INSERT INTO `sys_talent_profile` VALUES (1, NULL, '潘航宇', 0, '本科', '康复医疗信息系统开发', 0, '2026-04-15 19:05:33', '2026-04-15 19:05:33', 0);
INSERT INTO `sys_talent_profile` VALUES (3, NULL, '王研究员', 2, '博士', '基于肌电信号的智能康复辅具研发', 2, '2026-04-15 19:05:33', '2026-04-15 19:05:33', 0);
INSERT INTO `sys_talent_profile` VALUES (5, NULL, 'xuyan', 1, '硕士', '人体', 0, '2026-04-16 13:52:29', '2026-04-16 13:52:29', 0);

-- ----------------------------
-- Table structure for sys_talent_skill
-- ----------------------------
DROP TABLE IF EXISTS `sys_talent_skill`;
CREATE TABLE `sys_talent_skill`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `talent_id` bigint NOT NULL COMMENT '关联的人才档案ID',
  `dev_score` int NULL DEFAULT 0 COMMENT '编程开发(Dev)',
  `algo_score` int NULL DEFAULT 0 COMMENT '算法设计(Algo)',
  `clinical_score` int NULL DEFAULT 0 COMMENT '临床评估(Clinical)',
  `physio_score` int NULL DEFAULT 0 COMMENT '生理基础(Physio)',
  `hardware_score` int NULL DEFAULT 0 COMMENT '硬件交互(Hardware)',
  `strong_points` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '核心优势总结',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '交叉人才技能雷达分数表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_talent_skill
-- ----------------------------
INSERT INTO `sys_talent_skill` VALUES (1, 1, 95, 90, 40, 50, 70, '编程开发、算法设计');
INSERT INTO `sys_talent_skill` VALUES (2, 2, 30, 40, 95, 90, 50, '临床评估、生理基础');
INSERT INTO `sys_talent_skill` VALUES (3, 3, 88, 85, 80, 85, 88, '算法设计、硬件交互');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录账号',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录密码(加密)',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `role_type` tinyint NOT NULL DEFAULT 1 COMMENT '角色类型: 1-学生, 2-专家, 3-管理员',
  `phone` char(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `status` tinyint NULL DEFAULT 0 COMMENT '帐号状态: 0-正常, 1-停用',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '超级管理员', 3, NULL, 0, 0, '2026-04-14 15:08:14', '2026-04-14 15:08:14');
INSERT INTO `sys_user` VALUES (4, 'test02', '$2a$10$D/1.05LTE6mSBoT1T8vep.pXxufjKx00y.kpO8zxdn2/eeOz1r4Wm', '新来的医生', 2, NULL, 0, 0, '2026-04-15 10:31:54', '2026-04-15 10:31:54');

-- ----------------------------
-- Table structure for biz_rehab_motion_data (时空运动数据表)
-- ----------------------------
DROP TABLE IF EXISTS `biz_rehab_motion_data`;
CREATE TABLE `biz_rehab_motion_data`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `talent_id` bigint NOT NULL COMMENT '关联人才ID（患者或受训者）',
  `device_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '物联网设备ID',
  `motion_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '运动类型(如: 手指屈伸, 步态训练)',
  `angle_x` decimal(10, 4) NULL DEFAULT NULL COMMENT 'X轴角度(空间数据)',
  `angle_y` decimal(10, 4) NULL DEFAULT NULL COMMENT 'Y轴角度(空间数据)',
  `angle_z` decimal(10, 4) NULL DEFAULT NULL COMMENT 'Z轴角度(空间数据)',
  `force_value` decimal(10, 2) NULL DEFAULT NULL COMMENT '受力/握力值',
  `record_time` datetime(3) NOT NULL COMMENT '采样时间(包含毫秒的时态数据)',
  `location_point` point NULL COMMENT '空间地理位置数据(用于记录训练场所)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '入库时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_talent_time`(`talent_id`, `record_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '康复运动时空数据归档表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for biz_training_plan (培训计划表)
-- ----------------------------
DROP TABLE IF EXISTS `biz_training_plan`;
CREATE TABLE `biz_training_plan`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `plan_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '计划名称',
  `target_domain` tinyint NOT NULL COMMENT '目标领域：0-计算机科学, 1-康复医学, 2-深度交叉',
  `difficulty_level` tinyint NULL DEFAULT 1 COMMENT '难度等级：1-初级, 2-中级, 3-高级',
  `content_desc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '计划内容描述',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态：0-启用, 1-停用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '智能培训计划推荐表' ROW_FORMAT = Dynamic;

INSERT INTO `biz_training_plan` VALUES (1, 'Spring Boot 康复系统开发基础', 0, 1, '涵盖Java基础与SpringBoot在医疗系统的应用', 0, '2026-04-16 10:00:00');
INSERT INTO `biz_training_plan` VALUES (2, '人体肌电信号分析与临床评估', 1, 2, '学习如何通过肌电信号进行精准临床康复评估', 0, '2026-04-16 10:00:00');
INSERT INTO `biz_training_plan` VALUES (3, '深度交叉：脑机接口硬件与算法协同设计', 2, 3, '针对交叉领域的高级课程，涵盖硬件与AI算法', 0, '2026-04-16 10:00:00');
INSERT INTO `biz_training_plan` VALUES (4, 'Vue3+ECharts 医疗数据三维可视化', 0, 2, '前端3D大屏与医疗数据结合的实战开发', 0, '2026-04-16 10:00:00');

-- ----------------------------
-- Table structure for biz_talent_profile_history (数据版本控制归档表)
-- ----------------------------
DROP TABLE IF EXISTS `biz_talent_profile_history`;
CREATE TABLE `biz_talent_profile_history`  (
  `history_id` bigint NOT NULL AUTO_INCREMENT COMMENT '历史记录主键',
  `profile_id` bigint NOT NULL COMMENT '原档案ID',
  `old_real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '旧姓名',
  `old_primary_domain` tinyint NULL DEFAULT NULL COMMENT '旧主攻领域',
  `old_education_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '旧学历',
  `old_research_direction` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '旧研究方向',
  `version_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '版本产生时间',
  `action_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'UPDATE' COMMENT '操作类型',
  PRIMARY KEY (`history_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '人才档案版本历史表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Trigger for data version control (触发器实现数据版本控制)
-- ----------------------------
DROP TRIGGER IF EXISTS `trg_talent_profile_update`;
DELIMITER ;;
CREATE TRIGGER `trg_talent_profile_update` AFTER UPDATE ON `sys_talent_profile` FOR EACH ROW BEGIN
    IF OLD.real_name != NEW.real_name OR OLD.primary_domain != NEW.primary_domain OR OLD.education_level != NEW.education_level OR OLD.research_direction != NEW.research_direction THEN
        INSERT INTO `biz_talent_profile_history` (
            `profile_id`, `old_real_name`, `old_primary_domain`, `old_education_level`, `old_research_direction`, `action_type`
        ) VALUES (
            OLD.id, OLD.real_name, OLD.primary_domain, OLD.education_level, OLD.research_direction, 'UPDATE'
        );
    END IF;
END
;;
DELIMITER ;

SET FOREIGN_KEY_CHECKS = 1;
