package com.swapServer.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.swapServer.bean.base.AbstractBean;
import com.swapServer.constants.OperationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Tolerate;
import org.springframework.util.CollectionUtils;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @Data :  2021/3/9 17:20
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */
@Data
@TableName("systemLog")
@AllArgsConstructor
public class SystemLog extends AbstractBean {

    @Tolerate
    public SystemLog() {
    }

    @TableId(value = "id", type = IdType.AUTO)
    private long id;

    @Enumerated(EnumType.STRING)
    @TableField(value = "type")
    private OperationType type;

    @TableField(value = "detail")
    private String detail;

    public static SystemLog builder() {
        return new SystemLog();
    }

    public SystemLog type(OperationType type) {
        this.type = type;
        return this;
    }

    public SystemLog detail(@NotNull Item item) {
        if (CollectionUtils.isEmpty(item.itemDetails)) {
            return this;
        }
        StringBuilder sb = new StringBuilder(type.getAlias());
        switch (item.itemType) {
            case create:
                sb.append(":【");
                item.itemDetails.forEach(itemDetail -> {
                    sb.append(itemDetail.getValue1() + "/" + itemDetail.getValue2() + ", ");
                });
                this.detail = sb.substring(0, sb.lastIndexOf(",")) + "】";
                break;
            case delete:
                sb.append(":【");
                sb.append(item.itemDetails.get(0).getValue1());
                sb.append("】");
                this.detail = sb.toString();
                break;
            case update:
                sb.append("由:【");
                item.itemDetails.stream()
                        .filter(Objects::nonNull)
                        .forEach(itemDetail -> {
                            sb.append(String.format("%s改为%s / ", itemDetail.getValue1(), itemDetail.getValue2()));
                        });
                this.detail = sb.substring(0, sb.lastIndexOf("/")) + "】";
                break;
        }
        return this;
    }

    public SystemLog build() {
        return new SystemLog(this.detail, this.type);
    }

    public SystemLog(String detail, OperationType type) {
        this.type = type;
        this.detail = detail;
    }

    @Data
    @NoArgsConstructor
    public static class Item {

        @NotNull
        private ItemType itemType;

        private List<ItemDetail> itemDetails;

        public static Item builder() {
            return new Item();
        }

        public Item itemType(ItemType itemType) {
            this.itemType = itemType;
            return this;
        }

        public Item itemDetails(ItemDetail... itemDetails) {
            this.itemDetails = Arrays.asList(itemDetails);
            return this;
        }

        public Item build() {
            return new Item(this.itemDetails, this.itemType);
        }

        public Item(List<ItemDetail> itemDetails, ItemType itemType) {
            this.itemType = itemType;
            this.itemDetails = itemDetails;
        }

    }

    /**
     * 具体参数记录
     * 针对不同操作含义不同
     * 1) 增 value1:key  value2:value  example 姓名:张三
     * 2) 删 value1:主要标识 example 张三
     * 3) 改 value1:oldValue  value2:newValue example 张三改为李四
     */
    @Data
    @Builder
    public static class ItemDetail {

        private String value1;

        private String value2;
    }

    /**
     * 数据库操作枚举（增，删，改）
     */
    public enum ItemType {
        update, create, delete;
    }
}
