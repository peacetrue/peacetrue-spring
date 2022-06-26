package com.github.peacetrue.spring.data.jpa.criteria;

import com.github.peacetrue.beans.createmodify.CreateModify;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 模板实体类。
 *
 * @author peace
 */
@Getter
@Setter
@Entity
@ToString
public class Template implements Serializable, CreateModify<Long, LocalDateTime> {

    private static final long serialVersionUID = 0L;

    /** 主键 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /** 编码 */
    @Column(nullable = false, length = 50)
    private String code;
    /** 名称 */
    @Column(nullable = false, length = 50)
    private String name;
    /** 内容 */
    @Column(nullable = false)
    private String content;
    /** 备注 */
    @Column(nullable = false)
    private String remark;
    /** 创建者. 主键 */
    @CreatedBy
    @Column(nullable = false, updatable = false)
    private Long creatorId;
    /** 创建时间 */
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdTime;
    /** 修改者. 主键 */
    @LastModifiedBy
    @Column(nullable = false)
    private Long modifierId;
    /** 修改时间 */
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime modifiedTime;

}
