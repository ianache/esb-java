package com.crossnetcorp.processors.whiteblacklist;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data @Builder  
public class CIDRDataItem {
    private List<String> cidrs;
    private String ip;
    private Boolean expected;
}
