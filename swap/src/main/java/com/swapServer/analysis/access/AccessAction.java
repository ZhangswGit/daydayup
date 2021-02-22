package com.swapServer.analysis.access;

import com.swapServer.analysis.Analyzed;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccessAction implements Analyzed {

    @Override
    public String id() {
        return "AccessAction";
    }

    String ip;
}
