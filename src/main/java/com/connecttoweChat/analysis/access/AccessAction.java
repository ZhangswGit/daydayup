package com.connecttoweChat.analysis.access;

import com.connecttoweChat.analysis.Analyzed;
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
