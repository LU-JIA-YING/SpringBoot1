-- --------------------------------------------------------
-- 主機:                           127.0.0.1
-- 伺服器版本:                        10.8.3-MariaDB - mariadb.org binary distribution
-- 伺服器作業系統:                      Win64
-- HeidiSQL 版本:                  11.3.0.6295
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- 傾印 spring 的資料庫結構
CREATE DATABASE IF NOT EXISTS `spring` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;
USE `spring`;

-- 傾印  資料表 spring.mgn_cashi 結構
CREATE TABLE IF NOT EXISTS `mgn_cashi` (
  `CASHI_MGNI_ID` char(20) NOT NULL COMMENT '申請主檔 ID',
  `CASHI_ACC_NO` char(7) NOT NULL COMMENT '存入結算帳戶帳號',
  `CASHI_CCY` char(3) NOT NULL COMMENT '幣別',
  `CASHI_AMT` decimal(20,4) NOT NULL COMMENT '金額',
  PRIMARY KEY (`CASHI_MGNI_ID`,`CASHI_ACC_NO`,`CASHI_CCY`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='銀行入金申請現金明細檔';

-- 取消選取資料匯出。

-- 傾印  資料表 spring.mgn_mgni 結構
CREATE TABLE IF NOT EXISTS `mgn_mgni` (
  `MGNI_ID` char(20) NOT NULL COMMENT '申請ID',
  `MGNI_TIME` datetime NOT NULL COMMENT '存入日期(填單時間)',
  `MGNI_TYPE` char(1) NOT NULL COMMENT '存入類型',
  `MGNI_CM_NO` char(7) NOT NULL COMMENT '結算會員代號',
  `MGNI_KAC_TYPE` char(1) NOT NULL COMMENT '存入保管專戶別',
  `MGNI_BANK_NO` char(3) NOT NULL COMMENT '存入結算銀行代碼',
  `MGNI_CCY` char(3) NOT NULL COMMENT '存入幣別',
  `MGNI_PV_TYPE` char(1) NOT NULL COMMENT '存入方式',
  `MGNI_BICACC_NO` char(21) NOT NULL COMMENT '實體帳號/虛擬帳號',
  `MGNI_I_TYPE` char(1) DEFAULT NULL COMMENT '存入類別',
  `MGNI_P_REASON` varchar(50) DEFAULT NULL COMMENT '存入實體帳號原因',
  `MGNI_AMT` decimal(20,4) NOT NULL COMMENT '總存入金額',
  `MGNI_CT_NAME` varchar(120) NOT NULL COMMENT '聯絡人姓名',
  `MGNI_CT_TEL` varchar(30) NOT NULL COMMENT '聯絡人電話',
  `MGNI_STATUS` char(1) NOT NULL COMMENT '申請狀態',
  `MGNI_U_TIME` datetime NOT NULL COMMENT '更新時間',
  PRIMARY KEY (`MGNI_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='銀行入金券申請主檔';

-- 取消選取資料匯出。

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
