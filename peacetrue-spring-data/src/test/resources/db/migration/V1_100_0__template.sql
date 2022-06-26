DROP TABLE IF EXISTS `template`;
CREATE TABLE `template`
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL COMMENT '主键',
    code          VARCHAR(64)                       NOT NULL COMMENT '编码',
    name          VARCHAR(64)                       NOT NULL COMMENT '名称',
    content       VARCHAR(64)                       NOT NULL COMMENT '内容',
    remark        VARCHAR(255)                      NOT NULL COMMENT '备注',
    creator_id    BIGINT                            NOT NULL COMMENT '创建者. 主键',
    created_time  DATETIME                          NOT NULL COMMENT '创建时间',
    modifier_id   BIGINT                            NOT NULL COMMENT '修改者. 主键',
    modified_time DATETIME                          NOT NULL COMMENT '修改时间'
) COMMENT '模板';

