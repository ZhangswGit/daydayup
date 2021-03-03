package bean;

import lombok.*;

/**
 * @Data :  2021/3/2 19:02
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwapUser {

    private long userId;

    private String userName;

    @Override
    public String toString() {
        return userName;
    }
}
