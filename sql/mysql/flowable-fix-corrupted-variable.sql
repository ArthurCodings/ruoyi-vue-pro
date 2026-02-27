/*
 Flowable 损坏变量修复

 问题：登录后报 FlowableException: Couldn't deserialize object in variable 'F8eimlhjt8x9ahc'
 解决：清除该变量对应的损坏数据

 用法：mysql -u root -p ruoyi-vue-pro < flowable-fix-corrupted-variable.sql
*/

USE `ruoyi-vue-pro`;

SET FOREIGN_KEY_CHECKS = 0;

DELETE ba FROM ACT_GE_BYTEARRAY ba
INNER JOIN ACT_HI_VARINST v ON ba.ID_ = v.BYTEARRAY_ID_
WHERE v.NAME_ = 'F8eimlhjt8x9ahc';

UPDATE ACT_HI_VARINST SET BYTEARRAY_ID_ = NULL, TEXT_ = NULL WHERE NAME_ = 'F8eimlhjt8x9ahc';

SET FOREIGN_KEY_CHECKS = 1;
