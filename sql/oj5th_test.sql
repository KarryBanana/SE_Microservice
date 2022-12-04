/*
 Navicat Premium Data Transfer

 Source Server         : Local
 Source Server Type    : MySQL
 Source Server Version : 50731
 Source Host           : localhost:3306
 Source Schema         : oj5th_test

 Target Server Type    : MySQL
 Target Server Version : 50731
 File Encoding         : 65001

 Date: 25/04/2021 19:44:22
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for apply_course
-- ----------------------------
DROP TABLE IF EXISTS `apply_course`;
CREATE TABLE `apply_course`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` int(11) UNSIGNED NOT NULL,
  `course_id` int(11) UNSIGNED NOT NULL,
  `info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `apply_time` datetime(0) NULL DEFAULT NULL,
  `is_agree` tinyint(4) NOT NULL,
  `deal_person` int(11) UNSIGNED NULL DEFAULT NULL,
  `deal_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  INDEX `course_id`(`course_id`) USING BTREE,
  INDEX `deal_person`(`deal_person`) USING BTREE,
  CONSTRAINT `apply_course_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `apply_course_ibfk_2` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `apply_course_ibfk_3` FOREIGN KEY (`deal_person`) REFERENCES `user` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of apply_course
-- ----------------------------

-- ----------------------------
-- Table structure for authentication
-- ----------------------------
DROP TABLE IF EXISTS `authentication`;
CREATE TABLE `authentication`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` int(11) UNSIGNED NULL DEFAULT NULL,
  `student_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `real_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  CONSTRAINT `authentication_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of authentication
-- ----------------------------

-- ----------------------------
-- Table structure for class
-- ----------------------------
DROP TABLE IF EXISTS `class`;
CREATE TABLE `class`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `_order` int(11) UNSIGNED NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of class
-- ----------------------------

-- ----------------------------
-- Table structure for course
-- ----------------------------
DROP TABLE IF EXISTS `course`;
CREATE TABLE `course`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `access` enum('private','protect','public','verifying') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'public',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `class_id` int(11) UNSIGNED NULL DEFAULT NULL,
  `creator_id` int(11) UNSIGNED NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `class_id`(`class_id`) USING BTREE,
  INDEX `creator_id`(`creator_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course
-- ----------------------------
INSERT INTO `course` VALUES (1, '2015级-软件学院-C++程序设计', 'public', '一门重要的课', '1970-01-02 00:00:00', 1, 2);
INSERT INTO `course` VALUES (2, '2014级-软件学院-算法分析与设计', 'public', '2014级算法上机小组，请各位2014级的同学在个人信息中填好学号并将用户昵称改为真实姓名。', '1970-01-02 00:00:00', 2, 2);
INSERT INTO `course` VALUES (3, 'other-只有超级管理员能点进去的小组', 'private', '这个小组只有超级管理员', '1970-01-02 00:00:00', 10, 2);
INSERT INTO `course` VALUES (4, 'other-软件学院-C++兴趣小组', 'public', 'C++ 兴趣小组实施方案\r\n\r\n一、	说明\r\n        兴趣小组为对C++感兴趣的所有同学开放，不限年级。经批准进入小组的成员可同时拥有出题和做题的权利。\r\n        兴趣小组无硬性过题要求，对于做题和出题的数量质量较好的同学会有C++课程最终成绩的加分。\r\n\r\n二、	小组成员选拔方案\r\n        同学们自愿申请加入C++兴趣小组，经审核通过后即可加入该小组。\r\n        审核条件：爱好编程\r\n        审核人员：宋老师、助教、欣导、耿导等。对兴趣小组感兴趣的同学欢迎申请兴趣小组管理员身份，发送申请至 lovebiancheng_xqxz@163.com。\r\n\r\n\r\n\r\n三、	实施方案\r\n        1.	出题\r\n        兴趣小组内所有成员均可在小组内出题（具有出题权限），出题之后经管理员审核即可发布到该小组的题目中，组内所有成员均可看到该题并做题。\r\n        若该题具有一定价值和水平，将会由超级管理员审核且得到出题者同意后将该题公开，即所有小组所有成员均可见且可做该题。（将好题的价值最大化）\r\n\r\n        注：由于对出题人员不做限定要求，因此所有组内成员所出题目均需有一定实际意义和学习价值，故意扰乱组内秩序滥用出题权限随意出题者一经发现将会被管理员提出警告，情节严重者将直接踢出兴趣小组并进入biancheng.love网站黑名单。\r\n        有一定实际意义和学习价值的定义为：与biancheng.love中全员公开的已有题目不重复，题目设计有一定难度或有亮点。\r\n\r\n2.	建议出题来源\r\n        建议出题来源包括：\r\n        ●      OJ3RD 上的经典老题\r\n        ●      自己出的原创新题\r\n        ●      其他编程网站的好题\r\n        建议将曾经在OJ3RD上看到过的、还未出现在biancheng.love上的题目搬迁过来，重新出题发布到biancheng.love上。经典好题的价值永远存在。\r\n        鼓励自创新题，但要确保题目的合理性。 \r\n        若将来源于OJ3RD和其他oj的题目搬到biancheng.love上供大家学习时，一定要注意题目来源的平台是否允许转载，如允许，出题时须在题目描述的开头注明题目来源。	\r\n\r\n3.	出题要求\r\n        出题需登录admin.biancheng.love，帐号密码与biancheng.love一致\r\n        出题需要包括：\r\n        ●      题目描述\r\n        ●      题目来源（据具体题目而定，非本站题目必须标明来源）\r\n        ●      输入\r\n        ●      输出\r\n        ●      输入样例\r\n        ●      输出样例\r\n        ●      标签（为题目分类）\r\n        ●      难度\r\n        ●      测试数据\r\n        ●      提示（可选）\r\n        出题之后，出题者需要提交一份完整的解题报告（内容及格式要求参见每次上机助教版解题报告），发送至 lovebiancheng_xqxz@163.com。\r\n        对于兴趣小组的管理制度和内容建设有任何意见或者建议也欢迎发送至 lovebiancheng_xqxz@163.com 。\r\n\r\n\r\n4.	奖惩措施\r\n\r\n	奖励：\r\n	兴趣小组的组内成员会依据其在兴趣小组内出题做题的数量质量对其给予等比例的算法/C++最终成绩加分。（重点考虑将OJ3RD上经典老题搬迁至biancheng.love 的部分）\r\n	对兴趣小组的发展和建设有较大贡献的成员可获得一定物质奖励。\r\n\r\n	惩罚：\r\n	兴趣小组组内成员若存在滥用出题权随意出题者一经发现将提出警告，情节严重者直接踢出该小组。\r\n', '1970-01-02 00:00:00', 10, 2);

-- ----------------------------
-- Table structure for course_problem
-- ----------------------------
DROP TABLE IF EXISTS `course_problem`;
CREATE TABLE `course_problem`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `course_id` int(11) UNSIGNED NOT NULL,
  `problem_id` int(11) UNSIGNED NOT NULL,
  `create_user` int(11) UNSIGNED NOT NULL,
  `create_time` datetime(0) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `course_id`(`course_id`) USING BTREE,
  INDEX `problem_id`(`problem_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3572 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course_problem
-- ----------------------------
INSERT INTO `course_problem` VALUES (3566, 1, 4, 0, '0000-00-00 00:00:00');
INSERT INTO `course_problem` VALUES (3567, 1, 5, 0, '0000-00-00 00:00:00');
INSERT INTO `course_problem` VALUES (3568, 3, 6, 0, '0000-00-00 00:00:00');
INSERT INTO `course_problem` VALUES (3569, 2, 13, 0, '0000-00-00 00:00:00');
INSERT INTO `course_problem` VALUES (3570, 1, 11, 0, '0000-00-00 00:00:00');
INSERT INTO `course_problem` VALUES (3571, 1, 1, 1, '2021-03-25 11:07:51');

-- ----------------------------
-- Table structure for exam
-- ----------------------------
DROP TABLE IF EXISTS `exam`;
CREATE TABLE `exam`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `start_time` datetime(0) NOT NULL,
  `end_time` datetime(0) NOT NULL,
  `create_time` datetime(0) NOT NULL,
  `update_time` datetime(0) NOT NULL,
  `access` enum('public','protected','private') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'public',
  `creator_id` int(11) UNSIGNED NOT NULL,
  `course_id` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of exam
-- ----------------------------
INSERT INTO `exam` VALUES (1, 'test', '测试比赛', '2021-04-07 11:42:19', '2021-04-08 11:42:22', '2021-04-07 11:42:26', '2021-04-07 11:42:29', 'public', 1, 1);
INSERT INTO `exam` VALUES (2, 'test2', '测试rank的比赛', '2021-04-22 19:00:00', '2021-04-22 22:00:00', '2021-04-22 20:11:59', '2021-04-22 20:12:01', 'public', 1, 1);

-- ----------------------------
-- Table structure for exam_problem
-- ----------------------------
DROP TABLE IF EXISTS `exam_problem`;
CREATE TABLE `exam_problem`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `exam_id` int(11) UNSIGNED NOT NULL,
  `problem_id` int(11) UNSIGNED NOT NULL,
  `order` int(255) NOT NULL,
  `creator_id` int(11) UNSIGNED NOT NULL,
  `score` int(11) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `exam_id`(`exam_id`, `order`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of exam_problem
-- ----------------------------
INSERT INTO `exam_problem` VALUES (1, 1, 1, 4, 1, 100);
INSERT INTO `exam_problem` VALUES (2, 1, 2, 3, 1, 100);
INSERT INTO `exam_problem` VALUES (3, 1, 3, 1, 1, 100);
INSERT INTO `exam_problem` VALUES (4, 1, 4, 2, 1, 100);
INSERT INTO `exam_problem` VALUES (5, 2, 1, 1, 1, 100);
INSERT INTO `exam_problem` VALUES (6, 2, 2, 3, 1, 100);
INSERT INTO `exam_problem` VALUES (7, 2, 3, 5, 1, 100);
INSERT INTO `exam_problem` VALUES (8, 2, 4, 2, 1, 100);
INSERT INTO `exam_problem` VALUES (9, 2, 5, 4, 1, 100);

-- ----------------------------
-- Table structure for issue
-- ----------------------------
DROP TABLE IF EXISTS `issue`;
CREATE TABLE `issue`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `content` varchar(4095) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `create_time` datetime(0) NOT NULL,
  `creator_id` int(11) UNSIGNED NOT NULL,
  `problem_id` int(11) UNSIGNED NULL DEFAULT NULL,
  `exam_id` int(11) UNSIGNED NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of issue
-- ----------------------------

-- ----------------------------
-- Table structure for language
-- ----------------------------
DROP TABLE IF EXISTS `language`;
CREATE TABLE `language`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of language
-- ----------------------------

-- ----------------------------
-- Table structure for membership
-- ----------------------------
DROP TABLE IF EXISTS `membership`;
CREATE TABLE `membership`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` int(11) UNSIGNED NOT NULL,
  `course_id` int(11) UNSIGNED NOT NULL,
  `type` enum('creator','admin','member') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'member',
  `create_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  INDEX `course_id`(`course_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21524 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of membership
-- ----------------------------
INSERT INTO `membership` VALUES (21515, 1, 1, 'member', NULL);
INSERT INTO `membership` VALUES (21516, 2, 2, 'member', NULL);
INSERT INTO `membership` VALUES (21517, 3, 2, 'member', NULL);
INSERT INTO `membership` VALUES (21518, 3, 1, 'member', NULL);
INSERT INTO `membership` VALUES (21519, 1, 3, 'member', NULL);
INSERT INTO `membership` VALUES (21520, 2, 1, 'member', NULL);
INSERT INTO `membership` VALUES (21521, 4, 1, 'member', NULL);
INSERT INTO `membership` VALUES (21522, 5, 1, 'member', NULL);
INSERT INTO `membership` VALUES (21523, 6, 1, 'member', NULL);

-- ----------------------------
-- Table structure for problem
-- ----------------------------
DROP TABLE IF EXISTS `problem`;
CREATE TABLE `problem`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `access` enum('public','protect','private') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'public',
  `difficulity` double NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `creator_id` int(11) UNSIGNED NULL DEFAULT NULL,
  `time_limit` int(11) NOT NULL,
  `memory_limit` int(11) NOT NULL,
  `is_special_judge` int(11) NOT NULL,
  `setting` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `title`(`title`) USING BTREE,
  INDEX `creator_id`(`creator_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of problem
-- ----------------------------
INSERT INTO `problem` VALUES (1, 'test a+b', '## 题目描述\r\n计算 $a+b$\r\n## 输入\r\n第一行，一个正整数$n$，表示数据组数。\r\n\r\n接下来$n$行，每行2个整数$a, b$（保证$a, b,  a+b$均在int范围内）。\r\n## 输出\r\n对于每组数据，输出一行，为 $a+b$ 的值。\r\n## 输入样例\r\n    2\r\n    1 2\r\n    2 3\r\n## 输出样例\r\n    3\r\n    5\r\n\r\n## 代码样例 \r\n    #include <stdio.h>\r\n    int main()\r\n    {\r\n        int n, a, b;\r\n\r\n        scanf(\"%d\", &n);\r\n\r\n        while(n--){\r\n            scanf(\"%d%d\", &a, &b);\r\n            printf(\"%d\\n\", (a+b));\r\n        }\r\n\r\n        /* // 或用如下for循环代替while循环\r\n        int i;\r\n        for(i=0; i<n; i++){\r\n            scanf(\"%d%d\", &a, &b);\r\n            printf(\"%d\\n\", (a+b));\r\n        }\r\n        */\r\n\r\n        return 0;\r\n    }', 'public', 1, '1970-01-02 00:00:00', '2020-06-11 19:04:33', 2, 1000, 65536, 0, '{\"compareLevel\":2,\"isIgnoreCase\":1,\"peCompareLevel\":0,\"tabWidth\":4}');
INSERT INTO `problem` VALUES (2, '求余', '## 题目描述\r\n求两个整数的余数。\r\n## 输入\r\n两个正整数$n, m$。\r\n## 输出\r\n输出一个整数，为$n$除以$m$的余数。\r\n## 输入样例\r\n    12 5\r\n## 输出样例\r\n    2\r\n\r\n## 代码样例 \r\n    #include <stdio.h>\r\n    int main()\r\n    {\r\n        int n, m;\r\n        scanf(\"%d%d\", &n, &m);\r\n        printf(\"%d\", n%m);\r\n        return 0;\r\n    }', 'public', 1, '1970-01-02 00:00:00', '2020-12-07 15:58:14', 2, 1000, 65536, 0, '{\"compareLevel\":2,\"isIgnoreCase\":1,\"peCompareLevel\":0,\"tabWidth\":4}');
INSERT INTO `problem` VALUES (3, 'BlueFly发糖了', '## 题目描述\r\nBlueFly是一个壕，  \r\n他有一大堆的挖掘机，通过挖掘机发家致富。  \r\n他不仅是厨师，  \r\n还是黑客，  \r\n还是高级技师，  \r\n拥有十八般武艺。。。  \r\nBlueFly是个壕，他有一个妹妹叫FlySnow。(脑洞不要太大。。)   \r\nBlueFly每天都要给他的妹妹发糖吃，第一天他给他妹妹发了n个糖，以后每天发的糖的数量比前一天多m个。\r\n请问k天以后BlueFly总共发了多少块糖。(假定FlySnow都能吃完这些糖 233)\r\n## 输入\r\n输入多组数据。  \r\n每组数据只有一行，是三个正整数，分别为n，m，k，保证每个数都在int范围内。\r\n## 输出\r\n输出这k+1天中FlySnow总共吃了多少糖，保证输出结果在int范围内。\r\n## 输入样例\r\n     1 1 2\r\n     1 2 2\r\n## 输出样例\r\n    6\r\n    9\r\n## 样例解释  \r\n第一天吃糖数量为1，每天比前一天多吃一块，2天后吃糖数量为1+2*1=3块。总共吃糖的个数就是1+2+3=6。总共吃了3天，而不是2天。\r\n## 输入提示 \r\n用while(scanf(\"%d%d%d\",&a,&b,&c) != EOF)来进行多组输入的控制，输入结束的标志是EOF。', 'public', 3, '1970-01-02 00:00:00', '2019-09-21 11:38:52', 2, 1000, 65536, 0, '{\"compareLevel\":2,\"isIgnoreCase\":1,\"peCompareLevel\":0,\"tabWidth\":4}');
INSERT INTO `problem` VALUES (4, 'Kevin·Feng的正确@姿势', '## 题目描述\r\nKevin·Feng是一只萌萌哒壕。  \r\n但是有一天Kevin听说有一只更壕的壕，叫做0yang。  \r\n所以Kevin决定跟她一决高下。  \r\n但是现在遇到一个问题，就是先要at一下0yang，才能一决高下。  \r\n不过Kevin的壕前些天买了一个神奇的键盘，输入字符之前要先敲一下\"\\\"这个键。而且at操作要在双引号里面。（敲双引号的时候不需要敲击\"\\\"键）    \r\n所以Kevin应该如何敲击键盘呢？ \r\n## 输入\r\n无需输入\r\n## 输出\r\n输出一行字符。（换句话说只要你的输出跟输出样例一样就对了）\r\n## 输出样例\r\n    \"\\a\\t\\0\\y\\a\\n\\g\"', 'protect', 1, '1970-01-02 00:00:00', '2019-09-21 11:38:52', 2, 1000, 65536, 0, '{\"compareLevel\":2,\"isIgnoreCase\":1,\"peCompareLevel\":0,\"tabWidth\":4}');
INSERT INTO `problem` VALUES (5, ' jhljx上小学', '## 题目描述\r\njhljx是一个特别喜欢数学的人，精通初等数学以上的各种数学，各种积分他都很精通。。  \r\n但他唯一的缺陷就是不会算加减法。。  \r\n于是，他只能默默的回去上小学了。。Orz。。他的小学老师是LuxakyLuee。  \r\nLuxakyLuee知道这件事以后说这是病，得治啊。。  \r\nLuxakyLuee不让他做普通的加减法，因为对于特殊病人要采取特殊手段治疗吖。  \r\nLuxakyLuee给了jhljx一个数，然后让他把这个数的每一位加起来。  \r\n如果加起来的和的位数多于1位的话，就继续将这个数的每一位数字加起来，直到最后只有一个为止。  \r\n这样jhljx就能进行好多次加法运算了。他表示很开心。\r\n## 输入\r\n输入一组数据。   \r\n该组数据只有一行，为一个整数n。  \r\n(保证33.33%的n在int范围内,33.33%的n在long long范围内,33.33%的n超过long long范围)\r\n## 输出\r\n输出最后所得的结果。\r\n## 输入样例\r\n    987\r\n## 输出样例\r\n    6', 'protect', 4, '1970-01-02 00:00:00', '2019-09-21 11:38:52', 2, 1000, 65536, 0, '{\"compareLevel\":2,\"isIgnoreCase\":1,\"peCompareLevel\":0,\"tabWidth\":4}');
INSERT INTO `problem` VALUES (6, '三位数反转', '## 题目描述\r\n输入一个三位数，分离出它的百位，十位，个位，翻转后输出。\r\n## 输入\r\n多组测试数据，每组输入一个x（100≤x≤999）。\r\n\r\n## 输出\r\n每组测试数据输出一行，为反转后的数字。\r\n## 输入样例\r\n127  \r\n742  \r\n640\r\n## 输出样例\r\n721  \r\n247  \r\n46', 'protect', 3, '1970-01-02 00:00:00', '2019-09-21 11:38:52', 2, 1000, 65536, 0, '{\"compareLevel\":2,\"isIgnoreCase\":1,\"peCompareLevel\":0,\"tabWidth\":4}');
INSERT INTO `problem` VALUES (7, 'Last_Day\'s dog', '## 题目描述\r\nLastDay要去西安了。    \r\n但是LastDay的狗狗被禁止带上火车。    \r\n为了防止饿出狗命来，LastDay 机智的准备了一些狗粮 (#)。  \r\n作为强迫症，LastDay决定把狗粮摆放的整齐大方。 比如倒三角形。 Last_Day手残不会摆，是时候你上场了。\r\n## 输入\r\n输入多组数据。   \r\n每组数据只有一行，为一个正整数n（n<=30）。\r\n## 输出\r\n每组数据输出n行，为n层的倒三角形\r\n## 输入样例\r\n    2\r\n    3\r\n## 输出样例  \r\n\r\n    ###\r\n     #\r\n    #####\r\n     ###\r\n      #', 'protect', 2, '1970-01-02 00:00:00', '2019-09-21 11:38:52', 2, 1000, 65536, 0, '{\"compareLevel\":2,\"isIgnoreCase\":1,\"peCompareLevel\":0,\"tabWidth\":4}');
INSERT INTO `problem` VALUES (8, 'creeper学妹的计算题', '## 题目描述\r\ncreeper学妹有一天拿来一个表达式a/b*c+d/e/f*g*h，然后在纸上一堆数字让Lawliet做，每一组不超过1秒。Lawliet表示瞬间被秒杀了- -#所以推倒creeper学妹的任务只能交给大家来完成了。\r\n## 输入\r\n多组数据，第一行一个数T，表示有T组数据。(1<T<1000)  \r\n接下来T行，每行8个数a,b,c,d,e,f,g,h（0<=a,b,c,d,e,f,g,h<=999999999,b,e,f均不为0)表示一组数据。  \r\n保证a/b*c、d/e/f*g*h与最终结果均为整数且在int范围内。\r\n## 输出\r\n每组数据输出一行，为最终计算结果。\r\n## 输入样例\r\n1  \r\n6 2 3 9 3 1 6 2\r\n## 输出样例\r\n45\r\n## Hint\r\n此题禁用float和double  \r\nGG说long double也不能忍！！！  \r\n数据可能比较弱。\r\n整个式子其实就是(a×c)/b+(d×g×h)/(e×f)= =', 'private', 2, '1970-01-02 00:00:00', '2019-09-21 11:38:52', 2, 1000, 65536, 0, '{\"compareLevel\":2,\"isIgnoreCase\":1,\"peCompareLevel\":0,\"tabWidth\":4}');
INSERT INTO `problem` VALUES (9, '零崎的人间冒险Ⅰ', '## 题目描述\r\n零崎最近一段时间非常无聊，于是他决定进行一场冒险，然而无聊的人遇到的冒险也非常的无聊，他的冒险刚刚开始就要结束了。\r\n理由也非常的无聊，因为一个无聊的大魔王决定用一个非常有魔(wu)力(liao)的方式毁灭世界。\r\n魔王有三个具有魔(wu)力(liao)的杆，暂时称为ABC，还有n个具有魔(wu)力(liao)的大小全都不同的盘子，这些盘子按照大小顺序放在A杆上，现在魔王要用具有魔(wu)力(liao)的方式移动到C杆，移动的过程中，小的盘子仍然只能摆在大的盘子上面而不能发生错乱，否侧魔王的魔法就会失灵。\r\n然而魔王似乎想找一个无聊的人来替他完成这个魔法，而无聊的零崎也觉得这个事情非常的无聊，干脆就决定还是让你们去做。\r\n零崎也不知道这个无聊的魔王到底有多少个有魔(wu)力(liao)的盘子，所以他说多少个你们就当是多少个吧。\r\n\r\n\r\n## 输入\r\n多组数据，每组一个数字n表示魔王的盘子数。\r\n\r\n## 输出\r\n\r\n对于每组数据，输出为魔王魔法发动后盘子移动的过程，两组输出之间用空行隔开。\r\n\r\n## 输入样例\r\n    1\r\n    2\r\n## 输出样例\r\n    A to C\r\n    \r\n    A to B\r\n    A to C\r\n    B to C\r\n##Hint\r\n这个无聊的魔法还有个名字叫做传说中可以毁灭世界的汉诺塔之术。', 'public', 5, '1970-01-02 00:00:00', '2019-09-23 19:42:04', 1, 1000, 65536, 0, '{\"compareLevel\":2,\"isIgnoreCase\":1,\"peCompareLevel\":0,\"tabWidth\":4}');
INSERT INTO `problem` VALUES (10, '零崎的人间冒险Ⅱ', '## 题目描述\r\n零崎本以为他的无聊冒险马上就要结束了，然而实际上距离魔王的魔法成功发动还有很久很久，于是他的无聊冒险还可以继续……\r\n无聊的零崎需要给自己的冒险找点事做，然而实际上他的日常非常平和，如果说有什么意外的话，那就是他去打麻将了。零崎在玩一种叫做日式麻将的竞技游戏，然而无聊的零崎总是遭遇别人立直需要防守的场面。\r\n零崎在防守时，会跟打现物和搏筋兜牌两种技能，然而为了不被婊得太惨，零崎不会连续搏筋兜牌。也就是说，零崎任意两次选择中不会都是搏筋兜牌。\r\n那么对于n次舍牌，无聊的零崎会有多少种选择？\r\n因为无聊的零崎可以打很久的麻将，所以n可能很大，无聊的零崎决定只要结果对100007求模后的选择数。\r\n\r\n## 输入\r\n多组输入数据，每组一个数字n，1<=n<=Int_MAX\r\n\r\n## 输出\r\n\r\n每组一行，只需要选择种数对100007求模后的结果。\r\n\r\n## 输入样例\r\n    1\r\n    2\r\n## 输出样例\r\n    2\r\n    3\r\n##Hint\r\n    听说这个也是很简单的，不然你们推推递推公式看？\r\n    ps：\r\n    n=1:刚，怂\r\n    n=2 刚怂，怂怂，怂刚\r\n    n=3 刚怂怂，刚怂刚，怂怂怂，怂怂刚，怂刚怂\r\n    ', 'public', 5, '1970-01-02 00:00:00', '2019-09-23 19:42:04', 1, 1000, 65536, 0, '{\"compareLevel\":2,\"isIgnoreCase\":1,\"peCompareLevel\":0,\"tabWidth\":4}');
INSERT INTO `problem` VALUES (11, 'Let\'s play a game ', '## 题目描述\r\n    这是一个古老而无聊的游戏，这是一个欧几里得躺枪的游戏。\r\nNova君和LaoWang决定一分胜负。给定两个正整数a,b。Nova君和LaoWang轮流从中将较大的数字减去较小数字的整数倍（1倍，2倍等等）。并且保证每次减完不会出现负数的情况。由Nova君先手。最终在自己回合将其中一个数变为0的一放获胜。两个人智商都还行，都会采取最优策略，谁会赢呢？\r\n## 输入\r\n多组测试数据。对于每组测试数据，给出两个数字a和b（保证Int范围内）\r\n## 输出\r\n对于每组数据，输出获胜者的名字。\r\n## 输入样例\r\n    34 12\r\n    15 24\r\n## 输出样例\r\n    Nova\r\n    LaoWang\r\n', 'private', 5, '1970-01-02 00:00:00', '2019-09-23 19:42:04', 2, 1000, 65536, 0, '{\"compareLevel\":2,\"isIgnoreCase\":1,\"peCompareLevel\":0,\"tabWidth\":4}');
INSERT INTO `problem` VALUES (12, '零崎的人间冒险Ⅲ', '## 题目描述\r\n不打麻将的零崎特别的无聊，所以他又四处乱逛了。\r\n四处乱逛的无聊零崎遇到了另一个特别无聊的人，因为这个人竟然在无聊的算各种一元n次多项式a0+a1x+a2x^2+……+anx^n！这个无聊的人算的实在太慢了令零崎忍不住想开启嘲讽模式，所以现在，快来给零崎搞一个能快速计算多项式的东西吧。（其实可能也不用特别快\r\n\r\n## 输入\r\n多组输入数据。\r\n每组数据以多项式次数n开始，下一个数字为变量x，之后n+1个数字为系数a0……an。输入数据保证在int范围内\r\n\r\n## 输出\r\n每行一个结果，也许n特别大所以最后结果还是对1e6+7求模吧……\r\n## 输入样例\r\n    1 2 1 2  \r\n    3 2 1 2 3 4\r\n## 输出样例\r\n    5\r\n    49\r\n## Hint\r\n你听说过上古卷轴吗？你听说过彩虹小马吗？你听说过Horner Rule吗？', 'private', 4, '1970-01-02 00:00:00', '2019-09-23 19:42:04', 2, 1000, 65536, 0, '{\"compareLevel\":2,\"isIgnoreCase\":1,\"peCompareLevel\":0,\"tabWidth\":4}');
INSERT INTO `problem` VALUES (13, '零崎的人间冒险', '## 题目描述\r\n刚才那个算一元n次多项式的guangtou竟然说零崎的算法不够快要和零崎比一比，好吧，速度加快速度加快，比就比谁怕谁！\r\n## 输入\r\n多组输入数据。\r\n每组数据以多项式次数n开始，下一个数字为变量x，之后n+1个数字为系数a0……an。\r\n\r\n## 输出\r\n每行一个结果，也许n特别大所以最后结果还是对1e6+7求模吧……\r\n## 输入样例\r\n    1 2 1 2  \r\n    3 2 1 2 3 4\r\n## 输出样例\r\n    5\r\n    49\r\n## Hint\r\n你听说过上古卷轴吗？\r\n你听说过彩虹小马吗？\r\n你听说过Horner Rule吗？', 'protect', 4, '1970-01-02 00:00:00', '2020-09-17 21:10:57', 2, 1000, 65536, 0, '{\"compareLevel\":2,\"isIgnoreCase\":1,\"peCompareLevel\":0,\"tabWidth\":4}');
INSERT INTO `problem` VALUES (14, 'Inverse number：Reborn', '## 题目描述\r\n输入一个正整数n，随后给出一个长度为n的整数序列a1,a2,a3...an。求给定序列的逆序数。\r\n\r\n概念回顾：\r\n\r\n逆序对：数列a[1],a[2],a[3]…中的任意两个数a[i],a[j] (i<j)，如果a[i]>a[j],那么我们就说这两个数构成了一个逆序对。\r\n\r\n逆序数：一个数列中逆序对的总数。\r\n## 输入\r\n多组测试数据。对于每组测试数据，给出序列长度n，和一个长度为n的序列a1,a2,a3...an\r\n\r\n(0<n<=10^6，保证ai在int范围内)\r\n## 输出\r\n对于每组数据，输出该序列的逆序数。\r\n## 输入样例\r\n    7 \r\n    3 5 4 8 2 6 9\r\n## 输出样例\r\n    6\r\n##Hint\r\n    1、用n^2的算法是不行不行滴╮(╯_╰)╭\r\n    2、分治法', 'protect', 5, '1970-01-02 00:00:00', '2019-09-23 19:42:04', 2, 1000, 65536, 0, '{\"compareLevel\":2,\"isIgnoreCase\":1,\"peCompareLevel\":0,\"tabWidth\":4}');
INSERT INTO `problem` VALUES (15, '零崎的人间冒险Ⅳ', '## 题目描述\r\n在干掉了guangtou之后，无聊的零崎又去找事了……\r\n\r\n说起来零崎前几周学到了一个叫做Apriori算法的东西，其第一步是挑出所有出现频率大于某个给定值的数据。然而作为一个具有一定程度的强(qiǎng )迫(pò)症的人，零崎显然希望先排个序再对其子集进行操作。\r\n\r\n于是，现在的任务是简单的升序排列。\r\n## 输入\r\n多组输入数据，每组两行，第一行为一个整数n。第二行为n个整数。\r\n## 输出\r\n每组一行，输出n个整数的升序排列，两个整数之间用一个空格隔开。\r\n## 输入样例\r\n    5\r\n    6 5 2 3 1 \r\n## 输出样例\r\n    1 2 3 5 6', 'protect', 5, '1970-01-02 00:00:00', '2019-09-23 19:42:04', 2, 1000, 65536, 0, '{\"compareLevel\":2,\"isIgnoreCase\":1,\"peCompareLevel\":0,\"tabWidth\":4}');

-- ----------------------------
-- Table structure for problem_language
-- ----------------------------
DROP TABLE IF EXISTS `problem_language`;
CREATE TABLE `problem_language`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `problem_id` int(11) UNSIGNED NOT NULL,
  `language_id` int(11) UNSIGNED NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `problem_id`(`problem_id`) USING BTREE,
  INDEX `language_id`(`language_id`) USING BTREE,
  CONSTRAINT `problem_language_ibfk_1` FOREIGN KEY (`problem_id`) REFERENCES `problem` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `problem_language_ibfk_2` FOREIGN KEY (`language_id`) REFERENCES `language` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of problem_language
-- ----------------------------

-- ----------------------------
-- Table structure for problem_share
-- ----------------------------
DROP TABLE IF EXISTS `problem_share`;
CREATE TABLE `problem_share`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `problem_id` int(11) UNSIGNED NOT NULL,
  `user_id` int(11) UNSIGNED NOT NULL,
  `create_user` int(11) UNSIGNED NOT NULL,
  `create_time` datetime(0) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of problem_share
-- ----------------------------
INSERT INTO `problem_share` VALUES (1, 7, 1, 2, '2021-03-24 17:42:25');
INSERT INTO `problem_share` VALUES (2, 8, 1, 2, '2021-03-24 17:42:34');
INSERT INTO `problem_share` VALUES (3, 1, 1, 2, '2021-03-25 10:23:09');
INSERT INTO `problem_share` VALUES (4, 2, 1, 2, '2021-03-25 00:00:00');
INSERT INTO `problem_share` VALUES (5, 5, 1, 2, '2021-03-25 00:00:00');
INSERT INTO `problem_share` VALUES (6, 5, 2, 2, '2021-03-25 00:00:00');
INSERT INTO `problem_share` VALUES (7, 1, 1, 2, '2021-03-25 10:35:36');
INSERT INTO `problem_share` VALUES (8, 5, 1, 2, '2021-03-25 00:00:00');
INSERT INTO `problem_share` VALUES (9, 5, 2, 2, '2021-03-25 00:00:00');
INSERT INTO `problem_share` VALUES (10, 5, 1, 2, '2021-03-25 10:45:58');
INSERT INTO `problem_share` VALUES (11, 5, 2, 2, '2021-03-25 10:45:58');

-- ----------------------------
-- Table structure for problem_tag
-- ----------------------------
DROP TABLE IF EXISTS `problem_tag`;
CREATE TABLE `problem_tag`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `problem_id` int(11) UNSIGNED NOT NULL,
  `tag_id` int(11) UNSIGNED NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `problem_id`(`problem_id`) USING BTREE,
  INDEX `tag_id`(`tag_id`) USING BTREE,
  CONSTRAINT `problem_tag_ibfk_1` FOREIGN KEY (`problem_id`) REFERENCES `problem` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `problem_tag_ibfk_2` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of problem_tag
-- ----------------------------

-- ----------------------------
-- Table structure for question
-- ----------------------------
DROP TABLE IF EXISTS `question`;
CREATE TABLE `question`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `content` varchar(4095) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `creator_id` int(11) UNSIGNED NOT NULL,
  `create_time` datetime(0) NOT NULL,
  `exam_id` int(11) UNSIGNED NOT NULL,
  `problem_id` int(11) UNSIGNED NULL DEFAULT NULL,
  `reply` varchar(4095) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `reply_person` int(11) UNSIGNED NULL DEFAULT NULL,
  `reply_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of question
-- ----------------------------

-- ----------------------------
-- Table structure for register
-- ----------------------------
DROP TABLE IF EXISTS `register`;
CREATE TABLE `register`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `apply_role` int(11) UNSIGNED NULL DEFAULT NULL,
  `info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `register_time` datetime(0) NULL DEFAULT NULL,
  `status` tinyint(3) NOT NULL,
  `check_person` int(11) UNSIGNED NULL DEFAULT NULL,
  `check_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `apply_role`(`apply_role`) USING BTREE,
  INDEX `check_person`(`check_person`) USING BTREE,
  CONSTRAINT `register_ibfk_1` FOREIGN KEY (`apply_role`) REFERENCES `role` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `register_ibfk_2` FOREIGN KEY (`check_person`) REFERENCES `user` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of register
-- ----------------------------

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `chinese_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES (1, 'user', '普通用户');
INSERT INTO `role` VALUES (2, 'admin', '管理员');
INSERT INTO `role` VALUES (3, 'super_admin', '超级管理员');

-- ----------------------------
-- Table structure for submission
-- ----------------------------
DROP TABLE IF EXISTS `submission`;
CREATE TABLE `submission`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `language` int(10) UNSIGNED NOT NULL,
  `result` enum('WT','JG','AC','WA','CE','REG','MLE','REP','PE','TLE','IFNR','OFNR','EFNR','OE') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `score` double NOT NULL,
  `time_cost` int(11) NOT NULL,
  `memory_cost` int(11) NOT NULL,
  `detail` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `create_time` datetime(0) NOT NULL,
  `update_time` datetime(0) NOT NULL,
  `user_id` int(11) NOT NULL,
  `problem_id` int(11) NOT NULL,
  `judge_id` int(11) NOT NULL,
  `exam_id` int(11) UNSIGNED NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 48 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of submission
-- ----------------------------
INSERT INTO `submission` VALUES (1, 1, 'AC', 1, 1, 1, 'id=1', '2021-04-07 14:43:21', '0000-00-00 00:00:00', 1, 1, 1, 1);
INSERT INTO `submission` VALUES (2, 1, 'WA', 0, 1, 1, 'id=2', '2021-04-07 14:44:25', '0000-00-00 00:00:00', 1, 2, 1, 1);
INSERT INTO `submission` VALUES (3, 1, 'AC', 1, 1, 1, 'id=3', '2021-04-07 14:45:28', '0000-00-00 00:00:00', 1, 1, 1, NULL);
INSERT INTO `submission` VALUES (4, 1, 'WA', 0.5, 1, 1, 'id=4', '2021-04-07 14:46:10', '0000-00-00 00:00:00', 1, 3, 1, 1);
INSERT INTO `submission` VALUES (5, 1, 'AC', 1, 1, 1, 'id=5', '2021-04-07 14:47:46', '0000-00-00 00:00:00', 2, 1, 1, 1);
INSERT INTO `submission` VALUES (6, 1, 'AC', 1, 1, 1, 'id=6', '2021-04-07 14:48:13', '0000-00-00 00:00:00', 2, 1, 1, NULL);
INSERT INTO `submission` VALUES (7, 1, 'WA', 0, 0, 1, 'id=7', '2021-04-07 14:49:19', '0000-00-00 00:00:00', 2, 1, 1, 1);
INSERT INTO `submission` VALUES (8, 1, 'WA', 0, 1, 1, 'id=8', '2021-04-07 14:50:15', '0000-00-00 00:00:00', 2, 2, 1, NULL);
INSERT INTO `submission` VALUES (9, 1, 'CE', 0, 1, 1, 'id=9', '2021-04-07 14:51:32', '0000-00-00 00:00:00', 2, 3, 1, 1);
INSERT INTO `submission` VALUES (10, 1, 'WA', 0, 1, 1, 'id=10', '2021-04-07 14:52:15', '0000-00-00 00:00:00', 3, 1, 1, 1);
INSERT INTO `submission` VALUES (11, 1, 'AC', 1, 1, 1, 'id=11', '2021-04-07 14:53:22', '0000-00-00 00:00:00', 3, 3, 1, 1);
INSERT INTO `submission` VALUES (12, 1, 'AC', 1, 1, 1, 'id=12', '2021-04-07 15:09:45', '0000-00-00 00:00:00', 1, 3, 1, 1);
INSERT INTO `submission` VALUES (13, 1, 'AC', 1, 1, 1, 'id=13', '2021-04-22 19:10:00', '2021-04-22 20:48:16', 1, 1, 1, 2);
INSERT INTO `submission` VALUES (14, 1, 'WA', 0.7, 1, 1, 'id=14', '2021-04-22 19:08:00', '2021-04-22 20:49:07', 1, 1, 1, 2);
INSERT INTO `submission` VALUES (15, 1, 'CE', 0, 1, 1, 'id=15', '2021-04-22 19:05:00', '2021-04-22 20:50:54', 1, 1, 1, 2);
INSERT INTO `submission` VALUES (16, 1, 'AC', 1, 1, 1, 'id=16', '2021-04-22 19:30:00', '2021-04-22 20:52:37', 1, 4, 1, 2);
INSERT INTO `submission` VALUES (17, 1, 'WA', 0.1, 1, 1, 'id=17', '2021-04-22 19:20:00', '2021-04-22 20:53:25', 1, 4, 1, 2);
INSERT INTO `submission` VALUES (18, 1, 'CE', 0, 0, 0, 'id=18', '2021-04-22 19:10:26', '2021-04-22 20:54:41', 1, 4, 1, 2);
INSERT INTO `submission` VALUES (19, 1, 'WA', 0.2, 1, 1, 'id=19', '2021-04-22 19:15:12', '2021-04-22 20:55:22', 1, 4, 1, 2);
INSERT INTO `submission` VALUES (20, 1, 'WA', 0.4, 1, 1, 'id=20', '2021-04-22 19:20:06', '2021-04-22 20:56:20', 1, 4, 1, 2);
INSERT INTO `submission` VALUES (21, 1, 'WA', 0.1, 1, 1, 'id=21', '2021-04-22 20:57:09', '2021-04-22 20:57:18', 1, 2, 1, 2);
INSERT INTO `submission` VALUES (22, 1, 'WA', 0.2, 1, 1, 'id=22', '2021-04-22 19:58:14', '2021-04-22 20:58:24', 1, 2, 1, 2);
INSERT INTO `submission` VALUES (23, 1, 'WA', 0.5, 1, 1, 'id=23', '2021-04-22 19:20:00', '2021-04-22 21:03:08', 1, 2, 1, 2);
INSERT INTO `submission` VALUES (24, 1, 'WA', 0.1, 1, 1, 'id=24', '2021-04-22 19:10:34', '2021-04-22 21:03:40', 1, 2, 1, 2);
INSERT INTO `submission` VALUES (25, 1, 'CE', 0, 0, 0, 'id=25', '2021-04-22 21:04:12', '2021-04-22 21:04:15', 1, 2, 1, 2);
INSERT INTO `submission` VALUES (26, 1, 'CE', 0, 0, 0, 'id=26', '2021-04-22 21:04:59', '2021-04-22 21:05:04', 1, 5, 1, 2);
INSERT INTO `submission` VALUES (27, 1, 'CE', 0, 0, 0, 'id=27', '2021-04-22 21:05:27', '2021-04-22 21:05:29', 1, 5, 1, 2);
INSERT INTO `submission` VALUES (28, 1, 'WA', 0.3, 1, 1, 'id=28', '2021-04-22 19:20:00', '2021-04-22 21:06:25', 2, 1, 1, 2);
INSERT INTO `submission` VALUES (29, 1, 'WA', 0.1, 1, 1, 'id=29', '2021-04-22 19:10:58', '2021-04-22 21:07:05', 2, 1, 1, 2);
INSERT INTO `submission` VALUES (30, 1, 'AC', 1, 1, 1, 'id=30', '2021-04-22 19:05:00', '2021-04-22 21:07:58', 2, 2, 1, 2);
INSERT INTO `submission` VALUES (31, 1, 'WA', 0.1, 1, 1, 'id=31', '2021-04-22 19:04:00', '2021-04-22 21:08:55', 2, 2, 1, 2);
INSERT INTO `submission` VALUES (32, 1, 'WA', 0.3, 1, 1, 'id=32', '2021-04-22 19:30:41', '2021-04-22 21:09:52', 2, 5, 1, 2);
INSERT INTO `submission` VALUES (33, 1, 'WA', 0.8, 1, 1, 'id=33', '2021-04-22 20:00:00', '2021-04-22 21:11:03', 2, 5, 1, 2);
INSERT INTO `submission` VALUES (34, 1, 'WA', 0.7, 1, 1, 'id=34', '2021-04-22 21:11:29', '2021-04-22 21:11:32', 2, 5, 1, 2);
INSERT INTO `submission` VALUES (35, 1, 'AC', 1, 1, 1, 'id=35', '2021-04-22 19:05:00', '2021-04-22 21:12:58', 3, 1, 1, 2);
INSERT INTO `submission` VALUES (36, 1, 'CE', 0, 0, 0, 'id=36', '2021-04-22 21:13:48', '2021-04-22 21:13:50', 3, 4, 1, 2);
INSERT INTO `submission` VALUES (37, 1, 'CE', 0, 0, 0, 'id=37', '2021-04-22 21:14:11', '2021-04-22 21:14:13', 3, 4, 1, 2);
INSERT INTO `submission` VALUES (38, 1, 'CE', 0, 0, 0, 'id=38', '2021-04-22 21:14:47', '2021-04-22 21:14:49', 3, 4, 1, 2);
INSERT INTO `submission` VALUES (39, 1, 'WA', 0.7, 1, 1, 'id=39', '2021-04-22 19:40:00', '2021-04-22 21:15:32', 3, 2, 1, 2);
INSERT INTO `submission` VALUES (40, 1, 'WA', 0.4, 1, 1, 'id=40', '2021-04-22 21:17:50', '2021-04-22 21:17:52', 3, 2, 1, 2);
INSERT INTO `submission` VALUES (41, 1, 'WA', 0.1, 1, 1, 'id=41', '2021-04-22 19:30:00', '2021-04-22 21:18:49', 4, 1, 1, 2);
INSERT INTO `submission` VALUES (42, 1, 'AC', 1, 1, 1, 'id=42', '2021-04-22 19:40:00', '2021-04-22 21:19:59', 4, 1, 1, 2);
INSERT INTO `submission` VALUES (43, 1, 'WA', 0.3, 1, 1, 'id=43', '2021-04-22 19:20:25', '2021-04-22 21:20:37', 4, 1, 1, 2);
INSERT INTO `submission` VALUES (44, 1, 'AC', 1, 1, 1, 'id=44', '2021-04-22 19:10:00', '2021-04-22 21:21:12', 4, 4, 1, 2);
INSERT INTO `submission` VALUES (45, 1, 'WA', 0.4, 1, 1, 'id=45', '2021-04-22 19:05:05', '2021-04-22 21:22:12', 4, 4, 1, 2);
INSERT INTO `submission` VALUES (46, 1, 'WA', 0.1, 1, 1, 'id=46', '2021-04-22 19:10:00', '2021-04-22 21:22:51', 4, 2, 1, 2);
INSERT INTO `submission` VALUES (47, 1, 'WA', 0.5, 1, 1, 'id=47', '2021-04-22 19:20:44', '2021-04-23 14:04:04', 1, 1, 1, 2);

-- ----------------------------
-- Table structure for tag
-- ----------------------------
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `index_name`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tag
-- ----------------------------

-- ----------------------------
-- Table structure for temp_password
-- ----------------------------
DROP TABLE IF EXISTS `temp_password`;
CREATE TABLE `temp_password`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` int(11) UNSIGNED NOT NULL,
  `exam_id` int(11) NOT NULL,
  `temp_password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `is_available` int(11) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `user_id`(`user_id`, `temp_password`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of temp_password
-- ----------------------------
INSERT INTO `temp_password` VALUES (1, 1, 1, 'QWER', 0);

-- ----------------------------
-- Table structure for test_data
-- ----------------------------
DROP TABLE IF EXISTS `test_data`;
CREATE TABLE `test_data`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `input` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `output` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `weight` double NOT NULL DEFAULT 5,
  `problem_id` int(10) UNSIGNED NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `index_problem_id`(`problem_id`) USING BTREE,
  CONSTRAINT `foreign_key_problem_id` FOREIGN KEY (`problem_id`) REFERENCES `problem` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of test_data
-- ----------------------------

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `role_id` int(11) UNSIGNED NULL DEFAULT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `school` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `college` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `last_login` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `index_username`(`username`) USING BTREE,
  INDEX `foreign_key_role`(`role_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'test1@test.com', 'dsasdsadas', 2, NULL, NULL, NULL, NULL, NULL, '2021-03-24 17:25:33', '2021-03-24 17:25:36', NULL, NULL);
INSERT INTO `user` VALUES (2, 'test2@test.com', 'dasdad', 3, NULL, NULL, NULL, NULL, NULL, '2021-03-24 17:28:31', '2021-03-24 17:28:33', NULL, NULL);
INSERT INTO `user` VALUES (3, 'test3@test.com', 'gfdhgf', 1, NULL, NULL, NULL, NULL, NULL, '2021-03-24 17:47:19', '2021-03-24 17:47:21', NULL, NULL);
INSERT INTO `user` VALUES (4, 'test4@test.com', 'fsdfser44', 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `user` VALUES (5, 'test5@test.com', 'fsdferw', 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `user` VALUES (6, 'test6@test.com', 'retryj', 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Function structure for hasProblemPermission
-- ----------------------------
DROP FUNCTION IF EXISTS `hasProblemPermission`;
delimiter ;;
CREATE FUNCTION `hasProblemPermission`(`problemId` int,`userId` int)
 RETURNS tinyint(1)
BEGIN
	#Routine body goes here...
	declare pid int(11);
	declare flag tinyint;
	
	select p.id into pid
	from problem p,course_problem cp,membership m,problem_share ps
  where p.id = problemId 
		    and (p.access = 'public' 
             or p.creator_id = userId 
             or (userId = m.user_id 
                 and m.course_id = cp.course_id 
                 and cp.problem_id = p.id
								 and p.access != 'private')
						 or (userId = ps.user_id 
						     and ps.problem_id = p.id))
	group by p.id;
	
	if isnull(pid) = 1 then set flag = 0;
	else set flag = 1;
	end if;
	RETURN flag;
END
;;
delimiter ;

-- ----------------------------
-- Function structure for test
-- ----------------------------
DROP FUNCTION IF EXISTS `test`;
delimiter ;;
CREATE FUNCTION `test`()
 RETURNS int(11)
BEGIN
	#Routine body goes here...

	RETURN 1;
END
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
